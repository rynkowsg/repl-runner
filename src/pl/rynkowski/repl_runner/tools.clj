(ns pl.rynkowski.repl-runner.tools
  (:require
   [pl.rynkowski.repl-runner.utils :as rru]))

(def morse-autostart-var-name "pl.rynkowski.repl-runner.morse-autostart")

(defn start-morse-if-requested []
  (let [requested? (some-> (System/getProperties) (get morse-autostart-var-name) boolean)]
    (when requested?
      (let [launch-morse-fn (rru/require-fn 'dev.nu.morse/launch-in-proc {:throw-on-fail? false
                                                                          :failure-msg "Failed to find morse in classpath"})]
        (launch-morse-fn)))))
