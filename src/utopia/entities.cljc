(ns utopia.entities
  (:require #?(:clj [utopia.macros :refer [defentity]])))


(defprotocol Action)

(defentity StartGame [] Action)
(defentity Restart [] Action)
(defentity Rest [] Action)
(defentity Search [] Action)


(defprotocol Effect)

(defentity AddDayOnTimeTrack [] Effect)
(defentity RemoveDayFromTimeTrack [] Effect)