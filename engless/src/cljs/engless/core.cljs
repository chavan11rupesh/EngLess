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

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))





(defn home-page []
 [:div
   [:header
    [:div.row
     [:div.logo
      [:img {:src (str js/context "/img/Engless.jpg")}]]
     [:ul.main-nav
      [:li
       [:a {:href ""} "Home"]]
      [:li
       [:a {:href ""} "Game"]]
      [:li
       [:a {:href ""} "Dictionary"]]
      [:li
       [:a {:href ""} "Contact us"]]]]
    [:div.hero
     [:h1 "LEARNING IS A TREASURE THAT WILL" [:br] "FOLLOW ITS OWENER EVERYWHERE."]
     [:div.button-awesome
      [:a.btn.btn-full {:href ""} "Show more"]
      ]]]
   [:section.features
    [:h3 [:center ""]]
    [:p.copy ""]]])

(def pages
  {:home #'home-page})

(defn page []
  [:div

   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))



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
