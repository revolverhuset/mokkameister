(ns mokkameister.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.stacktrace :as trace]
            [environ.core :refer [env]]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :as json]))

(defroutes app
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "Brillekake"}))

(defn wrap-middlewares [app]
  (-> app
      trace/wrap-stacktrace))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-middlewares #'app) {:port port :join? false})))

;; Dev:
(comment
  (.stop server)
  (def server (-main)))
