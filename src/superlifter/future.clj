(ns superlifter.future
  (:import
   java.util.concurrent.Callable))

(def default-executor
  clojure.lang.Agent/pooledExecutor)

(defn binding-conveyor-fn
  {:private true
   :added "1.3"}
  [f]
  (let [frame (clojure.lang.Var/cloneThreadBindingFrame)]
    (fn
      ([]
       (clojure.lang.Var/resetThreadBindingFrame frame)
       (f))
      ([x]
       (clojure.lang.Var/resetThreadBindingFrame frame)
       (f x))
      ([x y]
       (clojure.lang.Var/resetThreadBindingFrame frame)
       (f x y))
      ([x y z]
       (clojure.lang.Var/resetThreadBindingFrame frame)
       (f x y z))
      ([x y z & args]
       (clojure.lang.Var/resetThreadBindingFrame frame)
       (apply f x y z args)))))

(defn ^:private deref-future
  ([^java.util.concurrent.Future fut]
   (.get fut))
  ([^java.util.concurrent.Future fut timeout-ms timeout-val]
   (try (.get fut timeout-ms java.util.concurrent.TimeUnit/MILLISECONDS)
        (catch java.util.concurrent.TimeoutException e
          timeout-val))))

(defn future-call-with-executor
  "Takes a function of no args (and optionally an ExecutorService
  instance that will run the task, by default it uses pooledExecutor) 
  and yields a future object that will invoke the function in another thread, and will
  cache the result and return it on all subsequent calls to
  deref/@. If the computation has not yet finished, calls to deref/@
  will block, unless the variant of deref with timeout is used. See
  also - realized?."
  ([f executor]
   (let [f (binding-conveyor-fn f)
         fut (.submit ^java.util.concurrent.ExecutorService executor
                      ^Callable f)]
     (reify
       clojure.lang.IDeref
       (deref [_] (deref-future fut))
       clojure.lang.IBlockingDeref
       (deref
         [_ timeout-ms timeout-val]
         (deref-future fut timeout-ms timeout-val))
       clojure.lang.IPending
       (isRealized [_] (.isDone fut))
       java.util.concurrent.Future
       (get [_] (.get fut))
       (get [_ timeout unit] (.get fut timeout unit))
       (isCancelled [_] (.isCancelled fut))
       (isDone [_] (.isDone fut))
       (cancel [_ interrupt?] (.cancel fut interrupt?)))))
  ([f]
   (future-call-with-executor f clojure.lang.Agent/pooledExecutor)))

(defmacro future-with-pooled-executor
  "Takes a executor and body of expressions and yields a future object that will
  invoke the body in another thread, and will cache the result and
  return it on all subsequent calls to deref/@. If the computation has
  not yet finished, calls to deref/@ will block, unless the variant of
  deref with timeout is used. See also - realized?."
  ([& body] `(future-call-with-executor (^{:once true} fn* [] ~@body) default-executor)))
