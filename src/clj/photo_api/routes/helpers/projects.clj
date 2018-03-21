(ns photo-api.routes.helpers.projects
  (:require [clojure.string :as str]) )

(defn group-by*
  "Similar to group-by, but takes a collection of functions and returns
  a hierarchically grouped result. https://stackoverflow.com/questions/25386863"

  [fs coll]
  (if-let [f (first fs)]
    (into {} (map (fn [[k vs]]
                    [k (group-by* (next fs) vs)])
               (group-by f coll)))
    coll))

(defn project-map [prj]
  (let [prj-vec (map #(str/split % #"/") prj)
        prj-map (group-by* [first second] prj-vec)]
    (reduce merge
            (for [year-name (keys prj-map)]
              (let [year (prj-map year-name)]
                (hash-map
                  year-name
                  (reduce merge
                          (for [month-name (keys year)]
                            (let [month (year month-name)]
                              (hash-map
                                month-name
                                (vec (for [project month]
                                       (nth project 2)))))))))))))
