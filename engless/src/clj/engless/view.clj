(ns engless.view
  (:require [engless.layout :as layout]
            [engless.db.core :as db])
  (:gen-class))


(defn get-words-at [location]
  (map #(dissoc % :_id)
       (db/return-words-at location)))

(defn get-word-of [level]
  (dissoc (db/return-word-of level)
          :_id))

(defn check-user [user password]
  (if-let [user-map (db/get-user user)]
    (= password (user-map :pass))))