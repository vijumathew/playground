(ns quil-shadows.art
  (:require [quil.core :as q :include-macros true]))

(defn clear-screen
  "Helper fn to paint a white background, clearing the screen."
  []
  (q/background 255))

(defn draw-message
  "Helper fn to draw text to screen.

  Text is black and in top right corner."
  [font text subtext]
  (do
    (q/with-fill 0
      (q/with-stroke 255
        (q/text-font font 20)
        (q/text text 20 30)
        (when subtext
          (q/text-font font 30)
          (q/text subtext 20 100))))))
