(ns mokkameister.web
  (:require [compojure
             [core :refer [ANY defroutes]]
             [route :as route]]
            [mokkameister.resources
             [coffee :refer [coffee-stats coffee-status]]
             [button :refer [coffee-button]]]
            [mokkameister.system :refer [system]]
            [mokkameister.middleware :refer [ignore-trailing-slash]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware
             [cors :refer [wrap-cors]]
             [defaults :refer [api-defaults wrap-defaults]]]))

(defroutes app
  (ANY "/status" [] coffee-status)
  (ANY "/stats" [] coffee-stats)
  (ANY "/coffee-button" [] coffee-button)
  (route/resources "/"))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler (update-in req [:uri] #(if (= "/" %) "/index.html" %)))))

(defn wrap-middlewares [app]
  (-> app
      (wrap-dir-index)
      (wrap-defaults api-defaults)
      (ignore-trailing-slash)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])))

(defn -main [& [port]]
  (let [port (Integer. (or port (system :web-port)))]
    (jetty/run-jetty (wrap-middlewares #'app) {:port port :join? false})))

;; Lein ring handler
(def handler (-> app wrap-middlewares))

;; Dev:
(comment
  (.stop server)
  (def server (-main)))
