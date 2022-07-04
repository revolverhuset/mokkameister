(ns mokkameister.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]
            [lambdaisland.uri :as uri]
            [mokkameister.brew :refer [start-brewing! finish-brewing!]]
            [mokkameister.system :refer [system]]))

(def THRESHOLD_WATT 1000)

(def topic "shellies/shellyplug-s-882239/relay/0/power")

(defn- started-percolating? [history]
  (->> history
       (take-last 3)
       (mapv #(<= THRESHOLD_WATT %))
       (= [false false true])))

(defn- brew-finished? [history]
  (->> history
       (take-last 6)
       (mapv #(<= THRESHOLD_WATT %))
       (= [true true false false false false])))

(defn- connect! []
  (let [
        my-uri (uri/uri (system :mqtt-uri))
        short-url (str "tcp://" (:host my-uri) ":" (:port my-uri))
        creds {:username (:user my-uri)
               :password (:password my-uri)}
        conn (mh/connect short-url {:opts creds})]
    conn))

(defn connect-and-process! []
  (let [conn (connect!)
        history (atom (vec [0 0 0 0 0 0]))
        on-receive (fn [^String topic _ ^bytes payload]
                     (let [wattage (Float/parseFloat (String. payload "UTF-8"))]
                       (do
                         (swap! history
                                #(->> (conj % wattage)
                                      (drop 1)
                                      (vec)))
                         (cond
                           (started-percolating? @history) (start-brewing!)
                           (brew-finished? @history) (finish-brewing!)))))]
    (mh/subscribe conn {topic 0} on-receive)))

;; (connect-and-process!)
