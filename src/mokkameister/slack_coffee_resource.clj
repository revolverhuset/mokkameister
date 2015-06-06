(ns mokkameister.slack-coffee-resource
  (:require [liberator.core :refer [defresource]]
            [mokkameister.slack :as slack]
            [mokkameister.persistence :refer [persist-brew!]]
            [mokkameister.system :refer [system]]
            [mokkameister.util :refer [parse-int]]))

(defn- coffee-message-starting [{:keys [slack-user brew-time]}]
  (format "God nyhendnad folket! %s starta nett kaffitraktaren, kaffi om %d minuttar!"
          slack-user brew-time))

(defn- coffee-message-finished [{:keys [slack-user]}]
  (format "Det er kaffi å få på kjøken! @%s" slack-user))

(defn- coffee-message-instant [{:keys [slack-user]}]
  (format "Den sleipe robusta-knaskaren %s har lagt seg ein snar-kaffi :/" slack-user))


(defmulti handle-slack-coffee :coffee-type)

(defmethod handle-slack-coffee :instant [{:keys [channel] :as event}]
  (persist-brew! event)
  (let [msg (coffee-message-instant event)]
    (slack/notify msg :channel channel)))

(defmethod handle-slack-coffee :regular [{:keys [channel time-ms] :as event}]
  (persist-brew! event)
  (let [now-msg   (coffee-message-starting event)
        later-msg (coffee-message-finished event)]
    (slack/notify now-msg :channel channel)
    (slack/delayed-notify time-ms later-msg :channel channel)))


(defn parse-slack-coffee-event [{:keys [channel_id text user_name]}]
  (let [event {:channel    channel_id
               :slack-user user_name}]
    (if (= text "instant")
      (assoc event :coffee-type :instant, :brew-time 0)
      (let [time    (or (parse-int text) 5)
            time-ms (* time 1000 60)]
        (assoc event :coffee-type :regular, :brew-time time, :time-ms time-ms)))))

(defn valid-slack-token? [ctx]
  (let [token (get-in ctx [:request :params :token])]
    (= token (system :slack-token))))

(defresource slack-coffee
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :authorized? valid-slack-token?
  :post! (fn [{{:keys [params]} :request}]
           (-> params
               parse-slack-coffee-event
               handle-slack-coffee) ""))
