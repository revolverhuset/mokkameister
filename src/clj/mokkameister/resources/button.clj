(ns mokkameister.resources.button
  (:require [liberator.core :refer [defresource]]
            [mokkameister.brew :refer [start-brewing!]]
            [mokkameister.slack :refer [notify]]
            [mokkameister.system :refer [system]]))

(defn- valid-button-token? [ctx]
  (let [token (get-in ctx [:request :params :secret])]
    (= token (system :button-token))))

(defn- handle-button [ctx]
  (start-brewing!)
  "OK")

(defresource coffee-button
  :available-media-types ["text/plain"]
  :allowed-methods [:post :get]
  :authorized? valid-button-token?
  :post! handle-button
  ;; New button uses GET, seems like SendToHTTP
  ;; command is very basic :(
  :handle-ok handle-button)
