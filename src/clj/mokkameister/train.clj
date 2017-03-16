(ns mokkameister.train
  (:require [mokkameister.random :refer [rand-nth-weighted rand-sexp]]))

(def ^:private locomotives {":steam_locomotive:" 9
                            ":bybane2:" 3
                            ":bullettrain_front:" 3
                            ":train:" 1
                            ":horse:" 2
                            ":unicorn_face:" 1
                            ":boar:" 1
                            ":tractor:" 1})

(def ^:private train-endings {":dash:" 8
                              "" 2
                              ":afterburner:" 2
                              ":sparkles:" 2
                              ":exclamation:" 1})

(def ^:private num-wagons 7)

(defn- train [& wagons]
  (let [front  (rand-nth-weighted locomotives)
        back   (rand-nth-weighted train-endings)
        middle (apply str (take num-wagons (cycle wagons)))]
    (str front middle back)))

(def ^:private coffee-train (partial train ":coffee:"))

(defn rand-train []
  (rand-sexp
   (coffee-train)
   (coffee-train)
   (coffee-train ":heart:")
   (coffee-train ":heart:")
   (coffee-train ":heart:")
   (coffee-train ":syringe:")
   (coffee-train ":mushroom:")
   (coffee-train ":coffeepot:")
   (coffee-train ":coffeealarm:")
   (coffee-train ":hocho:")
   (coffee-train ":wrench:")
   (coffee-train ":tada:")
   (coffee-train ":bacon:")
   (coffee-train ":nuclear:")
   (coffee-train ":timwendelboe:")
   (coffee-train ":jan-richter:")
   (coffee-train ":scream:")
   (coffee-train ":heavy_heart_exclamation_mark_ornament:")
   (coffee-train ":badger:" ":mushroom:")
   (coffee-train ":coffeepot:" ":heart:")
   (coffee-train ":simply_overcaffeinated:" ":coffeespin:")
   (coffee-train ":smoking:")
   (train ":coffeealarm:" ":coffeealarm:")
   (train ":nespresso:" ":ali:")
   (train ":ali:" ":gruff:")
   (train ":syringe:" ":hocho:")
   ":steam_locomotive::coffee::coffee::running::running::running::dash:"
   "All aboard the coffee train\n:steam_locomotive::coffee::coffee::running::running::running::dash:"
   "Det er kaffi å få på kjøken!"
   "KEEP CALM THE COFFEE IS READY!"
   "M-m-m-m-m-microdosing! :syringe::syringe::syringe::exclamation:"
   "PLEASE PROCEED IN ORDERLY FASHION TO RECEIVE COFFEE :syringe:"
   ":steam_locomotive::sun_with_face::lollipop::sun_with_face::lollipop::sun_with_face::lollipop::sun_with_face::rainbow:"))
