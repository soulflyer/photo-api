(ns photo-api.routes.helpers.open
  (:require [photo-api.db.core :as db]
            [clojure.string :as str]
            [clojure.java.shell :refer [sh]]
            [ring.util.codec :refer [url-decode]]))

(defn test-string [] "hello world")

(defn open-project [yr mo pr]
  (let [path (str (db/medium-dir db/db db/preference-collection)
                  "/"
                  yr "/" mo "/" pr)
        files (str/split (:out (sh "ls" path)) #"\n")
        paths (reduce #(str %1 " " %2) (map #(str path "/" %) files))
        viewer (db/external-viewer db/db db/preference-collection)
        command (str viewer " " paths)]
    (do
      (sh "xargs" viewer :in paths)
      (str "Opening " path))))

(defn open-files [size filelist]
  (let [viewer (db/external-viewer db/db db/preference-collection)
        path   (db/medium-dir db/db db/preference-collection)
        files  (str/split (url-decode filelist) #" ")
        paths  (str/join " " (map #(str path "/" %) files))]
    (do
      (sh "sh" "-c" (str viewer " " paths))
      (str "Opening " paths))))
