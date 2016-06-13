
(ns sagittariidae.fe.state
  (:require [re-frame.core :refer [register-sub]]
            [reagent.core :as reagent]
            [reagent.ratom :refer-macros [reaction]]))

(defonce null-state
  {:cached {:projects []
            :methods []}
   :project ""
   :sample {:id nil
            :stages []
            :active-stage {:id nil
                           :file-spec []
                           :upload {:file nil
                                    :progress 0.0
                                    :state :default}}
            :new-stage {:method {}
                        :annotation ""}}
   :resumable nil})

(def state
  (reagent/atom null-state))

(defn clear
  "Reset the part of the state tree denoted by `path` to its default state."
  [state path]
  (assoc-in state path (get-in null-state path)))

(defn copy-state
  "Copy elements of the application state from `src` to `dst`; `paths` is a
  collection of sequences, where each is a path that might be used with
  `assoc-in` or `get-in`.  This function makes no attempt to optimise the
  process by looking for longer paths that may be contained within shorter
  ones."
  [dst src paths]
  (loop [paths paths
         dst   dst]
    (if (seq paths)
      (let [path (first paths)]
        (recur (rest paths)
               (assoc-in dst path (get-in src path))))
      dst)))

(register-sub
 :query/projects
 (fn [state [query-id]]
   (assert (= query-id :query/projects))
   (reaction (get-in @state [:cached :projects]))))

(register-sub
 :query/methods
 (fn [state [query-id]]
   (assert (= query-id :query/methods))
   (reaction (get-in @state [:cached :methods]))))

(register-sub
 :query/active-project
 (fn [state [query-id]]
   (assert (= query-id :query/active-project))
   (reaction (:project @state))))

(register-sub
 :query/sample-id
 (fn [state [query-id]]
  (assert (= query-id :query/sample-id))
  (reaction (get-in @state [:sample :id]))))

(register-sub
 :query/sample-stages
 (fn [state [query-id]]
  (assert (= query-id :query/sample-stages))
  (reaction {:stages (get-in @state [:sample :stages])
             :active (get-in @state [:sample :active-stage :id])})))

(register-sub
 :query/sample-stage-detail
 (fn [state [query-id]]
  (assert (= query-id :query/sample-stage-detail))
  (reaction (get-in @state [:sample :active-stage]))))

(register-sub
 :query/sample-stage-input
 (fn [state [query-id]]
  (assert (= query-id :query/sample-stage-input))
  (reaction (get-in @state [:sample :new-stage]))))
