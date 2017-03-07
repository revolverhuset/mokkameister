(ns mokkameister.resources.coffee
  "API for web page (status / stats)"
  (:require [cheshire.core :as json]
            [cheshire.generate :as generate]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [liberator.core :refer [defresource]]
            [mokkameister.db.persistence :refer [brew-stats find-last-regular-coffee month-stats]]))

(defn add-common-json-encoders!*
  "Non-memoize version of add-common-json-encoders!"
  []
  (generate/add-encoder
    org.joda.time.DateTime
    (fn [data jsonGenerator]
      (.writeString jsonGenerator (c/to-string data)))))

(add-common-json-encoders!*)

(defn- status [_]
  (let [[latest] (find-last-regular-coffee)]
    {:latest latest}))

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
