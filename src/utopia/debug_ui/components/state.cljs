(ns utopia.debug-ui.components.state
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [utopia.debug-ui.subs :as sub]
            [utopia.debug-ui.events :as evt]
            [utopia.core.entities :as e]))



(defn- render-node [node]
  (cond
    (map? node)
    [:ul
     (->> node
          (map-indexed
           (fn [i [k v]]
             [:li {:key i} (str k)])))]))


(defn state []
  (let [current-state @(rf/subscribe [::sub/rules-state])]
    [:div
     [:h1.heading "State"]
     (render-node current-state)]))
