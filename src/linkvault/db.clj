(ns linkvault.db
  "SQLite database connection and schema management."
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec
  "Lazy database spec — resolved at connect time so env vars are available."
  (delay
    (let [db-path (or (System/getenv "DB_PATH") "linkvault.db")]
      {:classname   "org.sqlite.JDBC"
       :subprotocol "sqlite"
       :subname     db-path})))

(defn connect
  "Return the resolved db-spec map."
  []
  @db-spec)

(defn init-schema!
  "Create tables if they don't already exist. Idempotent."
  []
  (let [c (connect)]
    (jdbc/query c ["PRAGMA journal_mode=WAL"])
    (jdbc/db-do-commands
     c
     [(str "CREATE TABLE IF NOT EXISTS bookmark ("
           "  id          INTEGER PRIMARY KEY AUTOINCREMENT,"
           "  url         TEXT    NOT NULL,"
           "  title       TEXT    NOT NULL,"
           "  created_at  TEXT    NOT NULL DEFAULT (datetime('now')),"
           "  updated_at  TEXT    NOT NULL DEFAULT (datetime('now'))"
           ")")
      (str "CREATE TABLE IF NOT EXISTS tag ("
           "  id   INTEGER PRIMARY KEY AUTOINCREMENT,"
           "  name TEXT    UNIQUE NOT NULL"
           ")")
      (str "CREATE TABLE IF NOT EXISTS bookmark_tag ("
           "  bookmark_id INTEGER NOT NULL REFERENCES bookmark(id) ON DELETE CASCADE,"
           "  tag_id      INTEGER NOT NULL REFERENCES tag(id)      ON DELETE CASCADE,"
           "  PRIMARY KEY (bookmark_id, tag_id)"
           ")")
      "CREATE INDEX IF NOT EXISTS ix_bookmark_tag_bid ON bookmark_tag(bookmark_id)"
      "CREATE INDEX IF NOT EXISTS ix_bookmark_tag_tid ON bookmark_tag(tag_id)"
      "CREATE INDEX IF NOT EXISTS ix_bookmark_created ON bookmark(created_at)"
      "CREATE INDEX IF NOT EXISTS ix_tag_name ON tag(name)"])))

;; ── helpers ───────────────────────────────────────────────────

(defn- inserted-id
  "Extract the auto-generated id from a jdbc/insert! result row.
   SQLite returns {:last_insert_rowid() N}."
  [row]
  (or (:id row)
      (get row (keyword "last_insert_rowid()"))
      (first (vals row))))

(defn- tag-id
  "Return the id for tag name, creating it if needed. Uses the given connection."
  [conn tag-name]
  (let [rows (vec (jdbc/query conn ["SELECT id FROM tag WHERE name = ?" tag-name]))]
    (if (seq rows)
      (:id (first rows))
      (inserted-id (first (vec (jdbc/insert! conn :tag {:name tag-name})))))))

;; ── bookmark CRUD ─────────────────────────────────────────────

(defn all-bookmarks
  "Return all bookmarks with their tags as a comma-joined string."
  []
  (jdbc/query (connect)
    ["SELECT b.id, b.url, b.title, b.created_at, b.updated_at,
             GROUP_CONCAT(t.name, ', ') AS tags
       FROM bookmark b
       LEFT JOIN bookmark_tag bt ON bt.bookmark_id = b.id
       LEFT JOIN tag t ON t.id = bt.tag_id
      GROUP BY b.id
      ORDER BY b.created_at DESC"]))

(defn get-bookmark
  "Return a single bookmark by id with its tags."
  [id]
  (first (jdbc/query (connect)
    ["SELECT b.id, b.url, b.title, b.created_at, b.updated_at,
             GROUP_CONCAT(t.name, ', ') AS tags
       FROM bookmark b
       LEFT JOIN bookmark_tag bt ON bt.bookmark_id = b.id
       LEFT JOIN tag t ON t.id = bt.tag_id
      WHERE b.id = ?
      GROUP BY b.id" (Integer/parseInt (str id))])))

(defn insert-bookmark!
  "Insert a bookmark with a set of tags. Returns the new bookmark id."
  [url title tag-names]
  (jdbc/with-db-transaction [tx (connect)]
    (let [result  (jdbc/insert! tx :bookmark {:url url :title title})
          bm-id   (inserted-id (first result))
          tids    (doall
                   (for [name tag-names
                         :let [n (clojure.string/trim name)]
                         :when (not (clojure.string/blank? n))]
                     (tag-id tx n)))]
      (doseq [tid tids]
        (jdbc/insert! tx :bookmark_tag {:bookmark_id bm-id :tag_id tid}))
      bm-id)))

(defn update-bookmark!
  "Update bookmark fields and replace its tag set. Uses a transaction."
  [id url title tag-names]
  (jdbc/with-db-transaction [tx (connect)]
    (let [id-int (Integer/parseInt (str id))]
      (jdbc/update! tx :bookmark
        {:url url :title title :updated_at (java.time.LocalDateTime/now)}
        ["id = ?" id-int])
      (jdbc/delete! tx :bookmark_tag ["bookmark_id = ?" id-int])
      (let [tids (doall
                  (for [name tag-names
                        :let [n (clojure.string/trim name)]
                        :when (not (clojure.string/blank? n))]
                    (tag-id tx n)))]
        (doseq [tid tids]
          (jdbc/insert! tx :bookmark_tag {:bookmark_id id-int :tag_id tid}))))))

(defn delete-bookmark!
  "Delete a bookmark and its tag associations."
  [id]
  (let [id-int (Integer/parseInt (str id))]
    (jdbc/delete! (connect) :bookmark_tag ["bookmark_id = ?" id-int])
    (jdbc/delete! (connect) :bookmark ["id = ?" id-int])))

;; ── tags ──────────────────────────────────────────────────────

(defn all-tags
  "Return all distinct tags with bookmark counts."
  []
  (jdbc/query (connect)
    ["SELECT t.id, t.name, COUNT(bt.bookmark_id) AS bookmark_count
        FROM tag t
   LEFT JOIN bookmark_tag bt ON bt.tag_id = t.id
    GROUP BY t.id
    ORDER BY t.name"]))

(defn bookmarks-by-tag
  "Return bookmarks that have the given tag name."
  [tag-name]
  (jdbc/query (connect)
    ["SELECT b.id, b.url, b.title, b.created_at, b.updated_at,
             GROUP_CONCAT(t2.name, ', ') AS tags
       FROM bookmark b
       JOIN bookmark_tag bt ON bt.bookmark_id = b.id
       JOIN tag t ON t.id = bt.tag_id
       LEFT JOIN bookmark_tag bt2 ON bt2.bookmark_id = b.id
       LEFT JOIN tag t2 ON t2.id = bt2.tag_id
      WHERE t.name = ?
      GROUP BY b.id
      ORDER BY b.created_at DESC" tag-name]))

(defn table-empty?
  "Check if a table is empty."
  [table]
  (zero? (:count (first (jdbc/query (connect)
                         [(str "SELECT COUNT(*) AS count FROM " (name table))])))))
