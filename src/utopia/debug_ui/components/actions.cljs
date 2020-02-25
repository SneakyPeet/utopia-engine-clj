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
     [:div.buttons
      (->> available-actions
           (map-indexed
            (fn [i a]
              [:button.button.is-fullwidth
               {:key i
                :on-click #(rf/dispatch [::evt/run-action a])}
               (e/get-name a)])))]]))


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
              [:li {:key i} (inc tick) ". "(e/get-name action)])))]]))
