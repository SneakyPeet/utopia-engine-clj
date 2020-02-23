(ns utopia.debug-ui.events
  (:require [re-frame.core :as rf]
            [utopia.core :as u]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]
   {:game-state (u/start)}))
