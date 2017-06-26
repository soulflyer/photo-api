(ns ^:figwheel-no-load photo-api.app
  (:require [photo-api.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
