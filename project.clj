(defproject photo-api "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[cider/cider-nrepl "0.15.0-SNAPSHOT"]
                 [clj-time "0.13.0"]
                 [com.google.guava/guava "20.0"]
                 [com.novemberain/monger "3.1.0" :exclusions [com.google.guava/guava]]
                 [compojure "1.6.0"]
                 [cprop "0.1.10"]
                 [funcool/struct "1.0.0"]
                 [luminus-immutant "0.2.3"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "0.9.99"]
                 [metosin/compojure-api "1.1.10"]
                 [metosin/muuntaja "0.3.1"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.11"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [org.webjars/jquery "3.1.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.1"]
                 [ring/ring-defaults "0.3.0"]
                 [selmer "1.10.7"]
                 [image-lib "0.2.0-SNAPSHOT"]]

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
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "photo-api.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.1.4"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.6.1"]
                                 [pjstadig/humane-test-output "0.8.2"]
                                 [clj-webdriver/clj-webdriver "0.7.2"]
                                 [org.apache.httpcomponents/httpcore "4.4"]
                                 [org.clojure/core.cache "0.6.3"]
                                 [org.seleniumhq.selenium/selenium-server "2.48.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]]
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
