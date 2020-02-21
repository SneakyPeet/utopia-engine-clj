(ns utopia.universe
  (:require [utopia.entities :refer :all]))


(defn- initial-search []
  {:a1 nil :a2 nil :a3 nil
   :b1 nil :b2 nil :b3 nil})


(defn- initial-search-areas []
  (->> (range 1 6)
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
   (map map->Region)
   (map (juxt :id identity))
   (into {})))


(defn initial-state []
  {:location :workshop
   :regions (regions)})
