(ns mokkameister.brew
  (:require [clojure.core.async :refer [<! go timeout]]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [mokkameister
             [pusher :as pusher]
             [slack :as slack]
             [system :refer [system]]
             [train :refer [rand-train]]]
            [mokkameister.db.persistence :refer [brew-stats persist-brew! find-last-regular-coffee]]))

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

(defn- currently-brewing? []
  (when-let [last-brew (first (find-last-regular-coffee))]
    (-> (:created last-brew)
        (tc/from-sql-time)
        (t/interval (t/now))
        (t/in-minutes)
        (< 2))))

(defn- notify-already-brewing! []
  (let [msg "Ro dykk ned! Kaffien kokar allereie!!1"]
    (slack/notify msg :channel channel)))

(defn- persist-and-notify-new-brew! [in-brew]
  (let [brew     (persist-brew! in-brew)
        delay-ms (* (:brew-time brew) 60 1000)]
    (notify-start! brew)
    (pusher/push! "coffee" "brewing" "start")
    (delayed! delay-ms (finish-brewing! brew))))

(defn start-brewing! [in-brew]
  (if (currently-brewing?)
    (notify-already-brewing!)
    (persist-and-notify-new-brew! in-brew)))
