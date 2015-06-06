(ns mokkameister.slack
  (:require [mokkameister.system :refer [system]]
            [clj-http.client :as client]
            [environ.core :refer [env]]
            [clojure.core.async :as a :refer [go timeout <!]]))

(defn notify
  "Send message to slack. Optional arguments: channel, emoji, username."
  [message & {:keys [channel username emoji]
              :or {channel  "#testroompleaseignore"
                   emoji    ":coffee:"
                   username "Mokkameister 9000"}}]
  (let [payload {:text       message
                 :username   username
                 :icon_emoji emoji
                 :channel    channel}]
    (client/post (system :slack-url) {:content-type :json
                                      :form-params payload})))

(defn delayed-notify
  "Send a delayed (millisec) message to slack."
  [delay-ms & args]
  (go (<! (timeout delay-ms))
      (apply notify args)))
