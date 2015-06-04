(ns mokkameister.web
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [liberator.core :refer [resource defresource]]
            [mokkameister.slack-coffee-resource :refer [slack-coffee]]
            [mokkameister.coffee-status-resource :refer [coffee-status coffee-stats]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defroutes app
  (ANY "/slack-coffee" [] slack-coffee)
  (ANY "/status" [] coffee-status)
  (ANY "/stats" [] coffee-stats)
  (route/resources "/"))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler (update-in req [:uri] #(if (= "/" %) "/index.html" %)))))

(defn wrap-middlewares [app]
  (-> app
      (wrap-dir-index)
      (wrap-defaults api-defaults)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-middlewares #'app) {:port port :join? false})))

;; Lein ring handler
(def handler (-> app wrap-middlewares))

;; Dev:
(comment
  (.stop server)
  (def server (-main)))
