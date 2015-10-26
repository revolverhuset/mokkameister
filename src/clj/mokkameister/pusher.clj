(ns mokkameister.pusher
  (:require [clj-http.client :as client]
            [clojure.core.async :refer [<! go timeout]]
            [mokkameister.system :refer [system]])
  (:import com.pusher.rest.Pusher))

(defn push!
  "Push message to Pusher.io"
  [channel msg-name msg-data]
  (let [pusher (Pusher. (system :pusher-url))
        result (.trigger pusher channel msg-name msg-data)]
    (= com.pusher.rest.data.Result$Status/SUCCESS
       (.getStatus result))))

;; (push! "coffee" "coffee" "kake")
