(ns utopia.core.rules
  (:require [clara.rules :refer :all]
            [clara.rules.accumulators :as acc]
            [utopia.core.entities :refer :all]
            [utopia.core.universe :as u]))

(clear-ns-productions!)

;;;; RULE ENTITIES
(defrecord CurrentAction [action])
(defrecord NextAction [action])
(defrecord Effect [effect])
(defrecord StateEntity [entity])

(defrecord PreviousActions [actions])
(defrecord PreviousState [state])
(defrecord NextState [state])

(defrecord StateChange [f])
(defrecord GameError [message])


;; BOILERPLATE RULES

(defrule only-apply-available-actions
  [PreviousActions (= ?actions actions)]
  [CurrentAction (= ?action action) (= ?action-type (type ?action))]
  [:test (not (contains? (set (map type ?actions)) ?action-type))]
  =>
  (insert! (->GameError (str "Invalid Action: " ?action-type))))


(defrule apply-all-state-changes
  [PreviousState (= ?state state)]
  [?state-change-fns <- (acc/all :f) :from [StateChange]]
  =>
  (insert!
   (->NextState
    (reduce (fn [r f] (f r)) ?state ?state-change-fns))))


(defrule start-game-triggered
  [CurrentAction (=StartGame? action)]
  =>
  (insert! (->Effect (->Initialize)))
  (insert! (->NextAction (->Search)))
  (insert! (->NextAction (->Rest))))


(defrule allow-restart-once-game-started
  [PreviousState (not (nil? state))]
  =>
  (insert! (->NextAction (->Restart))))


;; Movement

(defrule go-to-workshop
  [CurrentAction (=GoToWorkshop? action)]
  =>
  (insert! (->Effect (->ChangeLocation :workshop))))

;; Search

(defrule can-search-when-searchable-regions-and-in-workshop
  [:or
   [StateEntity (=Location? entity) (= :workshop (:location entity))]
   [Effect (=ChangeLocation? effect) (= :workshop (:location effect))]]
  [?regions <- (acc/all) :from [StateEntity (=Region? entity) (true? (:searchable? entity))]]
  [:test (not (empty? ?regions))]
  =>
  (insert! (->NextAction (->Search))))


(defrule search-lets-you-choose-searchable-regions
  [CurrentAction (=Search? action)]
  [?regions <- (acc/all :entity) :from [StateEntity (=Region? entity) (true? (:searchable? entity))]]
  =>
  (insert! (->Effect (->ChangeLocation :outside)))
  (insert! (->NextAction (->GoToWorkshop)))
  (insert-all! (map #(->NextAction (->SearchRegion (:id %))) ?regions)))


(defrule resting-takes-time
  [CurrentAction (=Rest? action)]
  =>
  (insert! (->Effect (->RemoveDayFromTimeTrack))))


;; Effecfs

(defrule initialize-state-effect
  [Effect (=Initialize? effect)]
  =>
  1
  (insert! (->StateChange (constantly (u/initial-state)))))


(defrule location-change-effect
  [Effect (=ChangeLocation? effect) (= ?location (:location effect))]
  =>
  (insert! (->StateChange #(assoc-in % [:location :id] ?location))))


;;;; Queries

(defquery get-new-state []
  [NextState (= ?state state)])


(defquery get-next-actions []
  [NextAction (= ?action action)])


(defquery get-game-errors []
  [GameError (= ?message message)])



#_(def ^:private rules-session (mk-session 'utopia.rules))


(defn- game-state->facts [game-state]
  (let [{:keys [actions state]} game-state
        {:keys [regions location]} state]
    (->> [[(->PreviousActions actions)
           (->PreviousState state)
           (->StateEntity location)]
          (map ->StateEntity (vals regions))]
         (reduce into))))


(defn run [game-state action]
  (let [session (-> (mk-session 'utopia.core.rules)
                    (insert (->CurrentAction action))
                    (insert-all (game-state->facts game-state))
                    (fire-rules))
        new-state (:?state (first (query session get-new-state)))
        actions (map :?action (query session get-next-actions))
        errors (map :?message (query session get-game-errors))]
    {:actions actions
     :errors errors
     :old-state (:state game-state)
     :state new-state}))


(defn initial-game-state []
  {:actions [(->StartGame)]
   :effects []
   :errors []
   :state nil})


(comment


  (-> (run (initial-game-state) (->StartGame))
      (run (->Search))
      (run (->GoToWorkshop))
      (select-keys [:actions :state]))

  (run (initial-game-state) (->Rest))


  )
