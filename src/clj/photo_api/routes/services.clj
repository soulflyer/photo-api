(ns photo-api.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [photo-api.db.core :refer :all]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [image-lib.projects :refer [all-projects
                                        project-images
                                        project-paths]]
            [image-lib.preferences :refer [preference]]))

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

           (GET "/projects" []
                :return s/Str
                :summary "returns all projects"
                (ok (str (all-projects db "images"))))

           (GET "/project/:yr/:mo/:pr" [yr mo pr]
                :return s/Str
                :summary "returns all picture paths for a given project"
                (ok (str (project-paths db "images" yr mo pr))))

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
                        ;; The following monstrosity is because there is a \" at the start of
                        ;; filelist. wut?
                        files (sort (str/split (str/replace filelist #"\"" "") #" "))
                        flist (str "\"" (str/join " " files) "\"")]
                    (do
                      ;; TODO replace this shell script with clojure code
                      (sh "sh" "-c" (str "/Users/iain/bin/build-json -l " flist
                                         " -d " divecentre
                                         " > " fn ))
                      (doall (map #(zipfile zipname (str ldir "/" %)) files))
                      (str "created JSON file " filename " for " divecentre)))))

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
