(ns cursocinco-bancofiltrado.exploring)

(def population {::zombies 2700 ::humans 9})
(def per-capita (/ (population ::zombies) (population ::humans)))
(def per-capita-2 (/ (::zombies population) (::humans population)))

(println population)
(println per-capita)
(println per-capita-2)

