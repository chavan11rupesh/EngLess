(ns user
  (:require [mount.core :as mount]
            [engless.figwheel :refer [start-fw stop-fw cljs]]
            engless.core))

(defn start []
  (mount/start-without #'engless.core/repl-server))

(defn stop []
  (mount/stop-except #'engless.core/repl-server))

(defn restart []
  (stop)
  (start))


