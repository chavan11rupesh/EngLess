(ns engless.handlers
  (:require [engless.db :as db]
            [re-frame.core :refer [dispatch reg-event-db]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
 :word-day
 (fn [db [_ word-day]]
   (assoc db :word-day word-day)))


(reg-event-db
 :words
 (fn [db [_ location]]
   (assoc db :words location)))

(reg-event-db
 :user-data
 (fn [db [_ user-data]]
   (assoc db :user-data user-data)))

(reg-event-db
 :word-level
 (fn [db [_ word-level]]
   (assoc db :word-level word-level)))


(reg-event-db
 :user-login
 (fn [db [_ value]]
   (assoc db :user-login value)))

(reg-event-db
 :dict-meaning
 (fn [db [_ meaning]]
   (assoc db :dict-meaning meaning)))
