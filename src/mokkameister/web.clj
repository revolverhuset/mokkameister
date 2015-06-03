(ns mokkameister.web
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [environ.core :refer [env]]
            [liberator.core :refer [resource defresource]]
            [mokkameister.slack :as slack]
            [mokkameister.util :refer [parse-int]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn- coffee-message-starting [user minutes]
  (format "God nyhendnad folket! %s starta nett kaffitraktaren, kaffi om %d minuttar!" user minutes))

(defn- coffee-message-finished [user]
  (format "Det er kaffi å få på kjøken! @%s" user))

(defn coffee-message-instant [user]
  (format "Den sleipe robusta-knaskaren %s har lagt seg ein snar-kaffi :/" user))


(defmulti handle-slack-coffee :coffee-type)

(defmethod handle-slack-coffee :instant [{:keys [user channel]}]
  (let [msg (coffee-message-instant user)]
    (slack/notify msg :channel channel)))

(defmethod handle-slack-coffee :regular [{:keys [user channel time time-ms]}]
  (let [now-msg   (coffee-message-starting user time)
        later-msg (coffee-message-finished user)]
    (slack/notify now-msg :channel channel)
    (slack/delayed-notify time-ms later-msg :channel channel)))


(defn parse-slack-coffee-event [{:keys [channel_id text token user_name]}]
  (let [event {:channel channel_id
               :token token
               :user user_name}]
    (if (= text "instant")
      (assoc event :coffee-type :instant)
      (let [time (or (parse-int text) 5)
            time-ms (* time 1000 60)]
        (assoc event :coffee-type :regular, :time time, :time-ms time-ms)))))

(defresource slack-coffee
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :post! (fn [{{:keys [params]} :request}]
           (-> params
               parse-slack-coffee-event
               handle-slack-coffee) ""))

(defroutes app
  (ANY "/slack-coffee" [] slack-coffee)
  (GET "/" [] {:status 200, :body "Brillekake"}))

(defn wrap-middlewares [app]
  (-> app
      ;; wrap-stacktrace
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
