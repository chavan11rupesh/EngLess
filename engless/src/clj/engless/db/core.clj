(ns engless.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [mount.core :refer [defstate]]
              [engless.config :refer [env]]
              [monger.result :refer [acknowledged?]]))

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-user [user]
  (mc/insert db "users" user))


(defn insert-user-db [user password email]
  (acknowledged? (mc/insert db "users" {:name user
                                        :pass password
                                        :email email})))

(defn update-user [id first-name last-name email]

  (mc/update db "users" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))



(defn create-words [word meaning img syn  location level]
  (mc/insert db "words" {:word word
                         :mean meaning
                         :imgp img
                         :synm syn
                         :locn location
                         :levl level}))



(defn save-words [user word meaning usage]
  (mc/insert db user  {:word word
                               :mean meaning
                               :usage usage}))

(defn return-words-at [location]
  (mc/find-maps db "words" {:locn location}))


(defn return-word-of [level]
   (mc/find-one-as-map db "words" {:levl level}))

(defn get-saved-words
  [user]
  (mc/find-maps db user {}))


(defn get-user [user]
  (mc/find-one-as-map db "users" {:name user}))
