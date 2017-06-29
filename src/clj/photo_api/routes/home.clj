(ns photo-api.routes.home
  (:require [photo-api.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [image-lib.core :refer [all-projects]]))

(defn home-page []
  (layout/render "home.html"))

(defn docs-page []
  (layout/render "docs.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (docs-page)))
