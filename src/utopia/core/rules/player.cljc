(ns utopia.core.rules.player
  #?(:cljs (:require-macros [clara.macros :refer [defrule]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]])
            [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]]
            [clara.rules.accumulators :as acc]
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.universe :as u]))

(clear-ns-productions!)

(defrule can-rest-if-damaged
  [:StateEntity (= ?entity (:entity this)) (e/=Player? ?entity)]
  [:NextState (= ?hit-points (get-in this [:state :player :hit-points]))]
  [:test (> (:max-hit-points ?entity) ?hit-points 0)]
  =>
  (insert! (b/->NextAction (e/->Rest))))


(defrule resting-gains-a-hitpoint
  [:CurrentAction (e/=Rest? (:action this))]
  =>
  (insert! (b/->Effect (e/->GainHitPoints 1))))


(defrule should-rest-if-unconsious
  [:NextState (= ?hit-points (get-in this [:state :player :hit-points]))]
  [:test (zero? ?hit-points)]
  =>
  (insert! (b/->NextAction (e/->UnconsiousRest))))


(defrule only-allowed-to-rest-if-unconsious
  [?actions <- (acc/all) :from [:NextAction]]
  =>
  (let [a-without-rest (filter #(not (e/=UnconsiousRest? (:action %))) ?actions)]
    (when-not (= (count ?actions) (count a-without-rest))
      (doseq [a a-without-rest]
        (retract! a)))))


(defrule unconsious-rest-gains-full-life
  [:StateEntity (= ?player (:entity this)) (e/=Player? ?player)]
  [:CurrentAction (e/=UnconsiousRest? (:action this))]
  =>
  (insert! (b/->Effect (e/->GainHitPoints (:max-hit-points ?player)))))


(def total-hit-points-gained
  (acc/reduce-to-accum
   (fn [value effect]
     (if effect
       (+ value (get-in effect [:effect :hit-points]))
       value))
   0))

(defrule increase-hitpoints-up-to-max-hit-points
  [?total-gained <- total-hit-points-gained :from [:Effect (e/=GainHitPoints? (:effect this))]]
  [:StateEntity (= ?player (:entity this)) (e/=Player? ?player)]
  [:test (> ?total-gained 0)]
  =>
  (insert! (b/->StateChange
            #(assoc-in % [:player :hit-points]
                       (min (:max-hit-points ?player)
                            (+ (:hit-points ?player) ?total-gained))))))
