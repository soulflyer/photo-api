(ns photo-api.routes.helpers.keywords
  (:require [clojure.string     :as str]
            [image-lib.core     :as ilc]
            [image-lib.keywords :as ilk]
            [image-lib.helper   :as ilh]
            [image-lib.images   :as ili]
            [photo-api.db.core  :as db]))

(defn keyword [kw]
  (ilk/find-keyword db/db db/keyword-collection kw ))

(defn children [kw]
  (ilk/find-sub-keywords db/db db/keyword-collection kw))

(defn add! [parent keyword]
  (ilk/add-keyword db/db db/keyword-collection keyword parent))

(defn path->id
  "remove all the / characters and any suffix (.jpg)"
  [path]
  (let [path-id   (str/join (str/split path #"/"))
        index-dot (if (= -1 (.lastIndexOf path-id "."))
                    (count path-id)
                    (.lastIndexOf path-id "."))]
    (if (< 0 index-dot)
      (subs path-id 0 index-dot)
      path-id)))

(defn best [kw]
  (let [sel  (:sample (ilk/find-keyword db/db db/keyword-collection kw))
        best (ilh/image-path (ilc/best-sub-image db/db db/image-collection
                                                 db/keyword-collection kw))]
    (or sel best)))

(defn best-map [kw]
  (let [best-path (best kw)
        best-id   (path->id best-path)]
    (ili/find-image db/db db/image-collection best-id)))

(defn all []
  (ilk/all-keywords db/db db/keyword-collection))

(defn used []
  (ilc/used-keywords db/db db/image-collection))

(defn dictionary [root]
  (let [keyword-map (keyword root)
        sample      (get keyword-map :sample)
        children    (get keyword-map :sub)]
    (reduce into {} [{:name root}
                     (if sample {:sample sample})
                     (if (< 0 (count children))
                       {:children (vec (for [child (:sub keyword-map)]
                                         (dictionary child)))})])))
