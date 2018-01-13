(ns photo-api.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [photo-api.db.core :as db]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [ring.util.codec :refer [url-decode]]
            [cheshire.core :as json]
            [image-lib.projects :refer [all-projects
                                        project-images
                                        project-paths]]
            [image-lib.preferences :refer [preference
                                           preference!]]
            [image-lib.core        :refer [add-keyword-to-photo]]
            [photo-api.routes.helpers.open     :as open]
            [photo-api.routes.helpers.build    :as build]
            [photo-api.routes.helpers.keywords :as keywords]
            [photo-api.routes.helpers.photos   :as photos]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data
             {:info
              {:version "1.0.0"
               :title "Photos Development API"
               :description "Access a mongo database containing details of photos"}}}}

  (context "/api" []
    :tags ["projects"]
    (GET "/projects" []
      :return s/Str
      :summary "returns all projects"
      (ok (json/generate-string (all-projects db/db "images"))))

    (context "/project" []
      :tags ["projects"]
      (GET "/:yr/:mo/:pr" [yr mo pr]
        :return s/Str
        :summary "returns all picture details for a given project."
        (ok (json/generate-string (project-images db/db "images" yr mo pr)))))

    (context "/photos" []
      :tags ["photos"]
      (GET "/add/keyword/:keyword/:photo" [keyword photo]
        :return s/Str
        :summary "adds a new keyword to a photo"
        (ok (photos/add-keyword keyword photo))))

    (context "/keywords" []
      :tags ["keywords"]
      (GET "/:keyword" [keyword]
        :return s/Str
        :summary "returns a keyword object"
        (ok (json/generate-string (keywords/keyword keyword))))
      (GET "/:keyword/children" [keyword]
        :return s/Str
        :summary "returns the sub keywords of <kw>"
        (ok (json/generate-string (keywords/children keyword))))
      (GET "/add/:parent/:keyword" [parent keyword]
        :return s/Str
        :summary "adds <keyword> as child of <parent>"
        (ok (str (keywords/add! parent keyword))))
      (GET "/:keyword/best" [keyword]
        :return s/Str
        :summary "returns the selected, or best, image for <keyword>"
        (ok (keywords/best keyword)))
      (GET "/all/" []
        :return s/Str
        :summary "returns all the keywords in the keyword-collection"
        (ok (json/generate-string (keywords/all))))
      (GET "/used/" []
        :return s/Str
        :summary "returns all keywords found in the image-collection"
        (ok (json/generate-string (keywords/used)))))

    (context "/preferences" []
      :tags ["preferences"]
      (GET "/:pref" [pref]
        :return s/Str
        :summary "returns preferences as stored in the db"
        (ok (preference db/db "preferences" pref)))

      (GET "/set/:pref/:value" [pref value]
        :return s/Str
        :summary "sets a preference in the database"
        (ok (str (preference! db/db "preferences" pref value)))) )

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
