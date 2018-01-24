(ns mokkameister.resources.button
  (:require [liberator.core :refer [defresource]]
            [mokkameister.brew :refer [start-brewing!]]
            [mokkameister.slack :refer [notify]]
            [mokkameister.system :refer [system]]))

(defn- valid-button-token? [ctx]
  (let [token (get-in ctx [:request :params :secret])]
    (= token (system :button-token))))

(defn- handle-old-button-post [ctx]
  (let [brew {:channel "#penthouse"
              :slack-user "nokon"
              :brew-time 5
              :coffee-type :regular}]
    (start-brewing! brew))
  "OK")

(defn- handle-button-get [ctx]
  ;; New button uses GET, seems like SendToHTTP
  ;; command is very basic :(
  (notify "Kaffi!" :channel "#testroompleaseignore")
  "OK")

(defresource coffee-button
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :authorized? valid-button-token?
  :post! handle-old-button-post
  :handle-ok handle-button-get)
