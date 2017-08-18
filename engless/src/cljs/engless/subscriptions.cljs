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
 :words
 (fn [db _]
   (or (:words db) [{}])))
