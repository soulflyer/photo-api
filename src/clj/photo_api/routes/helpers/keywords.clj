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
  (ilk/add-keyword db/db db/keyword-collection keyword parent)
  (str "Added " keyword " to " parent))

(defn delete! [kw]
  (ilc/remove-keyword-from-photos db/db db/image-collection kw)
  (ilk/safe-delete-keyword db/db db/keyword-collection kw)
  (str "Deleted " kw " from db and photos."))

(defn move! [kw old-parent new-parent]
  (ilk/move-keyword db/db db/keyword-collection kw old-parent new-parent)
  (str "Moved " kw " from " old-parent " to " new-parent))

(defn rename! [old new]
  (ilc/rename-keyword db/db
                      db/keyword-collection
                      db/image-collection
                      old new)
  (str "Renamed " old " to " new))

(defn merge! [dispose keep]
  (ilc/merge-keyword db/db
                     db/keyword-collection
                     db/image-collection
                     dispose keep)
  (str "Merged " dispose " into " keep))

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

(defn add-sample! [kw sample]
  (ilk/add-sample db/db db/keyword-collection kw sample)
  (str "Adding " sample " to " kw))

(defn best [kw]
  (let [kw-map (ilk/find-keyword db/db db/keyword-collection kw)]
    (or (:sample kw-map)
        (let [best-image (ilh/image-path (ilc/best-sub-image
                                           db/db
                                           db/image-collection
                                           db/keyword-collection kw))]
          (add-sample! kw best-image)
          best-image))))

(defn best-map [kw]
  (let [best-path (best kw)
        best-id   (path->id best-path)]
    (ili/find-image db/db db/image-collection best-id)))

(defn all []
  (ilk/all-keywords db/db db/keyword-collection))

(defn used []
  (ilc/used-keywords db/db db/image-collection))

(defn unused []
  (ilc/unused-keywords db/db db/image-collection db/keyword-collection))

(defn delete-unused! []
  (map delete! (unused) ))

(defn add-missing! []
  (ilc/add-missing-keywords
    db/db
    db/image-collection
    db/keyword-collection)
  (str "Added missing keywords."))

(defn dictionary [root]
  (let [keyword-map (keyword root)
        sample      (get keyword-map :sample)
        children    (get keyword-map :sub)]
    (reduce into {} [{:name root}
                     (if sample {:sample sample})
                     (if (< 0 (count children))
                       {:children (vec (for [child (:sub keyword-map)]
                                         (dictionary child)))})])))
