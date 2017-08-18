(ns engless.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [engless.ajax :refer [load-interceptors!]]
            [engless.handlers]
            [engless.subscriptions])
  (:import goog.History))


(def word-day "http://urban-word-of-the-day.herokuapp.com/")


(defn get-by-id
  [id]
  (.-value (.getElementById js/document id)))


(defn log
  [& params]
  (.log js/console (apply str params)))


(defn error-handler
  []
  (js/alert "Sorry you can not proceed :("))


(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))


(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    [:nav.navbar.navbar-dark.bg-primary
     [:button.navbar-toggler.hidden-sm-up
      {:on-click #(swap! collapsed? not)} "☰"]
     [:div.collapse.navbar-toggleable-xs
      (when-not @collapsed? {:class "in"})
      [:ul.nav.navbar-nav
       [nav-link "#/" "Home" :home collapsed?]
       [nav-link "#/game" "Game" :game collapsed?]
       [nav-link "#/dictionary" "Dictionary" :dictionary collapsed?]
       [nav-link "#/revision" "Revision" :revision collapsed?]]]]))



(defn word-of-the-day
  []
  (GET word-day
       {:response-format :json
        :keywords? true
        :handler #(rf/dispatch [:word-day %])
        :error-handler error-handler}))





(defn home-page []
 [:div
  [:header
   [navbar]
    [:div.row
     [:div.logo
      [:img {:src (str js/context "/img/Engless.png")}]]
     ]
    [:div.hero
     [:h1 "WORD OF THE DAY"]
     [:div.button-awesome
      [:a.btn.btn-full {:href ""} "GET STARTED"]
      ]]]
   #_[:section.features
    [:h3 [:center ""]]
      [:p.copy ""]]])

(defn game-page
  []
  [:div
   [:h2 "sds"]]
  )


(defn dictionary-page
  []
  [:div
   [:h2 "sds"]]
  )


(defn revision-page
  []
  [:div
   [:h2 "sds"]]
  )

(def pages
  {:home #'home-page
   :game #'game-page
   :dictionary #'dictionary-page
   :revision #'revision-page
   })

(defn page []
  [:div
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/game" []
  (rf/dispatch [:set-active-page :game]))

(secretary/defroute "/dictionary" []
  (rf/dispatch [:set-active-page :dictionary]))

(secretary/defroute "/revision" []
  (rf/dispatch [:set-active-page :revision]))


;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
