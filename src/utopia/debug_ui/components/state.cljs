(ns utopia.debug-ui.components.state
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [utopia.debug-ui.subs :as sub]
            [utopia.debug-ui.events :as evt]
            [utopia.core.entities :as e]))


(rf/reg-event-db
 ::->previous-state
 (fn [db _]
   (if-let [previous-state (:game-state (first (get-in db [:game-state :history])))]
     (assoc db :game-state previous-state)
     db)))


(rf/reg-event-db
 ::toggle-map-node
 (fn [db [_ k]]
   (let [toggled-nodes (::toggled-nodes db #{})
         next (if (contains? toggled-nodes k)
                (disj toggled-nodes k)
                (conj toggled-nodes k))]
     (assoc db ::toggled-nodes next))))


(rf/reg-sub
 ::toggled-nodes
 ::toggled-nodes)


(defn- render-node
  ([toggled-nodes node] (render-node toggled-nodes node []))
  ([toggled-nodes node path]
   (cond
     (nil? node) "Not Initialized"

     (map? node)
     [:div
      (->> node
           (map-indexed
            (fn [i [k v]]
              (let [node-path (conj path k)
                    open?     (contains? toggled-nodes node-path)]
                (if (map? v)
                  [:div {:key i}
                   [:strong {:style    {:cursor "pointer"}
                             :on-click #(rf/dispatch [::toggle-map-node node-path])}
                    (if open? "^ " "> ") (str k)]
                   (when open?
                     [:div {:style {:margin-left "12px"}}
                      (render-node toggled-nodes v node-path)])]
                  [:div {:key i :style {:margin-left "14px"}}
                   [:strong (str k) " "] (render-node toggled-nodes v node-path)])))))]

     :else
     [:span (str node)])))


(defn state []
  (let [current-state @(rf/subscribe [::sub/current-state])
        toggled-nodes @(rf/subscribe [::toggled-nodes])]
    [:div
     [:h1.heading "State"]
     (render-node toggled-nodes current-state)]))


(defn previous-state []
  [:button.button.is-danger.is-small
   {:on-click #(rf/dispatch [::->previous-state])}
   "<- BACK"])
