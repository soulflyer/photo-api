(ns photo-api.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [photo-api.db.core :refer :all]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [image-lib.core :refer [all-projects]]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (context "/api" []
           :tags ["photos"]

           (GET "/projects" []
                :return s/Str
                :summary "returns all projects"
                (ok (str (all-projects db "images"))))

           (GET "/open/project/:yr/:mo/:pr" [yr mo pr]
                :return s/Str
                :summary "Open a project in whatever external program is specified in options db"
                (ok (let [path (str (medium-dir db preference-collection)
                                    "/"
                                    yr "/" mo "/" pr)
                          files (str/split (:out (sh "ls" path)) #"\n")
                          paths (reduce #(str %1 " " %2) (map #(str path "/" %) files))
                          viewer (external-viewer db preference-collection)
                          command (str viewer " " paths)]
                      (do
                        (sh "xargs" viewer :in paths)
                        (str "Running " command)))))))
