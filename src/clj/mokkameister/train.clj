(ns mokkameister.train
  (:require [mokkameister.random :refer [rand-nth-weighted rand-sexp]]))

(def ^:private locomotives {:steam_locomotive 9
                            :bullettrain_front 3
                            :train 1
                            :horse 2
                            :unicorn_face 1
                            :boar 1
                            :bicyclist 1
                            :tractor 1})

(def ^:private train-endings {:dash 8
                              :sparkles 2
                              :sweat_drops 2
                              :exclamation 1
                              :runner 1
                              :rainbow 1})

(def ^:private num-wagons 7)

(defn- key->emoji [s]
  (str s ":"))

(defn- train->emojis [t]
  (->> (map key->emoji t)
       (apply str)))

(defn- train [& wagons]
  (let [front  (rand-nth-weighted locomotives)
        back   (rand-nth-weighted train-endings)
        middle (take num-wagons (cycle wagons))]
    (->> [front middle back]
         (flatten)
         (train->emojis))))

(def ^:private train-coffee (partial train :coffee))

(defn- train-custom [& wagons]
  (->> (cons :steam_locomotive wagons)
       (train->emojis)))

(defn rand-train []
  (rand-sexp
   (train-coffee)
   (train-coffee)
   (train-coffee)
   (train-coffee :heart)
   (train-coffee :heart)
   (train-coffee :heart)
   (train-coffee :heart)
   (train-coffee :heartpulse)
   (train-coffee :heartbeat)
   (train-coffee :syringe)
   (train-coffee :mushroom)
   (train-coffee :hocho)
   (train-coffee :wrench)
   (train-coffee :tada)
   (train-coffee :bacon)
   (train-coffee :scream)
   (train-coffee :female_health_worker)
   (train-coffee :smoking)
   (train-coffee :hand_with_index_and_middle_fingers_crossed)
   (train-coffee :battery)
   (train-coffee :bomb)
   (train-coffee :zap)
   (train-coffee :cake)
   (train-coffee :notes)
   (train-coffee :fuelpump)
   (train-coffee :alarm_clock)
   (train-coffee :rotating_light)
   (train-coffee :punch)
   (train-coffee :monkey_face :wrench)
   (train-coffee :exclamation_heart)
   (train-coffee :badger :mushroom)
   (train :heartbeat :heartpulse)
   (train :syringe :hocho)
   (train :scream_cat :scream)
   (train-custom :coffee :coffee :running :running :running :dash)
   (train-custom :sun_with_face :lollipop :sun_with_face :lollipop
                 :sun_with_face :lollipop :sun_with_face :rainbow)
   ":bullettrain_front::coffee::scream_cat::coffee::scream_cat::dash:"
   ":tractor::cow2::cow::cow2::cow::cow2::cow::cow2::dash:"
   ":unicorn_face::coffee::tada::coffee::tada::coffee::tada::coffee::rainbow:"
   (str "All aboard the coffee train!\n"
        (train-custom :coffee :coffee :running :running :running :dash))
   "Det er kaffi å få på kjøken!"
   "KEEP CALM THE COFFEE IS READY!"
   "M-m-m-m-m-microdosing! :syringe::syringe::syringe::exclamation:"
   "PLEASE PROCEED IN ORDERLY FASHION TO RECEIVE COFFEE :syringe:"
   "COFFEES WILL CONTINUE UNTIL MORALE IMPROVES! :facepunch::coffee:"
   ":rotating_light: BALLISTIC COFFEE TREAT INBOUND TO REVOLVERHUSET. SEEK IMMEDIATE COFFEE CUP. THIS IS NOT A DRILL. :rotating_light:"
   ":steam_locomotive::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::coffee::dash:"))
