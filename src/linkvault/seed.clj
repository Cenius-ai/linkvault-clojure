(ns linkvault.seed
  "Seed sample bookmarks and tags on first run."
  (:require [linkvault.db :as db]))

(def sample-bookmarks
  "Realistic sample bookmarks with tags. Cedric Nius is a full-stack developer
   learning Clojure and functional programming."
  [{:url   "https://clojure.org"
    :title "Clojure — The Joy of Functional Programming"
    :tags  ["clojure" "functional-programming" "language"]}
   {:url   "https://clojurescript.org"
    :title "ClojureScript — Clojure on the JVM and JS"
    :tags  ["clojure" "clojurescript" "frontend"]}
   {:url   "https://github.com/nickgieschen/clojure-style-guide"
    :title "Clojure Style Guide"
    :tags  ["clojure" "style-guide" "best-practices"]}
   {:url   "https://practical.li/clojure/"
    :title "Practicalli Clojure — Free Online Guides"
    :tags  ["clojure" "learning" "tutorial"]}
   {:url   "https://ring-clojure.github.io/ring/"
    :title "Ring — Clojure HTTP Server Abstraction"
    :tags  ["clojure" "ring" "web"]}
   {:url   "https://github.com/weavejester/compojure"
    :title "Compojure — A Concise Routing Library for Ring"
    :tags  ["clojure" "compojure" "web" "routing"]}
   {:url   "https://github.com/weavejester/hiccup"
    :title "Hiccup — Fast HTML Rendering in Clojure"
    :tags  ["clojure" "hiccup" "html" "templating"]}
   {:url   "https://sqlite.org"
    :title "SQLite — Small, Fast, Self-Contained SQL Database"
    :tags  ["sqlite" "database" "sql"]}
   {:url   "https://htmx.org"
    :title "htmx — High Power Tools for HTML"
    :tags  ["frontend" "htmx" "html"]}
   {:url   "https://tailwindcss.com"
    :title "Tailwind CSS — Utility-First CSS Framework"
    :tags  ["css" "tailwind" "frontend"]}
   {:url   "https://developer.mozilla.org"
    :title "MDN Web Docs — Resources for Developers"
    :tags  ["reference" "web" "documentation"]}
   {:url   "https://news.ycombinator.com"
    :title "Hacker News — Technology and Startup News"
    :tags  ["news" "tech" "community"]}])

(defn seed!
  "Insert sample bookmarks if the bookmark table is empty."
  []
  (println "Checking for existing bookmarks…")
  (if (db/table-empty? :bookmark)
    (do
      (println "Seeding" (count sample-bookmarks) "sample bookmarks…")
      (doseq [{:keys [url title tags]} sample-bookmarks]
        (db/insert-bookmark! url title (map clojure.string/trim tags)))
      (println "Seed complete."))
    (println "Bookmarks already present — skipping seed.")))
