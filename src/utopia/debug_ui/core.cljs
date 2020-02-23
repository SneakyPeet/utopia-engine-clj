(ns ^:figwheel-hooks utopia.debug-ui.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [utopia.core :as u]
            [goog.events :as events]))


;;;; EVENTS

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:game-state (u/start)}))




;;;; START

(defn mount []
  (r/render [:h1 "works"] (js/document.getElementById "app")))


(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])
  (mount))


(defn ^:after-load re-render
  []
  (mount))


(events/listen js/window "load" #(run))
