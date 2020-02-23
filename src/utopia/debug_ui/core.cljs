(ns ^:figwheel-hooks utopia.debug-ui.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            [utopia.debug-ui.events :as evt]
            [utopia.debug-ui.subs :as subs]))




(defn app []
  [:div
   [:h1 "works"]
   [:p (str @(rf/subscribe [::subs/game-state]))]
   [:p (str @(rf/subscribe [::subs/available-actions]))]
   [:p (str (or @(rf/subscribe [::subs/rules-state]) "nil"))]
   [:p (str @(rf/subscribe [::subs/effects]))]
   [:p (str @(rf/subscribe [::subs/errors]))]])


(defn mount []
  (r/render (app) (js/document.getElementById "app")))


(defn ^:export run
  []
  (rf/dispatch-sync [::evt/initialize])
  (mount))


(defn ^:after-load re-render
  []
  (mount))


(events/listen js/window "load" #(run))
