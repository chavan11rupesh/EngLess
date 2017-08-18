(ns engless.routes.home
  (:require [engless.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clj-http.client :as client]
            [engless.view :as view]))



(def word-day "http://urban-word-of-the-day.herokuapp.com")


(def dictionary "http://api.pearson.com/v2/dictionaries/wordwise/entries?headword=")

(defn home-page []
  (layout/render "home.html"))


(defn get-dict-word
  [word]
  (client/get (str dictionary word)))


(defn get-word
  "returns map of word and meaning from urban-word-of-the-day"
  []
  (client/get word-day))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8")))
  (GET "/user" [user password]
       (layout/render-json (view/check-user user password)))

  (POST "/signup" [user password email]
       (layout/render-json (view/insert-user user password email)))

  (GET "/words" [location]
       (layout/render-json (view/get-words-at location)))

  (GET "/word-day" []
       (get-word))

  (GET "/dictionary" [word]
       (get-dict-word word))

  (GET "/word" [level]
       (layout/render-json (view/get-word-of level))))
