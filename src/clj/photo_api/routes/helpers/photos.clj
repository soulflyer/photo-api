(ns photo-api.routes.helpers.photos
  (:require [clojure.string    :as str]
            ;; [image-lib.core    :as ilc]
            ;; [image-lib.helper  :as ilh]
            [photo-api.db.core :as db]))

(defn add-keyword [keyword photo]
  "add <keyword> to the keywords field of the db entry for <photo>"
  (str keyword " " photo " from helpers.photos/add-keyword"))
