(ns clojure-lab2-13.core
    (:require [clojure.core.async :refer [>! <! <!! >!! timeout chan alts! go go-loop close!]]))

(defn channel-to-print [channel]
    (<!! (go-loop []
             (when-let [x (<! channel)]
                 (println x)
                 (recur)))))

(defn vec-to-channel [vec]
    (let [channel (chan 10)]
        (go
            (doseq [x vec]
                (>! channel x))
            (close! channel))
        channel))

(defn channel-copying [input output]
    (go-loop [cash []]
        (if-let [x (<! input)]
            (if (= x :reset)
                (recur [])
                (if (.contains cash x)
                    (recur cash)
                    (do
                        (>! output x)
                        (recur (conj cash x)))))
            (close! output))))

(defn -main [& _]
    (let [x (vec-to-channel ["a" 5 -1 true 5 -1 2 5 "true" :reset 5 "a" 5 :reset 5])
          y (chan 10)]
        (channel-copying x y)
        (channel-to-print y)))
