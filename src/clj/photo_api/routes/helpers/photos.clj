(ns photo-api.routes.helpers.photos
  (:require [clojure.string    :as str]
            [ring.util.codec   :refer [url-decode]]
            [image-lib.core    :as ilc]
            ;; [image-lib.helper  :as ilh]
            [photo-api.db.core :as db]))

(defn add-keyword
  "add <keyword> to the keywords field of the db entry for photo or photos"
  ([keyword year month project photo]
   ;;(add-keyword keyword (str year month project photo))
   (ilc/add-keyword-to-photo db/db db/image-collection keyword
                             (str year month project photo))
   (str "adding " keyword " to " photo))
  ([keyword photos]
   (let [files  (str/split (url-decode photos) #" ")]
     (doall (map #(ilc/add-keyword-to-photo db/db db/image-collection keyword %) files))
     (str "adding " keyword " to " (count files) " photos. " (first files)))))

(defn delete-keyword
  "remove <keyword> from the photo given"
  [keyword year month project photo]
  (let [id (str year month project photo)]
    (ilc/remove-keyword-from-photo db/db db/image-collection keyword id)
    (str "Deleted " keyword " from " id)))

(defn delete-keyword-from-photos
  "remove <keyword> from the set of photos given"
  [kw photos]
  (let [files  (str/split (url-decode photos) #" ")]
    (println (str "--------------" files))
    (doall (map #(ilc/remove-keyword-from-photo db/db db/image-collection kw %) files))
    ;;(ilc/remove-keyword-from-photo db/db db/image-collection kw)
    (str "removing " kw)))
