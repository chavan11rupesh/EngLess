(ns engless.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [engless.core-test]))

(doo-tests 'engless.core-test)

