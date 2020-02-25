(ns utopia.debug-ui.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 ::game-state
 :game-state)


(rf/reg-sub
 ::tick
 (fn [_ _] (rf/subscribe [::game-state]))
 :tick)


(rf/reg-sub
 ::history
 :history)


(rf/reg-sub
 ::rules-state
 (fn [_ _] (rf/subscribe [::game-state]))
 :state)



(rf/reg-sub
 ::effects
 (fn [_ _] (rf/subscribe [::game-state]))
 :effects)


(rf/reg-sub
 ::errors
 (fn [_ _] (rf/subscribe [::game-state]))
 :errors)
