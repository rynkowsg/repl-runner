(ns pl.rynkowski.repl-runner
  (:require
   [medley.core :refer [map-vals filter-vals]]
   [pl.rynkowski.repl-runner.checks :as rrc]
   [pl.rynkowski.repl-runner.tools :as rrt]
   [pl.rynkowski.repl-runner.utils :as rru]))

(defn find-mains []
  "Composes a map of popular REPL starting main functions."
  (->> {:clojure 'clojure.main/main
        :figwheel 'figwheel.main/-main
        :nrepl 'nrepl.cmdline/-main
        :rebel 'rebel-readline.main/-main
        :rebl 'cognitect.rebl/-main
        :morse 'dev.nu.morse/-main
        :reveal 'vlaaad.reveal/repl}
       (map-vals #(rru/try-it (requiring-resolve %)))
       (filter-vals some?)))

; Inspired by: https://github.com/seancorfield/dot-clojure/blob/3c12189d333c719e387fd271a1c2d9f3eeb89a6c/dev.clj
(defn determine-strategy [mains]
  "Depends on the given main fns provided, determine strategy for the REPL start process."
  (cond
    (empty? mains)
    (throw (ex-info "No mains has been found." mains))

    (and (:nrepl mains) (:rebel mains) (:rebl mains))
    {:name "nREPL+Rebel+REBL"
     :exec #(let [nrepl-args (if (rru/try-it (requiring-resolve 'nrebl.middleware/wrap-nrebl))
                               ["--interactive" "--middleware" "[nrebl.middleware/wrap-nrebl]" "--repl-fn" "rebel-readline.main/-main"]
                               ["--interactive" "--repl-fn" "rebel-readline.main/-main"])]
              (apply (:nrepl mains) nrepl-args))}

    (and (:nrepl mains) (:rebel mains))
    {:name "nREPL+Rebel"
     :exec #((:nrepl mains) "--interactive" "--repl-fn" "rebel-readline.main/-main")}

    (and (:nrepl mains) (:rebl mains))
    {:name "nREPL+REBL"
     :exec #(let [nrepl-args (if (rru/try-it (requiring-resolve 'nrebl.middleware/wrap-nrebl))
                               ["--interactive" "--middleware" "[nrebl.middleware/wrap-nrebl]"]
                               ["--interactive"])]
              (apply (:nrepl mains) nrepl-args))}

    (and (:nrepl mains) (:morse mains))
    {:name "nREPL+morse"
     :exec #(let [nrepl-args (if (rru/try-it (requiring-resolve 'pl.rynkowski.morse-nrepl/wrap))
                               ["--interactive" "--middleware" "[pl.rynkowski.morse-nrepl/wrap]"]
                               ["--interactive"])]

              (apply (:nrepl mains) nrepl-args))}

    (and (:rebel mains) (:rebl mains))
    {:name "Rebel+REBL"
     :exec #(do ((requiring-resolve 'cognitect.rebl/ui)) ((:rebel mains)))}

    (and (:rebel mains) (:morse mains))
    {:name "Rebel+morse"
     :exec #(do ((requiring-resolve 'cognitect.rebl/launch-in-proc)) ((:rebel mains)))}

    (and (:clojure mains) (:rebl mains))
    {:name "clojure.main+REBL"
     :exec #(do ((:rebl mains)) ((:nrepl mains)))}

    (and (:clojure mains) (:morse mains))
    {:name "clojure.main+morse"
     :exec #(do ((:morse mains)) ((:nrepl mains)))}

    (:nrepl mains)
    {:name "Interactive nREPL"
     :exec #((:nrepl mains) "--interactive")}

    (:rebel mains)
    {:name "Rebel"
     :exec #((:rebel mains))}

    (:figwheel mains)
    {:name "Figwheel"
     :exec #((:figwheel mains) "-b" "dev" "-r")}

    (:clojure mains)
    {:name "clojure.main"
     :exec #((:clojure mains))}

    :else
    (throw (ex-info "The available mains are not supported" mains))))

(comment
 (find-mains)
 (-> (find-mains) (determine-strategy)))

(defn launch-repl
  [{:keys [name exec]}]
  (println "Starting" name "as the REPL...")
  (exec))

(defn- start-repl []
  (rrc/check-requiring-resolve)
  (rrt/start-morse-if-requested)
  (-> (find-mains)
      (determine-strategy)
      (launch-repl)))

(defn -main [& _args]
  (start-repl)
  (System/exit 0))
