(defproject photo-api "0.1.1"

  :description "API for geting data from photos db"
  :url "http://soulflyer.com"

  :dependencies [[cheshire "5.8.1"]
                 [clj-time "0.15.1"]
                 [com.google.guava/guava "23.0"]
                 [com.novemberain/monger "3.5.0" :exclusions [com.google.guava/guava]]
                 [compojure "1.6.1"]
                 [cprop "0.1.13"]
                 [expectations "2.2.0-rc3"]
                 [funcool/struct "1.3.0"]
                 [luminus-immutant "0.2.5"]
                 [luminus-nrepl "0.1.6"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.0"]
                 [metosin/compojure-api "1.1.12"]
                 [metosin/muuntaja "0.6.4"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0-alpha.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.webjars.bower/tether "1.4.4"]
                 [org.webjars/bootstrap "4.3.1"]
                 [org.webjars/font-awesome "5.8.2"]
                 [org.webjars/jquery "3.4.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-codec "1.1.1"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.12"]
                 [image-lib "0.2.3-SNAPSHOT"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot photo-api.core

  :plugins [[lein-cprop "1.0.3"]
            [org.clojars.punkisdead/lein-cucumber "1.0.5"]
            [lein-immutant "2.1.0"]]
  :cucumber-feature-paths ["test/clj/features"]


  :profiles
  {:repl {:plugins [[cider/cider-nrepl "0.21.0"]]}
   :uberjar {:omit-source true
             :aot :all
             :uberjar-name "photo-api.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.6.3"]
                                 [ring/ring-mock "0.4.0"]
                                 [ring/ring-devel "1.7.1"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [clj-webdriver/clj-webdriver "0.7.2"]
                                 [org.apache.httpcomponents/httpcore "4.4.11"]
                                 [org.clojure/core.cache "0.7.2"]
                                 [org.seleniumhq.selenium/selenium-server "3.141.59"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]]
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
