(ns engless.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[engless started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[engless has shut down successfully]=-"))
   :middleware identity})
