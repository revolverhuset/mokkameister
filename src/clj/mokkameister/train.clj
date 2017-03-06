(ns mokkameister.train
  (:require [mokkameister.random :refer [rand-nth-weighted]]))

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
                              ":sparkles:" 2
                              ":exclamation:" 1})

(defn train [a b]
  (let [front (rand-nth-weighted locomotives)
        back  (rand-nth-weighted train-endings)]
    (str front a b a b a b a back)))
