(ns quil-shadows.core
  (:require [cljs.core.match :as m :include-macros true]
            [quil.core :as q :include-macros true]
            [quil.middleware :as mi]
            [quil-shadows.art :as a]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb 360 100 100 1)
  {:phase :welcome
   ;; prevent drawing in top left corner
   :mouse-moved? false
   :start-color-cycling? false
   ;; used to cycle colors
   :tick 0})

(defn update-state [state]
  (let [{:keys [tick mouse-x mouse-y mouse-moved?
                phase start-color-cycling?]} state
        started? (not= :welcome phase)]
    {:tick (if (q/mouse-pressed?) (inc tick) tick)
     :fill (mod tick 360)
     :stroke (mod tick 50)
     :mouse-moved? (and started?
                        (or mouse-moved?
                            (not= (q/mouse-x) mouse-x)
                            (not= (q/mouse-y) mouse-y)))
     :mouse-x (q/mouse-x)
     :mouse-y (q/mouse-y)
     :space-pressed? (and (= :space (q/key-as-keyword))
                          (q/key-pressed?))
     ;; start cycling after mouse is pressed
     :start-color-cycling? (and started? (or start-color-cycling? (q/mouse-pressed?)))
     :phase (m/match [phase (q/key-as-keyword) (q/mouse-pressed?)]
                     [:welcome :space _] :first-draw
                     [:welcome _ true] :first-draw
                     [:welcome _ false] :welcome
                     [:first-draw _ _] :active
                     [:active _ _] :active)}))

(defn draw-colorful-circles
  "Helper fn to draw colorful or b&w circles."
  [colorful? stroke fill x y]
  (let [[fill-vals stroke-vals] (if colorful?
                                  ;; draw rainbow circles
                                  [[fill 27 98]
                                   [stroke 20 100]]
                                  ;; draw black and white circles
                                  [[0 0 100]
                                   [0 0 0]])]
            (apply q/fill fill-vals)
            (apply q/stroke stroke-vals)
            (q/ellipse x y 150 150)))

(defn draw-state
  "Main draw fn."
  [state]
  (let [font "Courier New"
        {:keys [phase mouse-x mouse-y mouse-moved? start-color-cycling?
                space-pressed? fill stroke]} state]
    (condp = phase
      :welcome (do (a/clear-screen)
                   (a/draw-message font
                                   "Welcome friend"
                                   "Press space (or click) to start"))
      :first-draw (a/clear-screen)
      :active (do (when space-pressed?
                    (a/clear-screen))
                  (a/draw-message font
                                  "Click to rainbow. Press space to clear" nil)
                  (when mouse-moved?
                    (draw-colorful-circles start-color-cycling?
                                           stroke
                                           fill
                                           mouse-x
                                           mouse-y))))))

(defn run-sketch
  "Helper fn to start the sketch."
  []
  (q/defsketch quil-cljs
    :host "quil-cljs"
    :size [(.-innerWidth js/window)
           (.-innerHeight js/window)]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [mi/fun-mode]))

;; start the sketch in browser
(run-sketch)
