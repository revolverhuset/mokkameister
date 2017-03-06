(ns mokkameister.resources.button
  (:require [liberator.core :refer [defresource]]
            [mokkameister.resources.slack :refer [handle-slack-coffee]]
            [mokkameister.system :refer [system]]))

(defn- valid-button-token? [ctx]
  (let [token (get-in ctx [:request :params :secret])]
    (= token (system :button-token))))

(defn- handle-button-post [ctx]
  ;; Just produce a faux slack /coffee event map for now..
  (let [event {:channel "#penthouse"
               :slack-user "nokon"
               :time-ms (* 5 1000 60)
               :brew-time 5
               :coffee-type :regular}]
    (prn event)
    (handle-slack-coffee event))
  "OK")

(defresource coffee-button
  :available-media-types ["text/plain"]
  :allowed-methods [:post]
  :authorized? valid-button-token?
  :post! handle-button-post)
