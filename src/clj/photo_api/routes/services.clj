(ns photo-api.routes.services
  (:require [cheshire.core                     :as json]
            [compojure.api.sweet               :refer :all]
            [image-lib.images                  :as ilim]
            [image-lib.preferences             :as ilpf]
            [image-lib.projects                :as ilpr]
            [image-lib.write                   :as ilwr]
            [photo-api.db.core                 :as db]
            [photo-api.config                  :refer [env]]
            [photo-api.routes.helpers.build    :as build]
            [photo-api.routes.helpers.keywords :as keywords]
            [photo-api.routes.helpers.open     :as open]
            [photo-api.routes.helpers.photos   :as photos]
            [photo-api.routes.helpers.projects :as projects]
            [ring.util.codec                   :refer [url-decode]]
            [ring.util.http-response           :refer [ok]]
            [schema.core                       :as s]
            [clojure.string                    :as str]
            [image-lib.keywords :as ilk]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data
             {:info
              {:version "1.0.1"
               ;; Switch to correct title before lein uberjar
               ;; TODO Automate this so swagger page always shows dev or prod version. This may not be
               ;; possible, it looks like the routes are defined before the env service is started.
               :title "Photo API"
               ;; :title (:title env)
               :description "Access a mongo database containing details of photos"}}}}

  (context "/api" []
    :tags ["projects"]
    (GET "/projects" []
      :return s/Str
      :summary "returns all projects"
      (ok (json/generate-string (ilpr/all-projects db/db "images"))))

    (context "/project" []
      :tags ["projects"]
      (GET "/:yr/:mo/:pr" [yr mo pr]
        :return s/Str
        :summary "returns all picture details for a project."
        (ok (json/generate-string (ilpr/project-images db/db "images" yr mo pr))))
      (GET "/maps" []
        :return s/Str
        :summary "returns a JSON map of the projects tree"
        (ok (json/generate-string (projects/project-map (ilpr/all-projects db/db "images"))))))

    (context "/photos" []
      :tags ["photos"]
      (context "/keyword" []
        (GET "/:keyword" [keyword]
          :return s/Str
          :summary "returns all the photos containing <keyword>"
          (ok
            (json/generate-string
              (ilim/find-images
                db/db
                db/image-collection
                :Keywords
                keyword))))
        (GET "/all/:keyword" [keyword]
          :return s/Str
          :summary "returns all the photos containing <keyword> and children of <keyword>"
          (ok
            (json/generate-string
              (ilim/find-all-images
                db/db
                db/image-collection
                db/keyword-collection
                keyword)))))

      (context "/write" []
        ;; TODO make this write to the db and create more similar endpoints for caption etc
        (GET "/title/:year/:month/:project/:photo/:title"
            [year month project photo title]
          :return s/Str
          :summary "adds a title to a photo"
          (let [id (str year month project photo)]
            (ok (str (ilwr/write-title db/db db/image-collection id title)))))
        (GET "/caption/:year/:month/:project/:photo/:caption"
            [year month project photo caption]
          :return s/Str
          :summary "adds a caption to a photo"
          (let [id (str year month project photo)]
            (ok (str (ilwr/write-caption db/db db/image-collection id caption)))))
        (GET "/:iptc/:year/:month/:project/:photo/:text"
            [iptc year month project photo text]
          :return s/Str
          :summary "writes something to a specified IPTC field"
          (let [id    (str year month project photo)
                field (keyword iptc)]
            (ok (str (ilwr/write-to-photo db/db db/image-collection id field text))))))
      ;; Convert to POST
      (context "/add/keyword" []
        (GET "/:keyword/:photos" [keyword photos]
          :return s/Str
          :summary "adds a keyword to some photos, specified as a string containing the ids"
          (ok (photos/add-keyword keyword photos)))
        (GET "/:keyword/:year/:month/:project/:photo" [keyword year month project photo]
          :return s/Str
          :summary "adds a keyword to a specified photo"
          (ok (photos/add-keyword keyword year month project photo))))

      (context "/delete/keyword" []
        (GET "/:kw/:year/:month/:project/:photo" [kw year month project photo]
          :return s/Str
          :summary "deletes a keyword from a photo"
          (ok (photos/delete-keyword kw year month project photo)))
        (GET "/:kw/:photos" [kw photos]
          :return s/Str
          :summary "deletes a keyword from some photos"
          (ok (photos/delete-keyword-from-photos kw photos))))

      (GET "/:year/:month/:project/:version" [year month project version]
        :return s/Str
        :summary "returns the details for a picture."
        (ok (json/generate-string
              (ilim/find-image db/db "images" (str year month project version)))))
      (GET "/:year/:month/:project" [year month project]
        :return s/Str
        :summary "returns all picture details for a project."
        (ok (json/generate-string (ilim/images db/db "images" year month project))))
      (GET "/:year/:month" [year month]
        :return s/Str
        :summary "returns all picture details for a month."
        (ok (json/generate-string (ilim/images db/db "images" year month))))
      (GET "/:year" [year]
        :return s/Str
        :summary "returns all picture details for a year."
        (ok (json/generate-string (ilim/images db/db "images" year)))))

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
      (GET "/:keyword/path" [keyword]
        :return s/Str
        :summary "returns the path to <keyword> as a vector"
        (ok (str (json/generate-string (ilk/keyword-path db/db db/keyword-collection keyword)))))
      (GET "/add/:parent/:keyword" [parent keyword]
        :return s/Str
        :summary "adds <keyword> as child of <parent>"
        (ok (str (keywords/add! parent keyword))))
      (GET "/delete/:keyword" [keyword]
        :return s/Str
        :summary "deletes a keyword, but only if it has no children"
        (ok (str (keywords/delete! keyword))))
      (GET "/move/:keyword/:old-parent/:new-parent" [keyword old-parent new-parent]
        :return s/Str
        :summary "Moves a keyword from old-parent to new-parent"
        (ok (str (keywords/move! keyword old-parent new-parent))))
      (GET "/rename/:old-name/:new-name" [old-name new-name]
        :return s/Str
        :summary "Renames a keyword in the db and in all pictures"
        (ok (str (keywords/rename! old-name new-name))))
      (GET "/merge/:dispose/:keep" [dispose keep]
        :return s/Str
        :summary "merges 2 keywords, keeping the second"
        (ok (str (keywords/merge! dispose keep))))
      (GET "/add/missing/" []
        :return s/Str
        :summary "Adds any keywords found in photos but not in already in the db"
        (ok (keywords/add-missing!)))
      (GET "/:keyword/best" [keyword]
        :return s/Str
        :summary "returns the selected, or best, image for <keyword>"
        (ok (keywords/best keyword)))
      (GET "/:keyword/best/map" [keyword]
        :return s/Str
        :summary "returns a map representing the selected, or best image for <keyword>"
        (ok (json/generate-string (keywords/best-map keyword))))
      (GET "/:keyword/best/all" [keyword]
        :return s/Str
        :summary "returns a list of the best image for a keyword and all sub keywords"
        (ok (json/generate-string (keywords/all-best keyword))))
      (GET "/all/" []
        :return s/Str
        :summary "returns all the keywords in the keyword-collection"
        (ok (json/generate-string (keywords/all))))
      (GET "/used/" []
        :return s/Str
        :summary "returns all keywords found in the image-collection"
        (ok (json/generate-string (keywords/used))))
      (GET "/unused/" []
        :return s/Str
        :summary "Returns a list of keywords that are in the db but not in any photos"
        (ok (json/generate-string (keywords/unused))))
      (GET "/delete/unused/" []
        :return s/Str
        :summary "CAUTION! Deletes all the keywords that are not in any photos."
        (ok (keywords/delete-unused!)))
      (GET "/map/" []
        :return s/Str
        :summary "returns a nested map of all the keywords"
        (ok (json/generate-string (keywords/dictionary "Root"))))
      (GET "/:kw/sample/:sample" [kw sample]
        :return s/Str
        :summary "Adds a sample picture to a keyword"
        (ok (keywords/add-sample! kw sample))))

    (context "/preferences" []
      :tags ["preferences"]
      (GET "/:pref" [pref]
        :return s/Str
        :summary "Get a stored preference."
        (ok (ilpf/preference db/db "preferences" pref)))
      ;; TODO convert these to POST
      (GET "/set/:pref/:value" [pref value]
        :return s/Str
        :summary "Set a preference."
        (ok (str (ilpf/preference! db/db "preferences" pref value)))) )

    (context "/open" []
      :tags ["open"]
      (GET "/:size/:filelist" [size filelist]
        ;; size is ignored for now, always opens medium
        :return s/Str
        :summary "Opens a list (space separated url-encoded) of files in an external viewer. ie 2015/03/01-1000-Dives/DIW_1686_ 2015/03/01-1000-Dives/DIW_1689"
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
