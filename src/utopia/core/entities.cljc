(ns utopia.core.entities
  (:require #?(:clj [utopia.core.macros :refer [defentity]])))


;;;; Actions

(defentity StartGame [])
(defentity Restart [])
(defentity Rest [])
(defentity GoToWorkshop [])

(defentity Search [])
(defentity SearchRegion [id])


;;;; Effects

(defentity Initialize [])
(defentity ChangeLocation [location])
(defentity AddDayOnTimeTrack [])
(defentity RemoveDayFromTimeTrack [])


;;;; Universe
(defentity Location [id])
(defentity Region
  [id number name
   day-tracker days-searched
   construct component treasure
   search-areas])
