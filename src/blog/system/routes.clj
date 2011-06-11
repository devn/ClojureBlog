(ns blog.system.routes
  (:use compojure.core,
	ring.adapter.jetty,
	ring.util.response,
	ring.middleware.session,
	blog.system.utils)
  (:require [blog.controllers.c-post :as post]
	    [blog.controllers.c-comment :as comment]
	    [blog.controllers.c-user :as user]	    
	    [compojure.route :as route]
	    [compojure.handler :as handler]
	    [clj-soy.template :as soy]
	    [clojure.contrib.sql :as sql]))

(defroutes main-routes
  (GET "/" [] (redirect-to "/post"))
  (GET "/request" request (str request))

  ;; POST
  (route GET "/post" post/index)
  (route GET "/post/new" post/new)
  (route POST "/post/create" post/create)
  (route GET "/post/show/:id" post/show)
  (route GET "/post/edit/:id" post/edit)
  (route POST "/post/update"  post/update)
  (route GET "/post/delete/:id" post/delete)

  ;; COMMENT
  (route GET "/post/:post_id/comment/new" comment/new)
  (route POST "/post/:post_id/comment/create" comment/create)
  (route GET "/post/:post_id/comment/delete/:id" comment/delete)

  ;; USER
  (route GET "/login" user/login-page)
  (route POST "/login" user/login)
  (route GET "/logout" user/logout)
  (route GET "/nologin" user/nologin)

  
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      wrap-session))