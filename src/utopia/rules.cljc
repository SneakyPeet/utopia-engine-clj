(ns utopia.rules
  (:require [clara.rules :refer :all]
            [clara.rules.accumulators :as acc]
            [utopia.entities :refer :all]
            [utopia.universe :as u]))

(clear-ns-productions!)

;;;; BoilerPlate
(defrecord Execute [action])
(defrecord AvailableAction [action])
(defrecord AllowedActions [actions])
(defrecord EffectState [effect])
(defrecord StateChange [f])
(defrecord OldState [state])
(defrecord StateEntity [entity])
(defrecord NewState [state])
(defrecord GameError [message])

;;;; Core Rules


;; BoilerPlate

(defrule only-apply-available-actions
  [AllowedActions (= ?actions actions)]
  [Execute (= ?action action) (= ?action-type (type ?action))]
  [:test (not (contains? (set (map type ?actions)) ?action-type))]
  =>
  (insert! (->GameError (str "Invalid Action: " ?action-type))))


(defrule apply-all-state-changes
  [OldState (= ?state state)]
  [?state-change-fns <- (acc/all :f) :from [StateChange]]
  =>
  (insert!
   (->NewState
    (reduce (fn [r f] (f r)) ?state ?state-change-fns))))


(defrule start-game-triggered
  [Execute (=StartGame? action)]
  =>
  (insert! (->EffectState (->Initialize)))
  (insert! (->AvailableAction (->Search)))
  (insert! (->AvailableAction (->Rest))))


(defrule allow-restart-once-game-started
  [OldState (not (nil? state))]
  =>
  (insert! (->AvailableAction (->Restart))))

;; Search

(defrule can-search-when-searchable-regions-and-in-workshop
  [StateEntity (=Location? entity) (= :workshop (:location entity))]
  [?regions <- (acc/all) :from [StateEntity (=Region? entity) (true? (:searchable? entity))]]
  [:test (not (empty? ?regions))]
  =>
  (insert! (->AvailableAction (->Search))))




(defrule resting-takes-time
  [Execute (=Rest? action)]
  =>
  (insert! (->EffectState (->RemoveDayFromTimeTrack))))


;; Effecfs
(defrule initialize-state
  [EffectState (=Initialize? effect)]
  =>
  1
  (insert! (->StateChange (constantly (u/initial-state)))))


;;;; Queries

(defquery get-new-state []
  [NewState (= ?state state)])


(defquery get-next-actions []
  [AvailableAction (= ?action action)])


(defquery get-game-errors []
  [GameError (= ?message message)])



#_(def ^:private rules-session (mk-session 'utopia.rules))


(defn- game-state->facts [game-state]
  (let [{:keys [actions state]} game-state
        {:keys [regions location]} state]
    (->> [[(->AllowedActions actions)
           (->OldState state)
           (->StateEntity (->Location location))]
          (map ->StateEntity (vals regions))]
         (reduce into))))


(defn run [game-state action]
  (let [session (-> (mk-session 'utopia.rules)
                    (insert (->Execute action))
                    (insert-all (game-state->facts game-state))
                    (fire-rules))
        new-state (:?state (first (query session get-new-state)))
        actions (map :?action (query session get-next-actions))
        errors (map :?message (query session get-game-errors))]
    {:actions actions
     :errors errors
     :old-state (:state game-state)
     :state new-state}))


(def initial-game-state
  {:actions [(->StartGame)]
   :effects []
   :errors []
   :state nil})


(comment


  (-> (run initial-game-state (->StartGame))
      (run (->Rest))
      :actions)

  (run initial-game-state (->Rest))


  )
