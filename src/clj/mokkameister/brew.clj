(ns mokkameister.brew
  (:require [clojure.core.async :refer [<! go timeout]]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as tc]
            [mokkameister
             [matrix :as matrix]
             [pusher :as pusher]
             [slack :as slack]
             [system :refer [system]]
             [train :refer [rand-train]]
             [random :refer [rand-nth-weighted]]]
            [mokkameister.db.persistence :refer [brew-stats persist-brew! find-last-regular-coffee]]))

(def ^:private channel "#bergen")

(def ^:private msg-coffee-count
  {1 "Dagens fyrste kaffi! "
   2 "No er det snart kaffi igjen, "
   3 "Kaffi no igjen? "
   4 "Eg gir meg ende øve, fjerde kaffien i dag? "
   5 "Femte gong? "
   6 "clj.mokkameister.Exception: Coffee Overflow (brew.clj:18) "})

(def ^:private mokkameister-link
  "https://revolverhuset.no/kaffi/")

(def ^:private brewing-equipment
  [["traktaren" 60]
   ["kjelo" 10]
   ["mokkameister" 20]
   ["nespressomaskina" 5]
   ["grutkokaren" 5]
   ["grutpresså" 5]
   ["fluidumgeneratoren" 5]
   ["kaffikokaren" 30]])

(def ^:private dummy-brew ;; For stupid legacy reasons
  {:channel channel
   :slack-user "nokon"
   :brew-time 5
   :coffee-type :regular})

(defn- coffee-message-starting [{:keys [brew-time]} today-count]
  (format "God nyhendnad folket! %sNokon starta nett %s, kaffi om få minuttar!"
          (msg-coffee-count today-count "")
          (rand-nth-weighted brewing-equipment)))

(defn- coffee-message-finished []
  (rand-train))

(defn- notify-start! [brew]
  (let [stats        (brew-stats)
        today-count  (get-in stats [:regular :today])
        total-count  (get-in stats [:regular :total])
        starting-msg (coffee-message-starting brew today-count)
        msg          (str starting-msg " - " mokkameister-link)]
    (slack/notify msg :channel channel)))

(defn- notify-done! []
  (let [msg (coffee-message-finished)]
    (slack/notify msg :channel channel)))

(defn- parse-datetime [str]
  (f/parse (f/formatter :mysql) str))

(defn- currently-brewing? []
  (when-let [last-brew (first (find-last-regular-coffee))]
    (-> (:created last-brew)
        (parse-datetime)
        (t/interval (t/now))
        (t/in-minutes)
        (< 2))))

(defn- notify-already-brewing! []
  (let [msg "Ro dykk ned! Kaffien kokar allereie!!1"]
    (slack/notify msg :channel channel)))

(defn- persist-and-notify-new-brew! []
  (let [brew     (persist-brew! dummy-brew)]
    (notify-start! brew)
    (pusher/push! "coffee" "brewing" "start")))

(defn start-brewing! []
  (if (currently-brewing?)
    (notify-already-brewing!)
    (persist-and-notify-new-brew!)))

(defn finish-brewing! []
  (notify-done!)
  (pusher/push! "coffee" "brewing" "finish"))
