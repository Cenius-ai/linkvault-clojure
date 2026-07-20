(ns linkvault.core
  "LinkVault — a simple server-rendered bookmark manager.
   Entry point and Compojure route definitions."
  (:require [ring.adapter.jetty            :as jetty]
            [ring.middleware.params         :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.nested-params  :refer [wrap-nested-params]]
            [ring.middleware.flash          :refer [wrap-flash]]
            [ring.middleware.session        :refer [wrap-session]]
            [ring.middleware.resource       :refer [wrap-resource]]
            [ring.util.response             :as resp]
            [compojure.core                 :refer [defroutes GET POST context]]
            [compojure.route                :refer [not-found]]
            [linkvault.db                   :as db]
            [linkvault.seed                 :as seed]
            [linkvault.views                :as views]))

;; ── initialize ────────────────────────────────────────────────

(defn init!
  "Run once on startup: create schema, seed if empty."
  []
  (println "Initializing database schema…")
  (db/init-schema!)
  (seed/seed!))

;; ── security middleware ───────────────────────────────────────

(defn wrap-security-headers
  "Add baseline security headers to every response."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (resp/header "X-Content-Type-Options" "nosniff")
          (resp/header "X-Frame-Options" "DENY")
          (resp/header "Referrer-Policy" "strict-origin-when-cross-origin")
          (resp/header "Content-Security-Policy" "default-src 'self'; style-src 'self' https://fonts.googleapis.com; font-src https://fonts.gstatic.com")))))

;; ── route handlers ────────────────────────────────────────────

(defn home-handler [_req]
  (resp/redirect "/bookmarks"))

(defn list-bookmarks-handler [req]
  (let [bookmarks (db/all-bookmarks)
        flash     (get-in req [:flash :message])]
    (views/bookmark-list bookmarks (when flash {:flash flash}))))

(defn new-bookmark-form-handler [_req]
  (views/bookmark-form :action "/bookmarks" :legend "Add Bookmark"))

(defn create-bookmark-handler [req]
  (let [{:keys [url title tags]} (:params req)
        tag-names (when tags (clojure.string/split tags #"\s*,\s*"))]
    (db/insert-bookmark! url title tag-names)
    (-> (resp/redirect "/bookmarks")
        (assoc :flash {:message "Bookmark added."}))))

(defn edit-bookmark-form-handler [req]
  (let [id       (get-in req [:params :id])
        bookmark (db/get-bookmark id)]
    (if bookmark
      (views/bookmark-form :action (str "/bookmarks/" id)
                           :legend "Edit Bookmark"
                           :bookmark bookmark)
      (-> (resp/redirect "/bookmarks")
          (assoc :flash {:message "Bookmark not found."})))))

(defn update-bookmark-handler [req]
  (let [id        (get-in req [:params :id])
        {:keys [url title tags]} (:params req)
        tag-names (when tags (clojure.string/split tags #"\s*,\s*"))]
    (db/update-bookmark! id url title tag-names)
    (-> (resp/redirect "/bookmarks")
        (assoc :flash {:message "Bookmark updated."}))))

(defn delete-bookmark-handler [req]
  (let [id (get-in req [:params :id])]
    (db/delete-bookmark! id)
    (-> (resp/redirect "/bookmarks")
        (assoc :flash {:message "Bookmark deleted."}))))

(defn tags-index-handler [_req]
  (views/tag-list (db/all-tags)))

(defn tag-detail-handler [req]
  (let [tag-name  (get-in req [:params :name])
        bookmarks (db/bookmarks-by-tag tag-name)]
    (views/tag-detail tag-name bookmarks)))

(defn about-handler [_req]
  (views/about-page))

;; ── routes ────────────────────────────────────────────────────

(defroutes app-routes
  (GET  "/"                     [] home-handler)
  (GET  "/bookmarks"            [] list-bookmarks-handler)
  (GET  "/bookmarks/new"        [] new-bookmark-form-handler)
  (POST "/bookmarks"            [] create-bookmark-handler)
  (GET  "/bookmarks/:id/edit"   [] edit-bookmark-form-handler)
  (POST "/bookmarks/:id"        [] update-bookmark-handler)
  (POST "/bookmarks/:id/delete" [] delete-bookmark-handler)
  (GET  "/tags"                 [] tags-index-handler)
  (GET  "/tags/:name"           [] tag-detail-handler)
  (GET  "/about"                [] about-handler)
  (not-found "Page not found."))

;; ── middleware stack ──────────────────────────────────────────

(def app
  (-> app-routes
      wrap-keyword-params
      wrap-nested-params
      wrap-params
      (wrap-resource "public")
      wrap-flash
      wrap-session
      wrap-security-headers))

;; ── server start ──────────────────────────────────────────────

(defn -main
  "Entry point. Init DB + seed, then start Jetty."
  [& _args]
  (init!)
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println (str "LinkVault starting on http://0.0.0.0:" port))
    (jetty/run-jetty app {:host "0.0.0.0" :port port :join? true})))
