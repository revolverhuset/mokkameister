(ns mokkameister.resources.coffee
  "API for web page (status / stats)"
  (:require [cheshire.core :as json]
            [liberator.core :refer [defresource]]
            [mokkameister.db.persistence :refer [brew-stats latest-brews month-stats]]))

(defn- status [_]
  {:latest (latest-brews)})

(defn- stats [_]
  {:brews (brew-stats)
   :month-stats (month-stats)})

(defresource coffee-status
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string status))

(defresource coffee-stats
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string stats))
