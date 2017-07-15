(ns photo-api.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [photo-api.db.core :refer :all]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
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
                (ok (do
                      (println (external-viewer db preference-collection)
                          (str (medium-dir db preference-collection)
                               "/"
                               yr "/" mo "/" pr "/*.jpg"))
                      (str "Opening " pr))))

           (GET "/plus" []
                :return       Long
                :query-params [x :- Long, {y :- Long 1}]
                :summary      "x+y with query-parameters. y defaults to 1."
                (ok (+ x y)))))
