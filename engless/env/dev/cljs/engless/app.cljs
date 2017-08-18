(ns ^:figwheel-no-load engless.app
  (:require [engless.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
