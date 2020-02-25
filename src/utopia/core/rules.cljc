(ns utopia.core.rules
  #?(:cljs (:require-macros [clara.macros :refer [defrule defquery defsession ]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [insert insert-all retract clear-ns-productions!
                                          fire-rules query insert! insert-all! retract!]])
            [clara.rules.accumulators :as acc]
            [utopia.core.entities :as e]
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
  [CurrentAction (e/=StartGame? action)]
  =>
  (insert! (->Effect (e/->Initialize)))
  (insert! (->NextAction (e/->Search)))
  (insert! (->NextAction (e/->Rest))))


(defrule allow-restart-once-game-started
  [PreviousState (not (nil? state))]
  =>
  (insert! (->NextAction (e/->Restart))))


;; Movement

(defrule go-to-workshop
  [CurrentAction (e/=GoToWorkshop? action)]
  =>
  (insert! (->Effect (e/->ChangeLocation :workshop))))

;; Search

(defrule can-search-when-searchable-regions-and-in-workshop
  [:or
   [StateEntity (e/=Location? entity) (= :workshop (:id entity))]
   [Effect (e/=ChangeLocation? effect) (= :workshop (:id effect))]]
  [?regions <- (acc/all) :from [StateEntity (e/=Region? entity) (true? (:searchable? entity))]]
  [:test (not (empty? ?regions))]
  =>
  (insert! (->NextAction (e/->Search))))


(defrule search-lets-you-choose-searchable-regions
  [CurrentAction (e/=Search? action)]
  [?regions <- (acc/all :entity) :from [StateEntity (e/=Region? entity) (true? (:searchable? entity))]]
  =>
  (insert! (->Effect (e/->ChangeLocation :outside)))
  (insert! (->NextAction (e/->GoToWorkshop)))
  (insert-all! (map #(->NextAction (e/->SearchRegion (:id %))) ?regions)))


(defrule resting-takes-time
  [CurrentAction (e/=Rest? action)]
  =>
  (insert! (->Effect (e/->RemoveDayFromTimeTrack))))


;; Effecfs

(defrule initialize-state-effect
  [Effect (e/=Initialize? effect)]
  =>
  1
  (insert! (->StateChange (constantly (u/initial-state)))))


(defrule location-change-effect
  [Effect (e/=ChangeLocation? effect) (= ?location (:location effect))]
  =>
  (insert! (->StateChange #(assoc-in % [:location :id] ?location))))


;;;; Queries

(defquery get-new-state []
  [NextState (= ?state state)])


(defquery get-next-actions []
  [NextAction (= ?action action)])


(defquery get-game-errors []
  [GameError (= ?message message)])




(defn- game-state->facts [game-state]
  (let [{:keys [actions state]} game-state
        {:keys [regions location]} state]
    (->> [[(->PreviousActions actions)
           (->PreviousState state)
           (->StateEntity location)]
          (map ->StateEntity (vals regions))]
         (reduce into))))


(defsession ^:private session 'utopia.core.rules)

(defn run [game-state action]
  (let [session (-> session #_(mk-session 'utopia.core.rules)
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
  {:actions [(e/->StartGame)]
   :effects []
   :errors []
   :state nil})


(comment


  (-> (run (initial-game-state) (e/->StartGame))
      (run (e/->Search))
      (run (e/->GoToWorkshop))
      (select-keys [:actions :state]))

  (run (initial-game-state) (e/->Rest))


  )
