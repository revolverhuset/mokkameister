(ns mokkameister.brew
  (:require [clojure.core.async :refer [<! go timeout]]
            [mokkameister
             [pusher :as pusher]
             [slack :as slack]
             [system :refer [system]]
             [train :refer [rand-train]]]
            [mokkameister.db.persistence :refer [brew-stats persist-brew!]]))

(def ^:private channel "#penthouse")

(def ^:private msg-coffee-count
  {1 "Dagens fyrste kaffi! "
   2 "No er det snart kaffi igjen, "
   3 "Kaffi no igjen? "
   4 "Eg gir meg ende øve, fjerde kaffien i dag? "
   5 "Femte gong? "
   6 "clj.mokkameister.Exception: Coffee Overflow (brew.clj:18) "})

(def ^:private mokkameister-link
  "<https://revolverhuset.no/kaffi/|revolverhuset.no/kaffi>")

(defn- coffee-message-starting [{:keys [slack-user brew-time]} today-count]
  (format "God nyhendnad folket! %s%s starta nett traktaren, kaffi om %d minuttar!"
          (msg-coffee-count today-count "") slack-user brew-time))

(defn- coffee-message-finished []
  (rand-train))

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

(defn- finish-brewing! [brew]
  (notify-done! brew)
  (pusher/push! "coffee" "brewing" "finish"))

(defmacro delayed!
  "Execute body after time-ms"
  [time-ms & body]
  `(go (<! (timeout ~time-ms))
       ~@body))

(defn start-brewing! [in-brew]
  (let [brew     (persist-brew! in-brew)
        delay-ms (* (:brew-time brew) 60 1000)]
    (notify-start! brew)
    (pusher/push! "coffee" "brewing" "start")
    (delayed! delay-ms (finish-brewing! brew))))
