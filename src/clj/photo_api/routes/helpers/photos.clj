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
