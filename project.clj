(defproject mokkameister "0.1.0-SNAPSHOT"
  :description "Mokkameister the coffee bot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.3.1"]
                 [clj-http "3.9.0"]
                 [clj-time "0.13.0"]
                 [cljs-ajax "0.5.1"]
                 [clojurewerkz/machine_head "1.0.0"]
                 [com.pusher/pusher-http-java "0.9.3"]
                 [compojure "1.6.1"]
                 [environ "1.0.1"]
                 [lambdaisland/uri "1.1.0"]
                 [liberator "0.15.3"]
                 [nrepl "0.9.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.145" :scope "provided"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [reagent "0.5.1"]
                 [ring-cors "0.1.7"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-jetty-adapter "1.7.0-RC1"]
                 [yesql "0.5.1"]
                 [org.xerial/sqlite-jdbc "3.40.0.0"]
                 [com.vdurmont/emoji-java "5.1.1"]]

  :plugins [[lein-environ "1.0.1"]
            [lein-ring "0.9.4"]
            [lein-figwheel "0.4.1"]
            [lein-cljsbuild "1.1.0"]]

  :ring {:handler mokkameister.web/handler
         :uberwar-name "mokkameister.war"}

  :main mokkameister.web

  :min-lein-version "2.5.0"

  :source-paths ["src/clj" "src/cljc"]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :dev :compiler :output-dir]
                                    [:cljsbuild :builds :dev :compiler :output-to]]

  :uberjar-name "mokkameister-standalone.jar"

  :cljsbuild
  {:builds
   {:dev {:source-paths ["src/cljs" "src/cljc"]
          :figwheel true
          :compiler {:output-to     "resources/public/js/app.js"
                     :output-dir    "resources/public/js/out"
                     :source-map    true
                     :source-map-timestamp true
                     :optimizations :none
                     :pretty-print  true}}

    :uberjar {:source-paths ["src/cljs" "src/cljc"]
              :jar true
              :compiler {:output-to      "resources/public/js/app.js"
                         :externs        ["resources/public/js-externs/moment-externs.js"
                                          "resources/public/js-externs/pusher-externs.js"]
                         :pretty-print   false
                         :optimizations  :advanced}}}}

  :profiles {:dev {:env {:dev true}}
             :uberjar {:env {:production true}
                       :omit-source true
                       :aot :all
                       :prep-tasks ["compile" ["cljsbuild" "once" "uberjar"]]}}
  )
