(ns blog.system.utils
  "The namespace contains common functionality."
  (:use ring.util.response,
	compojure.core)
    (:require [clj-soy.template :as soy]))

(def ^{:doc "The name of the directory where the teplates can be found"}
  *templates* "templates")

(defn ^{:doc "Just a syntax sugar for ring.util.response/redirect"}
  redirect-to [addr]
  (ring.util.response/redirect addr))

(defn ^{:doc "When you pass nested parameters to clj-soy, you should
transform your map substituting the keys with strings.
The macros map->soy does this: (map->soy {:a 1, :b 2})
returns {\"a\" 1, \"b\" 2}."}
  map->soy [my-map]		;; Assume my-map contains {:a 1, :b 2}			
  (let [map-keys (keys my-map)	;; map-keys = (:a :b)
	string-keys (map #(subs (str %) 1) map-keys)	;; string-keys = ("a" "b")
	map-string-keys (reduce				;; map-string-keys = {:b "b", :a "a"}
			 (fn [m v]
			   (assoc m (first v) (second v))) {}
			   (map (fn [v1 v2] [v1 v2]) map-keys string-keys))
	updated-map (clojure.set/rename-keys my-map map-string-keys)]	;; updated-map = {"a" 1, "b" 2}
    updated-map))

(defn ^{:doc "Transforms struct-map to soy structure"}
  struct-map->soy [my-struct-map]
  (let [my-map (reduce (fn [m k] (assoc m k (k my-struct-map))) {} (keys my-struct-map))] ;; Transforms struct-map to map
    (map->soy my-map)))

(defn ^{:doc "Builds and renders the soy-template.
	Parameters:
	  template-file -- template file to build
	  template-ns -- full template name including the namespace
	  params -- template parameters."}
  render [template-file template-ns params]
  (let [tpl (soy/build template-file)]
    (soy/render tpl
		template-ns
		params)))

(defn ^{:doc "Renders an enclosing template (application template)
and includes the child template.
	Paremeters:
	  template-file -- child template file
	  template-ns -- full name of child template
	  params -- child template
	  app-template-file -- enclosing template file
	  app-template-ns -- full enclosing template name"}
  decorate-page [template-file template-ns params
		     app-template-file app-template-ns]
  (let [content (render template-file template-ns params)]
    (render app-template-file
	    app-template-ns
	    {:content content})))

(defn ^{:doc "Knows the exact paths to templates and their names.
Renders a correct template for each entity. Different entities
may have different enclosing templates, e.g. post has it's own
decorations and user -- it's own. So the structure of directories
for action \"new\" of the entity \"post\" should be
\"blog\\templates\\post\\new.soy\" and his enclosing template
should be located in \"blog\\templates\\post.soy\". The full name
of the enclosing template should be \"app.post\". The full name of
the child template should be \"post.new\""}
  render-rest-page [entity action params]
  (decorate-page (str *templates* "/" entity "/" action ".soy")
		 (str entity "." action)
		 params
		 (str *templates* "/app/" entity ".soy")
		 (str "app." entity)))

(defmacro ^{:doc "Parses the specified action and renders the corresponding
decorated template. The action should have format <entity>#<action>. E.g.:
post#new, comment#create, user#login. Usage example:
(render-action post#index {})"}
  render-action [action params]
  `(let [splitted# (.split ~(str action) "#")
	 entities# (first splitted#)
	 action# (second splitted#)]
     (render-rest-page entities# action# ~params)))

(defmacro ^{:doc "Just a syntax sugar for Compojure routing api.
(route GET \"/post/index\" post/index) will generate
(GET \"/post/index\" request (post/index request) "}
  route [method route function]
    `(~method ~route request# (~function request#)))

(defmacro ^{:doc "Automates parameters desctructuring for the user function.
Each user function defined in the routing table should accept just one argument --
request, which is a huge hash-map of request arguments.
Macro defaction destructures some of the parameters.
It produces two system parameters:
session -- the hash-map which contains the session values
params -- all the user parameters passed through the URL or through the posted form.
Additionally, the macro destructures all the user parameters specified in the
function argument list.
Usage: (defaction create [post_id] (println post_id))
Outcome: (defn create [{session :session, {post_id :post_id} :params, params :params}]
(println post_id))
Inside the generated function create there will be three local variables available:
session, params and post_id."}
  defaction [name & params-with-body]
  (let [params-vector (first params-with-body)
	params-hash (apply hash-map (flatten (map (fn [p] [p (keyword p)]) params-vector)))
	body (next params-with-body)
	session (symbol "session")
	params (symbol "params")]
    `(defn ~name [{~params-hash :params, ~params :params,  ~session :session}]
       ~@body)))

(defn ^{:doc "Updates the session while processing the POST-request.
	Parameters:
	  session -- (hash-map) extra session parameters or updated session.
	  response -- the result of \"redirect-to function.\""}
  with-session [session response]
  (merge response
	 {:session
	  (merge (response :session)
		 session)}))

(defmacro ^{:doc "Checks whether there is non-empty :user field in the session.
If there is such a field, then it evaluates the body. Otherwise it
redirects to the home page."}
  check-auth [& body]
  (let [session (symbol "session")]
    `(let [user# (:user ~session)]
       (if (empty? user#)
	 (redirect-to "/")
	 (do ~@body)))))