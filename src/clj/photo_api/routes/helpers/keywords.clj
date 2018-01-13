(ns photo-api.routes.helpers.keywords
  (:require [clojure.string     :as str]
            [image-lib.core     :as ilc]
            [image-lib.keywords :as ilk]
            [image-lib.helper   :as ilh]
            [photo-api.db.core  :as db]))

(defn keyword [kw]
  (ilk/find-keyword db/db db/keyword-collection kw ))

(defn children [kw]
  (ilk/find-sub-keywords db/db db/keyword-collection kw))

(defn add! [parent keyword]
  (ilk/add-keyword db/db db/keyword-collection keyword parent))

(defn best [kw]
  (let [sel  (:sample (ilk/find-keyword db/db db/keyword-collection kw))
        best (ilh/image-path (ilc/best-sub-image db/db db/image-collection
                                  db/keyword-collection kw))]
    (or sel best)))

(defn all []
  (ilk/all-keywords db/db db/keyword-collection))

(defn used []
  (ilc/used-keywords db/db db/image-collection))
