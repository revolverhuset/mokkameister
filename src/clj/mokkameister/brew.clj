(ns mokkameister.brew
  (:require [clojure.core.async :refer [<! go timeout]]
            [mokkameister
             [pusher :as pusher]
             [slack :as slack]
             [system :refer [system]]
             [util :refer [parse-int]]
             [random :refer [rand-nth-weighted]]
             [train :refer [train]]]
            [mokkameister.db.persistence :refer [brew-stats persist-brew!]]))

(def ^:private channel "#penthouse")

(def ^:private msg-coffee-count
  {1 "Dagens fyrste kaffi! "
   2 "No er det snart kaffi igjen, "
   3 "Kaffi no igjen? "
   4 "Eg gir meg ende øve, fjerde kaffien i dag? "
   5 "Femte gong? "})

(def ^:private mokkameister-link
  "<https://revolverhuset.no/kaffi/|revolverhuset.no/kaffi>")

(defn- coffee-message-starting [{:keys [slack-user brew-time]} today-count]
  (format "God nyhendnad folket! %s%s starta nett traktaren, kaffi om %d minuttar!"
          (msg-coffee-count today-count "") slack-user brew-time))

(def ^:private flat-rand (comp rand-nth flatten))

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
    (train ":syringe:" ":hocho:")
    ":steam_locomotive::coffee::coffee::running::running::running::dash:"
    "All aboard the coffee train\n:steam_locomotive::coffee::coffee::running::running::running::dash:"
    "Det er kaffi å få på kjøken!"
    "KEEP CALM THE COFFEE IS READY!"
    "PLEASE PROCEED IN ORDERLY FASHION TO RECEIVE COFFEE :syringe:"]))

(defn- notify-start! [brew]
  (let [stats        (brew-stats)
        today-count  (get-in stats [:regular :today])
        total-count  (get-in stats [:regular :total])
        starting-msg (coffee-message-starting brew today-count)
        msg          (str starting-msg " - " mokkameister-link)]
    (slack/notify msg :channel channel)))

(defn- notify-done! [brew]
  (let [msg (coffee-message-finished)]
    (slack/notify msg :channel channel)))

(defn finish-brewing! [brew]
  (notify-done! brew))

(defmacro delayed!
  "Execute body after time-ms"
  [time-ms & body]
  `(go (<! (timeout ~time-ms))
       ~@body))

(defn start-brewing! [in-brew]
  (let [brew     (persist-brew! in-brew)
        delay-ms (* (:brew-time brew) 60 50)]
    (notify-start! brew)
    (delayed! delay-ms (finish-brewing! brew))))
