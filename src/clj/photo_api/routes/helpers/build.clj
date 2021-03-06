(ns photo-api.routes.helpers.build
  (:require [photo-api.db.core :as db]
            [clojure.string :as str]
            [clojure.java.shell :refer [sh]]))

(defn zipfile [zipf filename]
  (sh "sh" "-c" (str "zip -jq " zipf " " filename)))

(defn build-json [divecentre filename filelist]
  (let [path (str (db/json-dir db/db db/preference-collection))
        ldir (str (db/large-dir db/db db/preference-collection))
        zdir (str (db/zip-dir db/db db/preference-collection))
        zipname (str zdir "/" filename ".zip")
        files (sort (str/split filelist #" "))
        flist (str/join " " files)]
    (do
      (sh "sh" "-c"
          (str "/Users/iain/bin/build-json -l \"" flist "\""
               " -d \"" divecentre "\""
               " -z "
               filename))
      (str "created JSON file " filename " for " divecentre))))
