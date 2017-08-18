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
            [engless.subscriptions]
            [soda-ash.core :as sa]
            [clojure.string :as str])
  (:import goog.History))

(enable-console-print!)
(println "dev mode")





;; link to get word of the day
(def server "http://localhost:3000/")

(def locations [{:key 1 :text "Airport" :value :airport}
                {:key 2 :text "Railway Station" :value :train}
                {:key 3 :text "Restaurant" :value :restaurant}])

(defn login-error
  []
  (js/alert "Failed to logged in!"))

(defn get-by-id
  [id]
  (.-value (.getElementById js/document id)))


(defn log
  [& params]
  (.log js/console (apply str params)))


(defn error-handler
  []
  (js/alert "Error!"))


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
       [nav-link "#/login" "Game" :login collapsed?]
       [nav-link "#/dictionary" "Dictionary" :dictionary collapsed?]
       [nav-link "#/revision" "Revision" :revision collapsed?]]]]))



(defn word-of-the-day
  []
  (GET (str server "word-day")
       {:response-format :json
        :keywords? true
        :handler #(rf/dispatch [:word-day %])
        :error-handler error-handler}))




(defn home-page []
  [:div
   [:div.home
    [:div.row
     [:div.logo
      [:img {:src (str js/context "/img/Engless.png")}]]]
    [:div.word-day
     [:h1 "WORD OF THE DAY"]
     [sa/Card
      [sa/CardContent
       [sa/CardHeader  (@(rf/subscribe [:word-day]) :word)]
       [sa/CardMeta "Meaning"]
       [sa/CardDescription (@(rf/subscribe [:word-day]) :meaning)]]]
     [:div.button-awesome
      [:a.btn.btn-full {:href "#/words"} "GET STARTED"]]]]])




(defn dictionary-handler
  [word [response]]
  (let [[defination] (:senses response)
        [usage] (:examples defination)]
    (if (empty? defination)
      (js/alert "Word Not Found!")
      (rf/dispatch [:dict-meaning {:word word
                                   :mean (:definition defination)
                                   :usage (:text usage)}]))))







(defn dictionary-page
  []
  [:div.home
   [:div.word-day
    [:h2 {:style {:color "White"}} "Search for?"]
    [:form {
            :on-submit (fn []
                         (let [word (get-by-id "dict-search")]
                           (GET (str server "dictionary")
                                {:params {:word word}
                                 :format :json
                                 :response-format :json
                                 :keywords? true
                                 :handler #(dictionary-handler word (% :results))
                                 :error-handler error-handler})))}
     [:input {:type "text" :id "dict-search"}]
     [:input {:type "submit" :value "Go"}]]
    [:h3 (str "Last Word : " (:word @(rf/subscribe [:dict-meaning])))]
    [sa/Card
     [sa/CardContent
      [sa/CardMeta "Meaning"]
      [sa/CardDescription (str/capitalize (:mean @(rf/subscribe [:dict-meaning])))]]]
    [sa/Card
     [sa/CardContent
      [sa/CardMeta "Usage"]
      [sa/CardDescription (str/capitalize (:usage @(rf/subscribe [:dict-meaning])))]]]]])



(defn get-dropdown-value [event data]
  (-> data
      (js->clj :keywordize-keys true)
      :value))



(defn get-location-words
  [location]
  (GET (str server "words")
       {:params {:location location}
        :format :json
        :response-format :json
        :keywords? true
        :handler #(rf/dispatch [:words %])
        :error-handler error-handler}))

(defn word-item
  [word-map]
  [sa/Card
   [sa/CardContent
    [sa/Image {:floated "left"
               :size "large"
               :src (word-map :imgp)}]
    [sa/CardMeta (word-map :word)]
    [sa/CardDescription (word-map :mean)]]])


(defn words-of-location
  []
  [:div.cards-loc
   [sa/CardGroup
    (doall (map word-item  @(rf/subscribe [:words])))]])

(defn words-page
  []
  [:div.home
   [:div.logo
    [:img {:src (str js/context "/img/Engless.png")}]]
   [:div.location-dropdown
    [sa/Dropdown {:fluid true
                  :selection true
                  :placeholder "Select Location"
                  :options locations
                  :onChange (fn [e data]
                              (get-location-words (get-dropdown-value e data)))}]
    [:h3 "Commonly used words at the choosen location"]]
   [words-of-location]])


(defn login-handler
  "when logged in then play game"
  [response]
  (println response)
  (when response
    (let [level (response :levl)]
           (GET (str server "word")
                {:params {:level level}
                 :format :json
                 :response-format :json
                 :keywords? true
                 :handler #(rf/dispatch [:word-level %])}))
    (rf/dispatch [:user-login true])
    (println "hsdfsd")
    (rf/dispatch [:set-active-page :game])))


(defn register-handler
  [response]
  (when response
      (js/alert ("Welcome!!  Registered successfully!!!"))))

(defn login-error
  []
  (js/alert "Failed to logged in!"))



(defn game-page
  []
  [:iframe.game {:src "https://www.gamestolearnenglish.com/htmlGames/fastEnglish/v12.1/index.html#site/personal"
            :scrolling "no"
            :height "550px"
            :width "100%"}])






(defn login-page
  []
  [:div.home
   [:div.logo
    [:div.word-day
     [sa/Modal {:trigger (r/as-element [sa/Button {:positive true} "Start"])}
      [sa/ModalHeader "Login"]
      [sa/ModalContent {:image true}
       [sa/Image {:wrapped true
                  :size    "small"
                  :src     "http://semantic-ui.com/images/avatar2/large/rachel.png"}]
       [sa/ModalDescription
        [sa/Header ""]
        [:form {
                :on-submit (fn [e]
                             (.preventDefault e)
                             (let [user (get-by-id "user")
                                   pass (get-by-id "pass")]
                               (log user pass)
                               (GET (str server "user")
                                    {:params {:user user
                                              :password pass}
                                     :format :json
                                     :response-format :json
                                     :keywords? true
                                     :handler login-handler
                                     :error-handler login-error})))}
         [:div.form-group[:input.form-control{:type "text" :id "user"}]]
         [:div.form-group [:input.form-control {:type "password" :id "pass"}]]
         [:div [:input.btn.btn-primary{:type "submit" :value "Login"}]]]]]]
     [sa/Modal {:trigger (r/as-element [sa/Button {:positive true} "Register"])}
      [sa/ModalHeader "Register"]
      [sa/ModalContent {:image true}
       [sa/Image {:wrapped true
                  :size    "small"
                  :src     "http://semantic-ui.com/images/avatar2/large/rachel.png"}]
       [sa/ModalDescription
        [sa/Header ""]
        [:form {:action "#" :method "get"
                :on-submit (fn [e]
                             (let [user (get-by-id "reg-user")
                                   pass (get-by-id "reg-pass")
                                   email-id (get-by-id "reg-email")]
                               #_(log user pass)
                               (POST (str server "signup")
                                    {:params {:user user
                                              :password pass
                                              :email email-id}
                                     :format :json
                                     :response-format :json
                                     :keywords? true
                                     :handler register-handler
                                     :error-handler login-error})))}
         [:div.form-group[:input.form-control{:type "text" :id "reg-user"}]]
         [:div.form-group [:input.form-control {:type "password" :id "reg-pass"}]]
         [:div.form-group [:input.form-control {:type "email" :id "reg-email"}]]
         [:div [:input.btn.btn-primary{:type "submit" :value "Register"}]]]]]]]]])




(defn revision-page
  []
  [:div
   [:h2 "sds"]])

(def pages
  {:home #'home-page
   :login #'login-page
   :game #'game-page
   :dictionary #'dictionary-page
   :revision #'revision-page
   :words #'words-page
   })

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))


(secretary/defroute "/login" []
  (rf/dispatch [:set-active-page :login]))


(secretary/defroute "/dictionary" []
  (rf/dispatch [:set-active-page :dictionary]))

(secretary/defroute "/words" []
  (rf/dispatch [:set-active-page :words]))

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
  (mount-components)
  (word-of-the-day))
