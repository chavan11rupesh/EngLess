(ns engless.routes.home
  (:require [engless.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [engless.view :as view]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8")))
  (GET "/user" [user password]
       (layout/render-json (view/check-user user password)))
  (GET "/words" [location]
       (layout/render-json (view/get-words-at location)))
  (GET "/word" [level]
       (layout/render-json (view/get-word-of level))))
