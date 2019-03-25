(ns mokkameister.system
  (:require [environ.core :refer [env]]))

(defn- db-spec []
  (or (env :database-url)
      (read-string (slurp "dev-db.edn")) ; fall back to edn config
      (throw (Exception. "Missing DATABASE_URL"))))

(defn- slack-url []
  (or (env :slack-url)
      (throw (Exception. "Missing SLACK_URL environment variable"))))

(defn- slack-token []
  (or (env :slack-token)
      (throw (Exception. "Missing SLACK_TOKEN environment variable"))))

(defn- button-token []
  (or (env :button-token)
      (throw (Exception. "Missing BUTTON_TOKEN environment variable"))))

(defn- pusher-url []
  (or (env :pusher-url)
      (throw (Exception. "Missing PUSHER_URL environment variable"))))

(defn- mqtt-uri []
  (or (env :cloudmqtt-url)
      (throw (Exception. "Missing CLOUDMQTT_URL environment variable"))))

(defn- web-port []
  (or (env :port)
      5000))

(defn- matrix-username []
  (or (env :matrix-username)
      (throw (Exception. "Missing MATRIX_USERNAME environment variable"))))

(defn- matrix-password []
  (or (env :matrix-password)
      (throw (Exception. "Missing MATRIX_PASSWORD environment variable"))))

(defn- matrix-room []
  (or (env :matrix-room)
      (throw (Exception. "Missing MATRIX_ROOM environment variable"))))

(defn system [what]
  (case what
    :db (db-spec)
    :matrix-username (matrix-username)
    :matrix-password (matrix-password)
    :matrix-room (matrix-room)
    :mqtt-uri (mqtt-uri)
    :slack-url (slack-url)
    :slack-token (slack-token)
    :web-port (web-port)
    :button-token (button-token)
    :pusher-url (pusher-url)))
