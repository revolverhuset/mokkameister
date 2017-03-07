(ns mokkameister.brew-status
  (:require [mokkameister.loading :refer [loading-gif]]))

(def ^:private fresh-coffee-minutes 5)

(defn- fresh-brew? [brew]
  (let [duration (:brew-time brew)
        start    (:created brew)]
    (-> (js/moment start)
        (.add duration "m")
        (.add fresh-coffee-minutes "m")
        (.isAfter (js/moment)))))

(defn- isbrewing? [brew]
  (let [duration (:brew-time brew)
        start    (:created brew)]
    (-> (js/moment start)
        (.add duration "m")
        (.isAfter (js/moment)))))

(defn- friendly-brew-relative-time [brew]
  (let [duration (:brew-time brew)
        start    (:created brew)]
     (-> (js/moment start)
              (.add duration "m")
              (.locale "nn")
              (.fromNow))))

;;; Components:

(defn- brewing [brew]
  [:p.lead.alert.alert-danger {:role "alert"}
   [:strong.blink "Brygg Alarm!"]
   (str " Kaffi klar " (friendly-brew-relative-time brew) "..")])

(defn- fresh-brew [brew]
  [:p.lead.alert.alert-success {:role "alert"}
   [:strong.blink "Fersk kaffi!"]
   " Nybrygga kaffi paa kjøken.."])

(defn- last-brew [brew]
  [:p.lead
   (str "Sist brygg blei laga " (friendly-brew-relative-time brew) ".")])

(defn brew-status [brew]
  (cond (nil? brew)        (loading-gif)
        (isbrewing? brew)  (brewing brew)
        (fresh-brew? brew) (fresh-brew brew)
        :else              (last-brew brew)))
