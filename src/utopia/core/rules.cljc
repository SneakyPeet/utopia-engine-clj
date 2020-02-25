(ns utopia.core.rules
  #?(:cljs (:require-macros [clara.macros :refer [defquery defsession]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [insert insert-all clear-ns-productions!
                                          fire-rules query ]])
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.rules.game-logic]
            [utopia.core.rules.search]))


;;; HELPERS

(defn- game-state->facts [game-state]
  (let [{:keys [actions state]} game-state
        {:keys [regions location]} state]
    (->> [[(b/->PreviousActions actions)
           (b/->PreviousState state)
           (b/->StateEntity location)]
          (map b/->StateEntity (vals regions))]
         (reduce into))))


(defn initial-game-state []
  {:actions [(e/->StartGame)]
   :history '()
   :effects []
   :errors []
   :state nil
   :tick 0})


;;;; QUERIES

(clear-ns-productions!)

(defquery get-new-state []
  [:NextState (= ?state (:state this))])


(defquery get-next-actions []
  [:NextAction (= ?action (:action this))])


(defquery get-effects []
  [:Effect (= ?effect (:effect this))])


(defquery get-game-errors []
  [:GameError (= ?message (:message this))])


;;;; SESSION

(defsession ^:private session
  'utopia.core.rules
  'utopia.core.rules.game-logic
  :fact-type-fn :rule-type)


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
       :effects effects
       :history (conj (:history game-state) {:action action
                                             :tick (:tick game-state)
                                             :game-state game-state})
       :errors errors
       :state new-state
       :tick (inc (:tick game-state))})))


(comment
  (run (initial-game-state) (e/->StartGame)))
