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

(defn- train [a b]
  (let [front (rand-nth-weighted locomotives)
        back  (rand-nth-weighted train-endings)]
    (str front a b a b a b a back)))

(defn rand-train []
  (rand-sexp
   (train ":coffee:" ":coffee:")
   (train ":coffee:" ":coffee:")
   (train ":coffee:" ":heart:")
   (train ":coffee:" ":heart:")
   (train ":coffee:" ":heart:")
   (train ":coffeealarm:" ":coffeealarm:")
   (train ":nespresso:" ":ali:")
   (train ":ali:" ":gruff:")
   (train ":coffee:" ":syringe:")
   (train ":coffee:" ":mushroom:")
   (train ":coffee:" ":coffeepot:")
   (train ":coffee:" ":coffeealarm:")
   (train ":coffee:" ":hocho:")
   (train ":coffee:" ":wrench:")
   (train ":coffee:" ":tada:")
   (train ":coffee:" ":bacon:")
   (train ":coffee:" ":nuclear:")
   (train ":coffee:" ":timwendelboe:")
   (train ":coffee:" ":jan-richter:")
   (train ":syringe:" ":hocho:")
   ":steam_locomotive::coffee::coffee::running::running::running::dash:"
   "All aboard the coffee train\n:steam_locomotive::coffee::coffee::running::running::running::dash:"
   "Det er kaffi å få på kjøken!"
   "KEEP CALM THE COFFEE IS READY!"
   "M-m-m-m-m-microdosing! :syringe::syringe::syringe::exclamation:"
   "PLEASE PROCEED IN ORDERLY FASHION TO RECEIVE COFFEE :syringe:"
   ":steam_locomotive::sun_with_face::lollipop::sun_with_face::lollipop::sun_with_face::lollipop::sun_with_face::rainbow:"))
