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
                                           preference!]]
            [photo-api.routes.helpers.open  :as open]
            [photo-api.routes.helpers.build :as build]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data
             {:info
              {:version "1.0.0"
               :title "Photos Development API"
               :description "Access a mongo database containing details of photos"}}}}

  (context "/api" []
           :tags ["photos"]

           (GET "/projects" []
                :return s/Str
                :summary "returns all projects"
                (ok (str (all-projects db "images"))))

           (GET "/project/:yr/:mo/:pr" [yr mo pr]
                :return s/Str
                :summary "returns all picture details for a given project."
                (ok (json/generate-string (project-images db "images" yr mo pr))))



           (context "/preferences" []
                    :tags ["preferences"]
                    (GET "/:pref" [pref]
                         :return s/Str
                         :summary "returns preferences as stored in the db"
                         (ok (preference db "preferences" pref)))

                    (GET "/set/:pref/:value" [pref value]
                         :return s/Str
                         :summary "sets a preference in the database"
                         (ok (str (preference! db "preferences" pref value)))) )

           (context "/open" []
                    :tags ["open"]

                    (GET "/:size/:filelist" [size filelist]
                         ;; size is ignored for now, always opens medium
                         :return s/Str
                         :summary "Opens a list of files in an external viewer."
                         (ok (open/open-files size filelist)))

                    (GET "/project/:yr/:mo/:pr" [yr mo pr]
                         :return s/Str
                         :summary "Open a project in external program."
                         (ok (open/open-project yr mo pr))))

           (context "/build" []
                    :tags ["build"]
                    (GET "/json/:divecentre/:filename/:filelist" [divecentre filename filelist]
                         :return s/Str
                         :summary "creates a JSON file containing the pics in filelist."
                         (ok (build/build-json divecentre filename filelist))))))
