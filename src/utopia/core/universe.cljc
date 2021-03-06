(ns utopia.core.universe
  (:require [utopia.core.entities :as e]))


;;;; Timetrack

(defn- time-track []
  (e/map->Timetrack
   {:day 1 :dooms-day 16 :skulls 8 :gods-hand-energy 0}))


;;;; Search
(defn- initial-search []
  {:a1 :open :a2 :open :a3 :open
   :b1 :open :b2 :open :b3 :open })


(defn- initial-search-areas []
  (->> (range 6)
       (map #(vec [% (initial-search)]))
       (into {})))


(defn- regions []
  (->>
   [{:id :halebeard-peak
     :number 1
     :name "Halebeard Peak"
     :day-tracker [-1 -1 0 -1 0 0]
     :construct :seal-of-balance
     :component :silver
     :treasure :ice-plate}
    {:id :the-great-wilds
     :number 2
     :name "The Great Wilds"
     :day-tracker [-1 0 0 -1 0 0]
     :construct :hermetic-mirror
     :component :quartz
     :treasure :bracelet-of-ios}]
   (map #(merge % {:search-areas (initial-search-areas)
                   :days-searched 0
                   :searchable? true}))
   (map e/map->Region)
   (map (juxt :id identity))
   (into {})))


(defn initial-state []
  {:player (e/map->Player {:max-hit-points 6 :hit-points 4})
   :time-track (time-track)
   :regions (regions)})
