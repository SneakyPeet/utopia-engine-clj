(ns utopia.core.rules.game-logic
  #?(:cljs (:require-macros [clara.macros :refer [defrule]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]])
            [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]]
            [clara.rules.accumulators :as acc]
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.universe :as u]))


(clear-ns-productions!)

(defrule only-apply-available-actions-are-allowed-to-run
  [:PreviousActions (= ?actions (:actions this))]
  [:CurrentAction (= ?action (:action this)) (= ?action-type (type ?action))]
  [:test (not (contains? (set (map type ?actions)) ?action-type))]
  =>
  (insert! (b/->GameError (str "Invalid Action: " (e/get-name ?action)))))


(defrule all-state-changes-should-be-applied
  [:PreviousState (= ?state (:state this))]
  [?state-change-fns <- (acc/all :f) :from [:StateChange]]
  =>
  (insert!
   (b/->NextState
    (reduce (fn [r f] (f r)) ?state ?state-change-fns))))


(defrule start-game-action-triggers-initialize
  [:CurrentAction (e/=StartGame? (:action this))]
  =>
  (insert! (b/->Effect (e/->Initialize))))


(defrule in-progress-game-can-be-restarted
  [:PreviousState (not (nil? (:state this)))]
  =>
  (insert! (b/->NextAction (e/->Restart))))


(defrule initialize-effect-resets-to-initial-state
  [:Effect (e/=Initialize? (:effect this))]
  =>
  (insert! (b/->StateChange (constantly (u/initial-state)))))
