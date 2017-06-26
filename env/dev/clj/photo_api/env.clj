(ns photo-api.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [photo-api.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[photo-api started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[photo-api has shut down successfully]=-"))
   :middleware wrap-dev})
