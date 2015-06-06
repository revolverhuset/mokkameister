(ns mokkameister.coffee-status-resource
  (:require [environ.core :refer [env]]
            [cheshire.core :as json]
            [clj-time.core :as t :refer [hours ago]]
            [clj-time.format :as f]
            [clj-time.coerce :as c :refer [to-date]]
            [liberator.core :refer [resource defresource]]
            [mokkameister.persistence :refer [find-latest-brewings latest-brews brew-stats]]
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
