(ns user
  (:require [mount.core :as mount]
            photo-api.core))

(defn start []
  (mount/start-without #'photo-api.core/repl-server))

(defn stop []
  (mount/stop-except #'photo-api.core/repl-server))

(defn restart []
  (stop)
  (start))
