(ns superlifter.lacinia
  (:require [com.walmartlabs.lacinia.resolve :as resolve]
            [superlifter.api :as api]))

(defn ->lacinia-promise [sl-result]
  (let [l-prom (resolve/resolve-promise)]
    @(api/unwrap (fn [result]
                   (resolve/deliver! l-prom result))
                 (fn [error]
                   (resolve/deliver! l-prom nil {:message (.getMessage error)}))
                 sl-result)))

(defmacro with-superlifter [ctx body]
  `(api/with-superlifter (get-in ~ctx [:request :superlifter])
     (->lacinia-promise ~body)))
