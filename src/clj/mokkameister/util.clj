(ns mokkameister.util)

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))
