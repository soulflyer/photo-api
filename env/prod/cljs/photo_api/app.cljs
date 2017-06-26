(ns photo-api.app
  (:require [photo-api.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
