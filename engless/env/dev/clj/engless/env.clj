(ns engless.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [engless.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[engless started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[engless has shut down successfully]=-"))
   :middleware wrap-dev})
