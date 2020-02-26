(ns utopia.core.entities
  #?(:clj (:require [utopia.core.macros :refer [defentity]])
     :cljs (:require-macros [utopia.core.macros :refer [defentity]])))


(defprotocol Entity
  (get-name [this]))

;;;; Actions

(defentity StartGame [])
(defentity Restart [])

(defentity Rest [])
(defentity UnconsiousRest [])


(defentity SearchRegion [id])


;;;; Effects

(defentity Initialize [])
(defentity DaysPass [days])
(defentity GainHitPoints [hit-points])
(defentity LooseHitPoints [hit-points])

;;;; Universe
(defentity Player [max-hit-points hit-points])

(defentity Timetrack [day dooms-day skulls gods-hand-energy])

(defentity Region
  [id number name
   day-tracker days-searched
   construct component treasure
   search-areas])
