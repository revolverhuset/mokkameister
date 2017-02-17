(ns mokkameister.resources.slack
  "Incoming webhooks from slack"
  (:require [liberator.core :refer [defresource]]
            [mokkameister
             [pusher :as pusher]
             [slack :as slack]
             [system :refer [system]]
             [util :refer [parse-int]]]
            [mokkameister.db.persistence :refer [brew-stats persist-brew!]]))

(def ^:private msg-coffee-count
  {0 "Dagens fyrste kaffi! "
   1 "No er det snart kaffi igjen, "
   2 "Kaffi no igjen? "
   3 "Eg gir meg ende øve, fjerde kaffien i dag? "
   4 "Femte gong? "})

(def ^:private mokkameister-link
  "<https://revolver.house/kaffi/|Mokkameister>")

(defn- coffee-message-starting [{:keys [slack-user brew-time]} today-count]
  (format "God nyhendnad folket! %s%s starta nett traktaren, kaffi om %d minuttar!"
          (msg-coffee-count today-count "") slack-user brew-time))

(def ^:private flat-rand (comp rand-nth flatten))

(defn- train [a b]
  (let [locomotive (flat-rand [(repeat 8 ":steam_locomotive:")
                               (repeat 2 ":bybane2:")
                               (repeat 1 ":train:")
                               (repeat 1 ":horse:")])
        ending (flat-rand [(repeat 8 ":dash:")
                           (repeat 2 nil)
                           (repeat 1 ":exclamation:")])]
    (str locomotive a b a b a b a ending)))

(defn- coffee-message-finished []
  (flat-rand
   [(repeat 2 (train ":coffee:" ":coffee:"))
    (repeat 3 (train ":coffee:" ":heart:"))
    (train ":coffeealarm:" ":coffeealarm:")
    (train ":nespresso:" ":ali:")
    (train ":ali:" ":gruff:")
    (train ":coffee:" ":syringe:")
    (train ":coffee:" ":mushroom:")
    (train ":coffee:" ":coffeepot:")
    (train ":coffee:" ":coffeealarm:")
    (train ":coffee:" ":hocho:")
    (train ":coffee:" ":timwendelboe:")
    (train ":coffee:" ":jan-richter:")
    ":steam_locomotive::coffee::coffee::running::running::running::dash:"
    "All aboard the coffee train\n:steam_locomotive::coffee::coffee::running::running::running::dash:"
    "Det er kaffi å få på kjøken!"
    "KEEP CALM THE COFFEE IS READY!"
    "PLEASE PROCEED IN ORDERLY FASHION TO RECEIVE COFFEE :syringe:"]))

(defn- coffee-message-instant [{:keys [slack-user]}]
  (format "Den sleipe robusta-knaskaren %s har lagt seg ein snar-kaffi :/" slack-user))

(defmulti handle-slack-coffee :coffee-type)

(defmethod handle-slack-coffee :instant [{:keys [channel] :as event}]
  (persist-brew! event)
  (let [msg (coffee-message-instant event)]
    (slack/notify msg :channel channel)))

(defmethod handle-slack-coffee :regular [{:keys [channel time-ms] :as event}]
  (let [stats       (brew-stats)
        today-count (get-in stats [:regular :today])
        total-count (get-in stats [:regular :total])
        now-msg     (coffee-message-starting event today-count)
        later-msg   (coffee-message-finished)]
    (persist-brew! event)
    (pusher/push! "coffee" "coffee" now-msg)
    (slack/notify (str now-msg " - " mokkameister-link) :channel channel)
    (slack/delayed-notify time-ms later-msg :channel channel)
    (when (= total-count 999)
      (slack/delayed-notify (+ time-ms 1000) "http://gph.is/2cTHbu3" :channel channel)
      (slack/delayed-notify (+ time-ms 1500) "Dette var brygg nr 1000! :tada::confetti_ball::coffee:" :channel channel))))

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

(defn valid-button-token? [ctx]
  (let [token (get-in ctx [:request :params :secret])]
    (= token (system :button-token))))

(defn handle-button-post [ctx]
  ;; Just produce a faux slack /coffee event map for now..
  (let [event {:channel "#penthouse"
               :slack-user "nokon"
               :time-ms (* 5 1000 60)
               :brew-time 5
               :coffee-type :regular}]
    (prn event)
    (handle-slack-coffee event))
  "OK")

(defresource coffee-button
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :authorized? valid-button-token?
  :post! handle-button-post)
