(defproject mokkameister "0.1.0-SNAPSHOT"
  :description "Mokkameister the coffee bot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.3.1"]
                 [clj-http "1.1.2"]
                 [clj-time "0.9.0"]
                 [cljs-ajax "0.5.1"]
                 [compojure "1.1.8"]
                 [environ "1.0.0"]
                 [liberator "0.13"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145" :scope "provided"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [reagent "0.5.1"]
                 [ring-cors "0.1.7"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [yesql "0.5.1"]]

  :plugins [[lein-environ "1.0.0"]
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
                     :source-map    "resources/public/js/app.js.map"
                     :optimizations :none
                     :pretty-print  true}}

    :uberjar {:source-paths ["src/cljs" "src/cljc"]
              :jar true
              :compiler {:output-to      "resources/public/js/app.js"
                         :pretty-print   false
                         :optimizations  :advanced}}}}

  :profiles {:dev {:env {:dev true}}
             :uberjar {:env {:production true}
                       :omit-source true
                       :aot :all
                       :prep-tasks ["compile" ["cljsbuild" "once" "uberjar"]]}}
  )
