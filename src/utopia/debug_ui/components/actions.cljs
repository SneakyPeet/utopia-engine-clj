(ns utopia.debug-ui.components.actions
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [utopia.debug-ui.subs :as sub]
            [utopia.debug-ui.events :as evt]
            [utopia.core.entities :as e]))


(rf/reg-sub
 ::available-actions
 (fn [_ _] (rf/subscribe [::sub/game-state]))
 :actions)


(defn actions []
  (let [available-actions @(rf/subscribe [::available-actions])]
    [:div
     [:h1.heading "Actions"]
     [:div
      (->> available-actions
           (map-indexed
            (fn [i a]
              [:div.has-background-white-ter
               {:key i :style {:cursor "pointer" :margin-bottom "10px" :padding "10px"}
                :on-click #(rf/dispatch [::evt/run-action a])}
               [:strong (e/get-name a)]
               (when-not (empty? a)
                 [:div [:small (str (into {} a))]])])))]]))


(rf/reg-sub
 ::history
 (fn [_ _] (rf/subscribe [::sub/game-state]))
 :history)


(defn action-history []
  (let [history @(rf/subscribe [::history])
        tick @(rf/subscribe [::sub/tick])]
    [:div
     [:h1.heading "History"]
     [:ul
      (->> history
           (map-indexed
            (fn [i {:keys [tick action]}]
              [:li {:key i}
               (inc tick) ". "(e/get-name action)
               (when-not (empty? action)
                 [:<> [:br] [:small (str (into {} action))]])])))]]))



(rf/reg-sub
 ::effects
 (fn [_ _] (rf/subscribe [::sub/game-state]))
 :effects)


(defn effects []
  (let [effects @(rf/subscribe [::effects])]
    [:div
     [:h1.heading "Effects of Previous Action"]
     [:ul
      (->> effects
           (map-indexed
            (fn [i e]
              [:li {:key i}
               [:strong (e/get-name e)]])))]]))
