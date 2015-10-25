(ns mokkameister.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

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

(defn status []
  (if-let [time (get-in @state [:latest :regular :created])]
    [:div (str "Sist brygg blei laga " (friendly-time-ago time) ".")]
    (loading-gif)))

(defn stats []
  (if-let [stats (get-in @state [:stats :regular])]
    (let [stat-lines {:today "I dag"
                      :yesterday "I går"
                      :thisweek "Denne veken"
                      :lastweek "Forrige veke"
                      :thismonth "Denne månaden"
                      :lastmonth "Forrige månad"
                      :total "Totalt"}]
      [:div
       (for [[line description] stat-lines]
         [:p (str description ": " (get stats line))])])
    (loading-gif)))

(defn ^:export main []
  (reagent/render-component [status] (.getElementById js/document "status"))
  (reagent/render-component [stats] (.getElementById js/document "stats"))
  (println "Running!"))
