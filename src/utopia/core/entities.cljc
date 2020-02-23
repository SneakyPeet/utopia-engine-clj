(ns utopia.core.entities
  #?(:clj (:require [utopia.core.macros :refer [defentity]])
     :cljs (:require-macros [utopia.core.macros :refer [defentity]])))


(defprotocol Entity
  (get-name [this]))

;;;; Actions

(defentity StartGame [])
(defentity Restart [])
(defentity Rest [])
(defentity GoToWorkshop [])

(defentity Search [])
(defentity SearchRegion [id])


;;;; Effects

(defentity Initialize [])
(defentity ChangeLocation [id])
(defentity AddDayOnTimeTrack [])
(defentity RemoveDayFromTimeTrack [])


;;;; Universe
(defentity Location [id])
(defentity Region
  [id number name
   day-tracker days-searched
   construct component treasure
   search-areas])
