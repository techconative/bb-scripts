#!/usr/bin/env bb

(require
   '[clojure.tools.cli :refer [parse-opts]]
   '[clojure.java.io :as io]
   '[clojure.string :as str])

(def progname "fequencies")
(def ^:dynamic *debug* false)

(def cli-options
  [["-f" "--file FILE" "File to process" :parse-fn #(slurp %)]
   ["-s" "--sort SORT" "Sort column" :validate [#(<= 1 % 2) :default 1]]
   ["-d" "--descending" "Sort in descending order"]
   ["-h" "--help"]
   ["-D" "--debug"]])

(defn print-usage
  [progname summary]
  (println "usage: " progname " [opts]")
  (println " ")
  (println "options:")
  (println summary))

(defn find-errors
  [parsed]
  (let [{:keys [errors options]} parsed
        {:keys [help]} options]
    (cond
      help
      {:exit 0}
      errors
      {:message (str/join "\n" errors)
       :exit 1}
      )))

(defn print-errors
  [progname parsed errors]
  (let [{:keys [summary]} parsed
        {:keys [message exit]} errors]
    (when message
      (println message)
      (println " "))
    (print-usage progname summary)
    exit))

(defn format
  [data options]
    (reduce-kv #(str %1  %2 "\t"  %3 "\n") "" data))

(defn get-data
[options]
(if (:file options)
  (str/split (:file options) #"\n") 
  *input*))

(defn debug 
  ([dat msg]
	(when (true? *debug*)
   (println "**Debug**" msg)
   (println dat)
   (println "*******"))
  dat)
  ([dat]
   (debug dat "")))

(let [parsed (parse-opts *command-line-args* cli-options)
      {:keys [options]} parsed]
  (or (some->> (find-errors parsed)
               (print-errors progname parsed)
               (System/exit))
      (binding [*debug* (:debug options)]
        (-> (get-data options)
            (debug "input data")
            (frequencies)
            (debug "frequency")
            (format options)
            (print)))))
