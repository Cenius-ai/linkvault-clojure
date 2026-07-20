(ns linkvault.views
  "Hiccup-based server-side HTML rendering."
  (:require [hiccup.page :as page]
            [hiccup.core :refer [h]]
            [hiccup.form  :as form]))

;; ── design tokens ─────────────────────────────────────────────
;; committed direction: editorial, accent oklch(0.58 0.16 267) / #5073d8,
;; Plus Jakarta Sans, soft-dark warm-slate surface, left-rail layout.

;; ── layout helpers ────────────────────────────────────────────

(defn- escape-html
  "Escape user-provided strings for HTML context."
  [s]
  (h s))

(defn layout
  "Base HTML shell with left-rail nav + main content area."
  [title & body]
  (page/html5
   {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
    [:link {:href "https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:ital,wght@0,400;0,500;0,600;0,700;1,400&display=swap"
            :rel "stylesheet"}]
    [:link {:rel "stylesheet" :href "/css/style.css"}]
    [:title (str (escape-html title) " — LinkVault")]]
   [:body
    [:div.app-shell
     [:nav.sidebar
      [:a.sidebar-brand {:href "/"} "LinkVault"]
      [:ul.nav-links
       [:li [:a {:href "/bookmarks"} "Bookmarks"]]
       [:li [:a {:href "/bookmarks/new"} "Add Bookmark"]]
       [:li [:a {:href "/tags"} "Tags"]]
       [:li [:a {:href "/about"} "About"]]]
      [:div.sidebar-footer
       [:p "A clean bookmark manager."]]]
     [:main.main-content
      [:h1.page-title (escape-html title)]
      body]]]))

;; ── partials ──────────────────────────────────────────────────

(defn tag-link
  "Render a single tag as a small pill link."
  [tag-name]
  [:a.tag-pill {:href (str "/tags/" (java.net.URLEncoder/encode tag-name "UTF-8"))}
   (escape-html tag-name)])

(defn tag-links
  "Render comma-separated tag string as linked pills."
  [tags-str]
  (when (and tags-str (not (clojure.string/blank? tags-str)))
    (let [tags (clojure.string/split tags-str #",\s*")]
      (interpose " " (map tag-link tags)))))

(defn flash-message
  "Render a flash message if present in params."
  [params]
  (when-let [msg (get params :flash)]
    [:div.flash-message (escape-html msg)]))

;; ── bookmark list ─────────────────────────────────────────────

(defn bookmark-list
  "Render the full bookmark list page."
  [bookmarks & [params]]
  (layout "Bookmarks"
    (flash-message params)
    (if (seq bookmarks)
      [:div.bookmark-table-wrapper
       [:table.bookmark-table
        [:thead
         [:tr
          [:th "Title"]
          [:th "URL"]
          [:th "Tags"]
          [:th "Actions"]]]
        [:tbody
         (for [bm bookmarks]
           [:tr
            [:td [:a.bookmark-title-link {:href (:url bm) :target "_blank" :rel "noopener"}
                  (escape-html (:title bm))]]
            [:td.url-cell [:code (escape-html (:url bm))]]
            [:td.tags-cell (tag-links (:tags bm))]
            [:td.actions-cell
             [:a.btn-edit {:href (str "/bookmarks/" (:id bm) "/edit")} "Edit"]]])]]]
      [:div.empty-state
       [:p "No bookmarks yet."]
       [:p [:a {:href "/bookmarks/new"} "Add your first bookmark →"]]])))

;; ── bookmark form ─────────────────────────────────────────────

(defn bookmark-form
  "Render the add/edit bookmark form."
  [& {:keys [action legend bookmark]}]
  (layout legend
    [:form.bookmark-form {:method "POST" :action action}
     (when (= "Edit Bookmark" legend)
       [:input {:type "hidden" :name "_method" :value "put"}])
     [:div.form-group
      [:label {:for "url"} "URL"]
      [:input#url {:type "url" :name "url" :required true
                   :placeholder "https://example.com"
                   :value (:url bookmark "")}]]
     [:div.form-group
      [:label {:for "title"} "Title"]
      [:input#title {:type "text" :name "title" :required true
                     :placeholder "Page title"
                     :value (:title bookmark "")}]]
     [:div.form-group
      [:label {:for "tags"} "Tags"]
      [:input#tags {:type "text" :name "tags"
                    :placeholder "clojure, web, programming"
                    :value (:tags bookmark "")}]
      [:small.form-hint "Comma-separated tag names."]]
     [:div.form-actions
      [:button.btn-primary {:type "submit"} "Save"]
      [:a.btn-cancel {:href "/bookmarks"} "Cancel"]]]))

;; ── tag list ──────────────────────────────────────────────────

(defn tag-list
  "Render the tag index page."
  [tags]
  (layout "Tags"
    (if (seq tags)
      [:div.tag-cloud
       (for [t tags]
         [:a.tag-card {:href (str "/tags/" (java.net.URLEncoder/encode (:name t) "UTF-8"))}
          [:span.tag-name (escape-html (:name t))]
          [:span.tag-count (str (:bookmark_count t) " bookmark" (when (not= 1 (:bookmark_count t)) "s"))]])]
      [:div.empty-state
       [:p "No tags yet."]])))

;; ── tag detail ────────────────────────────────────────────────

(defn tag-detail
  "Render bookmarks for a specific tag."
  [tag-name bookmarks]
  (layout (str "Tag: " tag-name)
    [:p.tag-breadcrumb "Bookmarks tagged with " [:strong (escape-html tag-name)]]
    (if (seq bookmarks)
      [:div.bookmark-table-wrapper
       [:table.bookmark-table
        [:thead
         [:tr
          [:th "Title"]
          [:th "URL"]
          [:th "Tags"]
          [:th "Actions"]]]
        [:tbody
         (for [bm bookmarks]
           [:tr
            [:td [:a.bookmark-title-link {:href (:url bm) :target "_blank" :rel "noopener"}
                  (escape-html (:title bm))]]
            [:td.url-cell [:code (escape-html (:url bm))]]
            [:td.tags-cell (tag-links (:tags bm))]
            [:td.actions-cell
             [:a.btn-edit {:href (str "/bookmarks/" (:id bm) "/edit")} "Edit"]]])]]]
      [:div.empty-state
       [:p "No bookmarks with this tag."]])))

;; ── about page ────────────────────────────────────────────────

(defn about-page
  "Render the about page."
  []
  (layout "About"
    [:div.about-content
     [:section
      [:h2 "What is LinkVault?"]
      [:p "LinkVault is a simple, fast bookmark manager built with "
       [:strong "Clojure"] ", " [:strong "Ring"] ", " [:strong "Compojure"]
       ", and " [:strong "Hiccup"] ". It stores your bookmarks in SQLite and "
       "renders every page on the server."]]
     [:section
      [:h2 "Features"]
      [:ul
       [:li "Add bookmarks with URL, title, and tags"]
       [:li "Edit or remove bookmarks at any time"]
       [:li "Browse bookmarks by tag"]
       [:li "Clean, distraction-free reading interface"]
       [:li "Zero JavaScript — every page is server-rendered"]]]
     [:section
      [:h2 "Stack"]
      [:p "Clojure · Ring · Compojure · Hiccup · SQLite · Plus Jakarta Sans"]]
     [:section
      [:h2 "Why LinkVault?"]
      [:p "Browser bookmarks are fine, but they live inside your browser. "
       "LinkVault gives you a lightweight, portable bookmark manager you can run "
       "locally or on a server — no accounts, no trackers, no JavaScript bloat. "
       "Just your links, organized with tags."]]]))
