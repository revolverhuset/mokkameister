(ns mokkameister.resources.button
  (:require [liberator.core :refer [defresource]]
            [mokkameister.brew :refer [start-brewing!]]
            [mokkameister.system :refer [system]]))

(defn- valid-button-token? [ctx]
  (let [token (get-in ctx [:request :params :secret])]
    (= token (system :button-token))))

(defn- handle-button-post [ctx]
  ;; Just produce a faux slack /coffee event map for now..
  (let [event {:channel "#penthouse"
               :slack-user "nokon"
               :brew-time 5
               :coffee-type :regular}]
    (start-brewing! event))
  "OK")

(defresource coffee-button
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :authorized? valid-button-token?
  :post! handle-button-post)
