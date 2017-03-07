(ns mokkameister.notification)

(defn notification-supported? []
  js/Notification)

(defn notification-permission []
  (.-permission js/Notification))

(defn notification-permitted? []
  (= "granted" (notification-permission)))

(defn notification-request-permission! []
  (.requestPermission
   js/Notification
   (fn [_] true)))

(defn create-notification
  [{:keys [title body icon sound]
    :or {title ""
         body  ""
         icon "img/coffee.png"}}]
  (when (and (notification-supported?)
             (notification-permitted?))
    (let [n (js/Notification. title (clj->js {:body body
                                              :icon icon}))]
      (when sound
        (.play (js/Audio. sound)))
      n)))


(defn notify-brew-starting! []
  (create-notification {:title "Mokkameister 9000"
                        :body "Nokon satt nett paa kaffien! 🎉"}))

(defn notify-brew-finished! []
  (create-notification {:title "Mokkameister 9000"
                        :body "🚂☕️❤️☕️❤️☕️❤️☕️💨"}))
