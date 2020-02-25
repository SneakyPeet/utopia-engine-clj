(ns utopia.core.rules.search
  #?(:cljs (:require-macros [clara.macros :refer [defrule]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]])
            [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]]
            [clara.rules.accumulators :as acc]
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.universe :as u]))

(clear-ns-productions!)


(defrule can-search-when-regions-are-searchable
  [:StateEntity (= ?entity (:entity this)) (true? (:searchable? ?entity))]
  =>
  (insert! (b/->NextAction (e/->SearchRegion (:id ?entity)))))


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
