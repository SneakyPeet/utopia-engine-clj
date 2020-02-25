(ns utopia.core.rules.search
  #?(:cljs (:require-macros [clara.macros :refer [defrule]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]])
            [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]]
            [clara.rules.accumulators :as acc]
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.universe :as u]))


;; Movement

#_(defrule go-to-workshop
  [CurrentAction (e/=GoToWorkshop? action)]
  =>
  (insert! (->Effect (e/->ChangeLocation :workshop))))

;; Search

#_(defrule can-search-when-searchable-regions-and-in-workshop
  [:or
   [StateEntity (e/=Location? entity) (= :workshop (:id entity))]
   [Effect (e/=ChangeLocation? effect) (= :workshop (:id effect))]]
  [?regions <- (acc/all) :from [StateEntity (e/=Region? entity) (true? (:searchable? entity))]]
  [:test (not (empty? ?regions))]
  =>
  (insert! (->NextAction (e/->Search))))


#_(defrule search-lets-you-choose-searchable-regions
  [CurrentAction (e/=Search? action)]
  [?regions <- (acc/all :entity) :from [StateEntity (e/=Region? entity) (true? (:searchable? entity))]]
  =>
  (insert! (->Effect (e/->ChangeLocation :outside)))
  (insert! (->NextAction (e/->GoToWorkshop)))
  (insert-all! (map #(->NextAction (e/->SearchRegion (:id %))) ?regions)))


#_(defrule resting-takes-time
  [CurrentAction (e/=Rest? action)]
  =>
  (insert! (->Effect (e/->RemoveDayFromTimeTrack))))


;; Effecfs

#_(defrule location-change-effect
  [:Effect [{effect :effect}] (e/=ChangeLocation? effect) (= ?location (:location effect))]
  =>
  (insert! (b/->StateChange #(assoc-in % [:location :id] ?location))))
