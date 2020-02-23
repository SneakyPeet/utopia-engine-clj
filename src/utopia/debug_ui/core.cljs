(ns ^:figwheel-hooks utopia.debug-ui.core
  (:require [reagent.core :as r]))

(js/console.log "Hello there world!")



(defn mount []
  (r/render [:h1 "works"] (js/document.getElementById "app")))


(defn ^:export run
  [] (mount))


(defn ^:after-load re-render
  []
  (mount))
