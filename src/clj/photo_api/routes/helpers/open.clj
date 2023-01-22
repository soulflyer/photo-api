(ns photo-api.routes.helpers.open
  (:require [photo-api.db.core :as db]
            [clojure.string :as str]
            [clojure.java.shell :refer [sh]]
            [ring.util.codec :refer [url-decode]]))

(def viewer 
  (if (sh "which" "open")
    "open"
    "xdg-open"))

(defn open-project [yr mo pr]
  (let [path (str (db/medium-dir db/db db/preference-collection)
                  "/"
                  yr "/" mo "/" pr)
        files (str/split (:out (sh "ls" path)) #"\n")
        paths (reduce #(str %1 " " %2) (map #(str path "/" %) files))]
    (sh "xargs" viewer :in paths)
    (str "Opening " paths " with " viewer ":")))

(defn open-files
  "Open a list of files provided as a single string of relative pathnames
  ie \"1991/11/fire_and_ice/59470001  /1991/11/fire_and_ice/59470013\"
  to open the 2 photos, 59470001 and 59470013 from the fire_and_ice project
  from November 1991"
  [size filelist]
  ;;TODO size is ignored and always opens medium
  (let [path    (db/medium-dir db/db db/preference-collection)
        files  (str/split (url-decode filelist) #" ")
        paths  (str/join " " (map #(str path "/" % ".jpg") files))]
    (sh "sh" "-c" (str viewer " " paths))
    (str "Opening " paths)))

(comment
  (open-project 1991 11 "fire_and_ice" )
  (open-files "medium" "1991/11/fire_and_ice/59470001 /1991/11/fire_and_ice/59470013")
  )
