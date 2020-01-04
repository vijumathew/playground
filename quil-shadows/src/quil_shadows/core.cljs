(ns quil-shadows.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 120)
  (q/color-mode :hsb 360 100 100 1)
  {:color 255
   :started? false
   :mouse-moved? false
   :mouse-started? false
   ;; mouse position always starts at 0
   :mouse-x 0
   :mouse-y 0
   :tick 0})

(defn update-state [state]
  {:tick (if (q/mouse-pressed?) (inc (:tick state)) (:tick state))
   :fill (mod (:tick state) 360)
   :stroke (mod (:tick state) 50)
   :mouse-moved? (or (:mouse-moved? state)
                     (not= (q/mouse-x) (:mouse-x state))
                     (not= (q/mouse-y) (:mouse-y state)))
   :mouse-x (q/mouse-x)
   :mouse-y (q/mouse-y)
   :space-pressed? (and (= :space (q/key-as-keyword))
                        (q/key-pressed?))
   :started? (or (:started? state) (= :space (q/key-as-keyword)))
   :mouse-started? (or (:mouse-started? state) (q/mouse-pressed?))})

(defn draw-state [state]
  (let [font "Courier New"
        {:keys [started? mouse-x mouse-y mouse-moved? mouse-started?
                space-pressed? fill stroke]} state]
    (if-not started?
      (do
        (q/background 255)
        (q/fill 0)
        (q/text-font font 20)
        (q/text "Welcome friend" 20 30)
        (q/text-font font 30)
        (q/text "Press space to start" 20 100))

      (do (q/with-fill 0
            (q/text-font font 20)
            (q/text "Press space to clear" 20 30))
          (when space-pressed?
            (q/background 255))
          (let [[f s] (if mouse-started?
                        [fill stroke]
                        [360 0])]
            (q/fill f 27 98)
            (q/stroke s 20 100))
          (when (and mouse-x mouse-y mouse-moved?)
            (q/ellipse mouse-x mouse-y 150 150))))))

(defn run-sketch []
  (q/defsketch quil-cljs
    :host "quil-cljs"
    :size [(.-innerWidth js/window)
           (.-innerHeight js/window)]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(run-sketch)
