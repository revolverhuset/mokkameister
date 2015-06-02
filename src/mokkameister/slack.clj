(ns mokkameister.slack
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]))

(def slack-url (env :slack-url))

(defn notify [message]
  (let [payload {:text message
                 :username "Mokkameister 9000"
                 :icon_emoji ":coffee:"
                 :channel "#testroompleaseignore"}]
    (client/post slack-url {:form-params payload
                            :content-type :json})))
