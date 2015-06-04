(ns mokkameister.coffee-status-resource
  (:require [environ.core :refer [env]]
            [cheshire.core :as json]
            [clj-time.core :as t :refer [hours ago]]
            [clj-time.format :as f]
            [clj-time.coerce :as c :refer [to-date]]
            [liberator.core :refer [resource defresource]]))

(defn- dummy-status [_]
  {:lastbrew {:time (-> 2 hours ago to-date)
              :by "stian"}})

(defn- dummy-stats [_]
  {:stats {:today 1
           :week  5
           :month 21}})

(defresource coffee-status
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string dummy-status))

(defresource coffee-stats
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (comp json/generate-string dummy-stats))
