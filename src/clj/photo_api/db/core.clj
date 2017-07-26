(ns photo-api.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [mount.core :refer [defstate]]
              [photo-api.config :refer [env]]
              [image-lib.preferences :refer [preference]]))

(def keyword-collection       "keywords")
(def preference-collection "preferences")
(def image-collection           "images")

(defn external-viewer [db prefs]
  (preference db prefs "external-viewer"))

(defn medium-dir [db prefs]
  (preference db prefs "medium-directory"))

(defn json-dir [db prefs]
  (preference db prefs "json-directory"))

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-user [user]
  (mc/insert db "users" user))

(defn update-user [id first-name last-name email]
  (mc/update db "users" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:_id id}))
