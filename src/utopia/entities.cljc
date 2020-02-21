(ns utopia.entities
  (:require #?(:clj [utopia.macros :refer [defentity]])))


(defprotocol Action)

(defentity StartGame [] Action)
(defentity Restart [] Action)
(defentity Rest [] Action)
(defentity Search [] Action)


(defprotocol Effect)

(defentity Initialize [] Effect)
(defentity AddDayOnTimeTrack [] Effect)
(defentity RemoveDayFromTimeTrack [] Effect)


;;;; Universe
(defentity Location [location])
(defentity Region
  [id number name
   day-tracker days-searched
   construct component treasure
   search-areas])

