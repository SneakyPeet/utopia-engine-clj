(ns utopia.debug-ui.events
  (:require [re-frame.core :as rf]
            [utopia.core :as u]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]
   {:game-state (u/start)}))


(rf/reg-event-db
 ::run-action
 (fn [db [_ action]]
   (let [current-state (:game-state db)]
     (-> db
         (assoc :game-state (u/next-state current-state action))))))
