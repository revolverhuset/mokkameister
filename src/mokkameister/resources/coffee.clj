(ns mokkameister.resources.coffee
  "API for web page (status / stats)"
  (:require [cheshire.core :as json]
            [liberator.core :refer [defresource]]
            [mokkameister.db.persistence :refer [brew-stats latest-brews]]
            [mokkameister.system :refer [system]]))

(defn- status [_]
  {:latest (latest-brews (system :db))})

(defn- stats [_]
  (brew-stats (system :db)))

(defresource coffee-status
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string status))

(defresource coffee-stats
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string stats))
