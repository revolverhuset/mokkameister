(ns mokkameister.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [timeout <!]]))

(enable-console-print!)

(defonce state (atom {}))

(add-watch state :listener
           (fn [key atom old new]
             (prn "New state:", new)))

(defn- fetch-data! []
  (GET "/status"
       {:response-format :json
        :keywords? true
        :handler (fn [data] (swap! state #(merge % data)))})
  (GET "/stats"
       {:response-format :json
        :keywords? true
        :handler (fn [data] (swap! state #(assoc % :stats data)))}))

(defn friendly-time-ago [time]
  (-> (js/moment time)
      (.locale "nn") ;; Nynorsk!
      (.fromNow)))

(defn- play-siren! []
  (let [audio (js/Audio. "/alarm.mp3")]
    (.play audio)))

(defn- loading-gif []
  [:div.loading
   [:img {:src "img/loading.gif"}]])

(defn last-brew []
  (let [timer (atom 0)]
    (fn []
      (go (<! (timeout 5000))
          (swap! timer inc))
      @timer ;; Ugly... deref atom to re-eval component.. :(
      (if-let [time (get-in @state [:latest :regular :created])]
        [:div (str "Sist brygg blei laga " (friendly-time-ago time) ".")]
        (loading-gif)))))

(defn alarm []
  (when-let [message (:alarm @state)]
    [:div.alert.alert-danger.alarm {:role "alert"}
     [:strong.blink "ALARM! ALARM! "] message]))

(defn stats []
  (if-let [stats (get-in @state [:stats :regular])]
    (let [stat-lines {:today "I dag"
                      :yesterday "I går"
                      :thisweek "Denne veken"
                      :lastweek "Forrige veke"
                      :thismonth "Denne månaden"
                      :lastmonth "Forrige månad"
                      :total "Totalt"}]
      [:table.table
       [:thead
        [:tr
         [:th "Periode"]
         [:th "Antall"]]]
       [:tbody
        (for [[line description] stat-lines]
          [:tr {:key line}
           [:td description]
           [:td (get stats line)]])]])
    (loading-gif)))

(defn trigger-alarm! [message]
  (swap! state #(assoc % :alarm message))
  (fetch-data!)
  (play-siren!)
  (go (<! (timeout 30000))
      (swap! state #(dissoc % :alarm))))

(defn subscribe-pusher! []
  (when-not (:pusher @state)
    (let [settings (js-obj "cluster" "eu",
                           "encrypted" true)
          socket (new js/Pusher "74106d5bea6fc6c2ed86" settings)
          channel (.subscribe socket "coffee")]
      (.bind channel "coffee" trigger-alarm!)
      (swap! state #(assoc % :pusher socket)))))

(do
  (reagent/render-component [last-brew] (.getElementById js/document "last-brew"))
  (reagent/render-component [stats] (.getElementById js/document "stats"))
  (reagent/render-component [alarm] (.getElementById js/document "alarm"))
  (fetch-data!)
  (subscribe-pusher!)
  (println "Running!"))
