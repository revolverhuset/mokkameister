(ns mokkameister.matrix
  (:require [clj-http.client :as client]
            [clojure.core.async :refer [<! go timeout]]
            [mokkameister.system :refer [system]])
  (:import [com.vdurmont.emoji EmojiParser]))

(def ^:private base-url "https://matrix.org/_matrix/client/r0")
(def ^:private access-token-cache (atom ""))

(defn- login! []
  (let [payload {:type "m.login.password"
                 :user (system :matrix-username)
                 :password (system :matrix-password)
                 :initial_device_display_name "Mokkameister 9000"}
        options {:content-type :json
                 :accept :json
                 :form-params payload
                 :as :json}
        response (client/post (str base-url "/login") options)
        access-token (get-in response [:body :access_token])]
    (reset! access-token-cache access-token)
    access-token))

(defn- put! [url payload]
  (client/put url {:content-type :json
                   :headers {:authorization (str "Bearer " @access-token-cache)}
                   :accept :json
                   :form-params payload}))

(defn- ensure-login-and-put! [url payload]
  (try
    (put! url payload)
    (catch clojure.lang.ExceptionInfo e
      (do
        (login!)
        (put! url payload)))))

(defn notify
  "Send a message to matrix. Optional arguments: room
  Encodes short form emoji to unicode"
  [message & {:keys [room]
              :or {room (system :matrix-room)}}]
  (let [timestamp (System/currentTimeMillis)
        url (str base-url "/rooms/" room "/send/m.room.message/" timestamp)
        payload {:msgtype "m.text"
                 :body (EmojiParser/parseToUnicode message)}]
    (ensure-login-and-put! url payload)))
