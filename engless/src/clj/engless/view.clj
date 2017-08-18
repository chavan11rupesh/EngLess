(ns engless.view
  (:require [engless.layout :as layout]
            [engless.db.core :as db]
            [postal.core :refer [send-message]])
  (:gen-class))


(defn get-words-at [location]
  (map #(dissoc % :_id)
       (db/return-words-at location)))

(defn get-word-of [level]
  (dissoc (db/return-word-of level)
          :_id))

(defn check-user [user password]
  (if-let [user-map (db/get-user user)]
    (if (= password (user-map :pass))
      (dissoc user-map :_id))))



(defn insert-user [user password email]
  (db/insert-user-db user password email))


(defn get-saved-words
  [user]
  (map #(dissoc % :_id)
       (db/get-saved-words user)))





(defn save-word
  [user map-word]
  (db/save-word user
                (map-word :word)
                (map-word :mean)
                (map-word :usage)))



(def conn {:host "smtp.gmail.com"
           :ssl true
           :user "rupeshbhavesh11@gmail.com"
           :pass "rupeshbhavesh11"})


(defn send-mail
  [email map-word-data]
  (send-message conn {:from "rupeshbhavesh11@gmail.com"
                      :to email
                      :subject "Welcome To EngLess"
                      :body "hello"}))
