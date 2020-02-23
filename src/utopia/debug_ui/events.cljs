(ns utopia.debug-ui.events
  (:require [re-frame.core :as rf]
            [utopia.core :as u]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]
   {:game-state (u/start)
    :tick 1
    :history '()}))


(rf/reg-event-db
 ::run-action
 (fn [db [_ action]]
   (let [current-state (:game-state db)]
     (-> db
         (update :history conj {:action action
                                :state current-state
                                :tick (:tick db)})
         (assoc :game-state (u/next-state current-state action))
         (update :tick inc)))))
