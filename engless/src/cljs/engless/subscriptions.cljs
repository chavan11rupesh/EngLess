(ns engless.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(reg-sub
 :word-day
 (fn [db _]
   (or (:word-day db) {})))

(reg-sub
 :dict-meaning
 (fn [db _]
   (or (:dict-meaning db) {:mean ""
                           :usage ""})))

(reg-sub
 :saved-words
 (fn [db _]
   (or (:saved-words db) {})))

(reg-sub
 :words
 (fn [db _]
   (or (:words db) [{}])))

(reg-sub
 :logged-in
 (fn [db _]
   (or (:logged-in db) false)))


(reg-sub
 :user
 (fn [db _]
   (or (:user db) " ")))
