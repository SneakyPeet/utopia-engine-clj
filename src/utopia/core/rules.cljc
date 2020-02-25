(ns utopia.core.rules
  #?(:cljs (:require-macros [clara.macros :refer [defquery defsession]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [insert insert-all clear-ns-productions!
                                          fire-rules query ]])
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            ;rules
            [utopia.core.rules.game-logic]))

(clear-ns-productions!)


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


;;;; Queries

(defquery get-new-state []
  [:NextState (= ?state (:state this))])


(defquery get-next-actions []
  [:NextAction (= ?action (:action this))])


(defquery get-effects []
  [:Effect (= ?effect (:effect this))])


(defquery get-game-errors []
  [:GameError (= ?message (:message this))])


(defn- game-state->facts [game-state]
  (let [{:keys [actions state]} game-state
        {:keys [regions location]} state]
    (->> [[(b/->PreviousActions actions)
           (b/->PreviousState state)
           (b/->StateEntity location)]
          (map b/->StateEntity (vals regions))]
         (reduce into))))


(defsession ^:private session
  'utopia.core.rules
  'utopia.core.rules.game-logic
  :fact-type-fn :rule-type)


(defn initial-game-state []
  {:actions [(e/->StartGame)]
   :history '()
   :effects []
   :errors []
   :state nil
   :tick 0})


(defn run [game-state action]
  (if (e/=Restart? action)
    (initial-game-state)
    (let [session (-> session
                      (insert (b/->CurrentAction action))
                      (insert-all (game-state->facts game-state))
                      (fire-rules))
          new-state (:?state (first (query session get-new-state)))
          actions (map :?action (query session get-next-actions))
          effects (map :?effect (query session get-effects))
          errors (map :?message (query session get-game-errors))]
      {:actions actions
       :history (conj (:history game-state) {:action action
                                             :tick (:tick game-state)
                                             :game-state game-state})
       :errors errors
       :state new-state
       :tick (inc (:tick game-state))})))





(comment

  (-> (run (initial-game-state) (e/->StartGame))
      (run (e/->Search))
      (run (e/->GoToWorkshop))
      (select-keys [:actions :state]))

  (run (initial-game-state) (e/->StartGame))


  )
