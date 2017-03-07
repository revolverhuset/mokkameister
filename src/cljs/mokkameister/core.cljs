(ns mokkameister.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [mokkameister.random :refer [rand-nth-weighted]]
            [mokkameister.brew-status :refer [brew-status]]
            [mokkameister.loading :refer [loading-gif]]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [timeout <!]]
            [clojure.string :as string :refer [join]]))

(enable-console-print!)

(defonce state (atom {}))
(defonce timer (atom 0))

#_(add-watch state :listener
           (fn [key atom old new]
             (prn "New state:", new)))

(defn- fetch-data! []
  (GET "status"
       {:response-format :json
        :keywords? true
        :handler (fn [data] (swap! state #(merge % data)))})
  (GET "stats"
       {:response-format :json
        :keywords? true
        :handler (fn [data] (swap! state #(assoc % :stats data)))}))

(defn brew-status-on-timer []
  (let [brew (get-in @state [:latest])
        ticks @timer] ; deref timer forces eval
    (brew-status brew)))

(defn stats []
  (if-let [stats (get-in @state [:stats :brews :regular])]
    (let [stat-lines {:today "I dag"
                      :yesterday "I går"
                      :thisweek "Denne veken"
                      :lastweek "Forrige veke"
                      :thismonth "Denne månaden"
                      :lastmonth "Forrige månad"
                      :avgmonth "Gj.snitt månad"
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

(defn chart [chart-data]
  (let [month-count (map :count chart-data)]
    [:pre.chart (js/chart (clj->js month-count)
                          (clj->js {:width 85}))]))

(defn- chart-title []
  (let [brew (rand-nth-weighted {"Brygg" 8, "Mikrodosar" 2, "Kaffidoktorar" 1})
        per (rand-nth-weighted {"per" 2, "kvar" 1})
        month (rand-nth-weighted {"månad" 8, "monade" 2, "månefase" 1, "nymåne" 1})]
    (join " " [brew per month])))

(defn chart-panel []
  (let [title (chart-title)
        chart-data (get-in @state [:stats :month-stats])]
    [:div.panel.panel-info
     [:div.panel-heading
      [:h3.panel-title title]]
     [:div.panel-body (if chart-data
                        (chart chart-data)
                        (loading-gif))]]))

(defn subscribe-pusher! []
  (when-not (:pusher @state)
    (let [settings (js-obj "cluster" "eu",
                           "encrypted" true)
          socket (new js/Pusher "74106d5bea6fc6c2ed86" settings)
          channel (.subscribe socket "coffee")]
      (.bind channel "brewing" (fn [_] (fetch-data!)))
      (swap! state #(assoc % :pusher socket)))))

(defn start-timer! []
  (go-loop []
    (<! (timeout 5000))
    (swap! timer inc)
    (recur)))

(do
  (reagent/render-component [brew-status-on-timer] (.getElementById js/document "brew-status"))
  (reagent/render-component [stats] (.getElementById js/document "stats"))
  (reagent/render-component [chart-panel] (.getElementById js/document "chart-panel"))
  (fetch-data!)
  (subscribe-pusher!)
  (start-timer!)
  (println "Running!"))
