(ns mokkameister.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :as json]
            [mokkameister.slack :as slack]
            [mokkameister.util :refer [parse-int]]))

(defn- coffee-message-starting [user minutes]
  (format "God nyhendnad folket! %s starta nett kaffitraktaren, kaffi om %d minuttar!" user minutes))

(defn- coffee-message-finished [user]
  (format "Det er kaffi å få på kjøken! @%s" user))

(defn coffee-message-instant [user]
  (format "Den sleipe robusta-knaskaren %s har lagt seg ein snar-kaffi :/" user))

(defn handle-instant-coffee [params]
  (slack/notify (coffee-message-instant (:user_name params))))

(defn handle-regular-coffee [params]
  (let [time (or (parse-int (:text params)) 5)
        time-ms (* time 1000 60)
        user (:user_name params)]
    (slack/notify (coffee-message-starting user time))
    (slack/delayed-notify time-ms (coffee-message-finished user))))

(defn handle-slack-coffee [params]
  (if (= (:text params) "instant")
    (handle-instant-coffee params)
    (handle-regular-coffee params))
  {:status 200
   :body ""})

(defroutes app
  (POST "/slack-coffee" req
        (handle-slack-coffee (:params req)))
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "Brillekake"}))

(defn wrap-middlewares [app]
  (-> app
      wrap-stacktrace
      (wrap-defaults api-defaults)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-middlewares #'app) {:port port :join? false})))

;; Dev:
(comment
  (.stop server)
  (def server (-main)))
