(ns utopia.debug-ui.components.state
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [utopia.debug-ui.subs :as sub]
            [utopia.debug-ui.events :as evt]
            [utopia.core.entities :as e]))



(defn- render-node [node]
  (cond
    (nil? node) "Not Initialized"

    (map? node)
    [:div
     (->> node
          (map-indexed
           (fn [i [k v]]
             (if (map? v)
               [:details {:key i}
                [:summary [:strong (str k)]]
                [:div {:style {:margin-left "25px"}}
                 (render-node v)]]
               [:div [:strong (str k) " "] (render-node v)]))))]

    :else
    [:span (str node)]))


(defn state []
  (let [current-state @(rf/subscribe [::sub/current-state])]
    [:div
     [:h1.heading "State"]
     (render-node current-state)]))
