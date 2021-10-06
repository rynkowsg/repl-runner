(ns repl-runner.core
  (:require
   [medley.core :refer [map-vals filter-vals]]
   [repl-runner.utils :as u]))

(defn find-mains []
  (->> {:clojure  'clojure.main/main
        :figwheel 'figwheel.main/-main
        :nrepl    'nrepl.cmdline/-main
        :rebel    'rebel-readline.main/-main
        :rebl     'cognitect.rebl/-main
        :reveal   'vlaaad.reveal/repl}
       (map-vals #(u/try-it (requiring-resolve %)))
       (filter-vals some?)))

; About nREPL REBL middleware
; There are two published middlewares that send forms to REBL:
; - https://github.com/RickMoynihan/nrebl.middleware
; - https://github.com/DaveWM/nrepl-rebl
; When using the first one, you have to take care of launching the REPL yourself
; and Cursive internal forms evaluations are sent too, which is annoying.
; When using the second one, it runs REBL itself and filters out all the Cursive internal forms.
;
; About REBL and Rebel
; At the moment I couldn't figure out how to support sending forms from Rebel to REBL.
; So you can either have REBL or Rebel.
;
; Inspiration:
; https://github.com/seancorfield/dot-clojure/blob/develop/dev.clj

(defn determine-strategy [mains]
  (cond
    (empty? mains)
    (throw (ex-info "No mains has been found." mains))

    (and (:nrepl mains) (:rebel mains) (:rebl mains))
    {:name "nREPL+Rebel+REBL"
     :exec #(let [nrepl-args (if (u/try-it (requiring-resolve 'nrebl.middleware/wrap-nrebl))
                               ["--interactive" "--middleware" "[nrebl.middleware/wrap-nrebl]" "--repl-fn" "rebel-readline.main/-main"]
                               ["--interactive" "--repl-fn" "rebel-readline.main/-main"])]
              (apply (:nrepl mains) nrepl-args))}

    (and (:nrepl mains) (:rebel mains))
    {:name "nREPL+Rebel"
     :exec #((:nrepl mains) "--interactive" "--repl-fn" "rebel-readline.main/-main")}

    (and (:nrepl mains) (:rebl mains))
    {:name "nREPL+REBL"
     :exec #(let [nrepl-args (if (u/try-it (requiring-resolve 'nrebl.middleware/wrap-nrebl))
                               ["--interactive" "--middleware" "[nrebl.middleware/wrap-nrebl]"]
                               ["--interactive"])]
              (apply (:nrepl mains) nrepl-args))}

    (and (:rebel mains) (:rebl mains))
    {:name "Rebel+REBL"
     :exec #(do ((requiring-resolve 'cognitect.rebl/ui)) ((:rebel mains)))}

    (and (:clojure mains) (:rebl mains))
    {:name "clojure.main+REBL"
     :exec #(do ((:rebl mains)) ((:nrepl mains)))}

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

(defn launch-repl [{:keys [name exec]}]
  (println "Starting" name "as the REPL...")
  (exec))

(defn- start-repl []
  (u/check-requiring-resolve)
  (u/setup-dcl)
  (u/setup-jedi)
  (-> (find-mains)
      (determine-strategy)
      (launch-repl)))

(defn -main [& _args]
  (start-repl)
  (System/exit 0))

#_(comment
   (-> (find-mains)
       (determine-strategy)
       (launch-repl)).)
