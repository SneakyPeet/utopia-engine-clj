(ns utopia.core.entities
  #?(:clj (:require [utopia.core.macros :refer [defentity]])
     :cljs (:require-macros [utopia.core.macros :refer [defentity]])))


(defprotocol Entity
  (get-name [this]))

;;;; Actions

(defentity StartGame [])
(defentity Restart [])

(defentity Rest [])


(defentity SearchRegion [id])


;;;; Effects

(defentity Initialize [])
(defentity AddDayOnTimeTrack [])
(defentity RemoveDayFromTimeTrack [])


;;;; Universe
(defentity Region
  [id number name
   day-tracker days-searched
   construct component treasure
   search-areas])
