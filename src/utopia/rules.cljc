(ns utopia.rules
  (:require [clara.rules :refer :all]
            [clara.rules.accumulators :as acc]
            [utopia.entities :refer :all]))

(clear-ns-productions!)

(defrecord Execute [action])
(defrecord AvailableAction [action])
(defrecord AllowedActions [actions])
(defrecord EffectState [effect])
(defrecord GameError [message])

;;;; Core Rules

(def ^:private always-available-actions [(->Restart) (->Rest)])

(defn- state->facts [state]
  [(->AllowedActions (:actions state))])


(defrule only-apply-available-actions
  [AllowedActions (= ?actions actions)]
  [Execute (= ?action action) (= ?action-type (type ?action))]
  [:test (not (contains? (set (map type ?actions)) ?action-type))]
  =>
  (insert! (->GameError (str "Invalid Action: " ?action-type))))


(defrule start-game-lets-you-search
  [Execute (=StartGame? action)]
  =>
  (insert! (->AvailableAction (->Search))))


(defrule resting-takes-time
  [Execute (=Rest? action)]
  =>
  (insert! (->EffectState (->RemoveDayFromTimeTrack))))



(defquery get-next-actions []
  [AvailableAction (= ?action action)])

(defquery get-effects []
  [EffectState (= ?effect effect)])

(defquery get-game-errors []
  [GameError (= ?message message)])


#_(def ^:private rules-session (mk-session 'utopia.rules))


(defn run [state action]
  (let [session (-> (mk-session 'utopia.rules)
                    (insert (->Execute action))
                    (insert-all (map ->AvailableAction always-available-actions))
                    (insert-all (state->facts state))
                    (fire-rules))
        actions (map :?action (query session get-next-actions))
        effects (map :?effect (query session get-effects))
        errors (map :?message (query session get-game-errors))]
    {:actions actions
     :effects effects
     :errors errors
     :old-state state}))

(def initial-state
  {:actions [(->StartGame)]
   :effects []
   :errors []
   :old-state nil})

(comment


  (run initial-state (->StartGame))

  (run initial-state (->Rest))


  )
