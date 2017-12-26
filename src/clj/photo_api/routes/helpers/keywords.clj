(ns photo-api.routes.helpers.keywords
  (:require [clojure.string    :as str]
            [image-lib.core    :as ilc]
            [image-lib.helper  :as ilh]
            [photo-api.db.core :as db]))

(defn keyword [kw]
  (ilc/get-keyword db/db db/keyword-collection kw ))

(defn children [kw]
  (ilc/find-sub-keywords db/db db/keyword-collection kw))

(defn add! [parent keyword]
  (ilc/add-keyword db/db db/keyword-collection keyword parent))

(defn best [kw]
  (let [sel  (:sample (ilc/get-keyword db/db db/keyword-collection kw))
        best (ilh/image-path (ilc/best-sub-image db/db db/image-collection
                                  db/keyword-collection kw))]
    (or sel best)))

(defn all []
  (ilc/all-keywords db/db db/keyword-collection))

(defn used []
  (ilc/used-keywords db/db db/image-collection))
