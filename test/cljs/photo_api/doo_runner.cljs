(ns photo-api.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [photo-api.core-test]))

(doo-tests 'photo-api.core-test)

