(ns mokkameister.db.persistence
  (:require [mokkameister.system :refer [system]]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/queries.sql"
  {:connection (system :db)})

#_(defn recreate-tables! []
  (try
    (drop-brewings-table!)
    (catch Exception e)
    (finally (create-brewings-table!))))

(defn persist-brew! [event]
  (let [{:keys [slack-user brew-time]} event
        coffee-type (name (:coffee-type event))]
    (insert-brewing<! {:slack_user slack-user
                       :brew_time brew-time
                       :coffee_type coffee-type})))

(defn latest-brews []
  (let [[last-regular] (find-last-regular-coffee)
        [last-instant] (find-last-instant-coffee)]
    {:regular last-regular
     :instant last-instant}))

(defn brew-stats []
  (letfn [(coffee-stats [type]
            (first (coffee-type-stats {:coffee_type type})))]
    {:regular (coffee-stats "regular")
     :instant (coffee-stats "instant")}))
