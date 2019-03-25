(ns mokkameister.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]
            [lambdaisland.uri :as uri]
            [mokkameister.system :refer [system]]))

(def topic "mokkameister/alarm/coffee/trigger/set")
(def trigger-payload "true")

(defn trigger-alarm! []
  (let [my-uri (uri/uri (system :mqtt-uri))
        short-url (str "tcp://" (:host my-uri) ":" (:port my-uri))
        creds {:username (:user my-uri)
               :password (:password my-uri)}
        conn (mh/connect short-url {:opts creds})]
    (mh/publish conn topic trigger-payload)
    (mh/disconnect conn)))
