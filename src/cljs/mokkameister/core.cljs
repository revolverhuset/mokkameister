(ns mokkameister.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [timeout <!]]))

(enable-console-print!)

(def state (atom {}))

(add-watch state :listener
           (fn [key atom old new]
             (prn "New state:", new)))

(GET "/status"
     {:response-format :json
      :keywords? true
      :handler (fn [data] (swap! state #(merge % data)))})

(GET "/stats"
     {:response-format :json
      :keywords? true
      :handler (fn [data] (swap! state #(assoc % :stats data)))})

(defn friendly-time-ago [time]
  (-> (js/moment time)
      (.locale "nn") ;; Nynorsk!
      (.fromNow)))


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

(do
  (reagent/render-component [last-brew] (.getElementById js/document "last-brew"))
  (reagent/render-component [stats] (.getElementById js/document "stats"))
  (println "Running!"))
