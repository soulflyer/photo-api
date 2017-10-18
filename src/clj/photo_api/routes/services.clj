(ns photo-api.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [photo-api.db.core :refer :all]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [ring.util.codec :refer [url-decode]]
            [cheshire.core :as json]
            [image-lib.projects :refer [all-projects
                                        project-images
                                        project-paths]]
            [image-lib.preferences :refer [preference
                                           preference!]]))

(defn zipfile [zipf filename]
  (sh "sh" "-c" (str "zip -jq " zipf " " filename)))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data
             {:info
              {:version "1.0.0"
               :title "Photos API"
               :description "Access a mongo database containing details of photos"}}}}

  (context "/api" []
           :tags ["photos"]

           (GET "/preferences/:pref" [pref]
                :return s/Str
                :summary "returns preferences as stored in the db"
                (ok (preference db "preferences" pref)))

           (GET "/preferences/set/:pref/:value" [pref value]
                :return s/Str
                :summary "sets a preference in the database"
                (ok (str (preference! db "preferences" pref value))))

           (GET "/projects" []
                :return s/Str
                :summary "returns all projects"
                (ok (str (all-projects db "images"))))

           ;; (GET "/project/:yr/:mo/:pr" [yr mo pr]
           ;;      :return s/Str
           ;;      :summary "returns all picture paths for a given project"
           ;;      (ok (str (project-paths db "images" yr mo pr))))

           (GET "/project/:yr/:mo/:pr" [yr mo pr]
                :return s/Str
                :summary "returns all picture details for a given project."
                (ok (json/generate-string (project-images db "images" yr mo pr))))

           (GET "/build/json/:divecentre/:filename/:filelist" [divecentre filename filelist]
                :return s/Str
                :summary "creates a JSON file containing the pics in filelist."
                (ok
                  (let [path (str (json-dir db preference-collection))
                        ldir (str (large-dir db preference-collection))
                        zdir (str (zip-dir db preference-collection))
                        fn (if (= \/ (first filename))
                             filename
                             (str path "/" filename))
                        zipname (str zdir "/" filename ".zip")
                        files (sort (str/split filelist #" "))
                        flist (str/join " " files)]
                    (do
                      ;; TODO replace this shell script with clojure code
                      (sh "sh" "-c" (str "/Users/iain/bin/build-json -l " flist
                                         " -d " divecentre
                                         " > " fn ))
                      (doall (map #(zipfile zipname (str ldir "/" %)) files))
                      (str "created JSON file " filename " for " divecentre)))))

           (GET "/open/:size/:filelist" [size filelist]
                ;; size is ignored for now, always opens medium
                :return s/Str
                :summary "Opens a list of files in an external viewer."
                (ok
                  (let [viewer (external-viewer db preference-collection)
                        path   (medium-dir db preference-collection)
                        files  (str/split (url-decode filelist) #" ")
                        paths  (str/join " " (map #(str path "/" %) files))]
                    (do
                      (sh "sh" "-c" (str viewer " " paths))
                      (str "Opening " paths)))))

           (GET "/open/project/:yr/:mo/:pr" [yr mo pr]
                :return s/Str
                :summary "Open a project in external program as specified in options db"
                (ok
                  (let [path (str (medium-dir db preference-collection)
                                  "/"
                                  yr "/" mo "/" pr)
                        files (str/split (:out (sh "ls" path)) #"\n")
                        paths (reduce #(str %1 " " %2) (map #(str path "/" %) files))
                        viewer (external-viewer db preference-collection)
                        command (str viewer " " paths)]
                    (do
                      (sh "xargs" viewer :in paths)
                      (str "Opening " path)))))))
