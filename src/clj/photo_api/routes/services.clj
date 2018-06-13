(ns photo-api.routes.services
  (:require [cheshire.core                     :as json]
            [compojure.api.sweet               :refer :all]
            [image-lib.images                  :as images]
            [image-lib.preferences             :as pr]
            [image-lib.projects                :as ipr]
            [photo-api.db.core                 :as db]
            [photo-api.routes.helpers.build    :as build]
            [photo-api.routes.helpers.keywords :as keywords]
            [photo-api.routes.helpers.open     :as open]
            [photo-api.routes.helpers.photos   :as photos]
            [photo-api.routes.helpers.projects :as projects]
            [ring.util.codec                   :refer [url-decode]]
            [ring.util.http-response           :refer :all]
            [schema.core                       :as s]
            [clojure.string                    :as str]))

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
      (ok (json/generate-string (ipr/all-projects db/db "images"))))

    (context "/project" []
      :tags ["projects"]
      (GET "/:yr/:mo/:pr" [yr mo pr]
        :return s/Str
        :summary "returns all picture details for a project."
        (ok (json/generate-string (ipr/project-images db/db "images" yr mo pr))))
      (GET "/maps" []
        :return s/Str
        :summary "returns a JSON may of the projects tree"
        (ok (json/generate-string (projects/project-map (ipr/all-projects db/db "images"))))))

    (context "/photos" []
      :tags ["photos"]
      (context "/write" []
        (POST "/title/:year/:month/:project/:photo/:title" [year month project photo title]
          :return s/Str
          :summary "adds a title to a photo"
          (ok (str "hello"))))
      (context "/add/keyword" []
        (GET "/:keyword/:photos" [keyword photos]
          :return s/Str
          :summary "adds a new keyword to some photos"
          (ok (photos/add-keyword keyword photos)))
        (GET "/:keyword/:year/:month/:project/:photo" [keyword year month project photo]
          :return s/Str
          :summary "adds a keyword to a specified photo"
          (ok (photos/add-keyword keyword year month project photo))))
      (GET "/:year/:month/:project" [year month project]
        :return s/Str
        :summary "returns all picture details for a project."
        (ok (json/generate-string (images/images db/db "images" year month project))))
      (GET "/:year/:month" [year month]
        :return s/Str
        :summary "returns all picture details for a month."
        (ok (json/generate-string (images/images db/db "images" year month))))
      (GET "/:year" [year]
        :return s/Str
        :summary "returns all picture details for a year."
        (ok (json/generate-string (images/images db/db "images" year)))))

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
        (ok (pr/preference db/db "preferences" pref)))

      (GET "/set/:pref/:value" [pref value]
        :return s/Str
        :summary "sets a preference in the database"
        (ok (str (pr/preference! db/db "preferences" pref value)))) )

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
