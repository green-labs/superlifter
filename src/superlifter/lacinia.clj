(ns superlifter.lacinia
  (:require [com.walmartlabs.lacinia.resolve :as resolve]
            [clojure.tools.logging :as log]
            [superlifter.api :as api]))

(defn ->lacinia-promise [sl-result]
  (let [l-prom (resolve/resolve-promise)]
    (api/unwrap (fn [result]
                  (resolve/deliver! l-prom result))
                (fn [error]
                  (log/error "Error in promise!" error)
                  (resolve/deliver! l-prom nil {:message (.getMessage error)}))
                sl-result)
    l-prom))

(defmacro with-superlifter [ctx body]
  `(api/with-superlifter (get-in ~ctx [:request :superlifter])
     (->lacinia-promise ~body)))
