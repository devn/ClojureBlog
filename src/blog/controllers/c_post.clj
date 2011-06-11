(ns blog.controllers.c-post
  (:use	blog.system.utils)
  (:require [blog.models.m-post :as post]
	    [blog.models.m-comment :as comment]
	    [clj-soy.template :as soy]
	    [clojure.contrib.sql :as sql]))

;; Action /post/index is requested via GET.
;; It checks whether the user is authenticated and thus
;; thus can create/update/delete posts.
;; Renders the post.index template.
(defaction index []
  (let [editable (if (empty? (:user session)) false true)]
    (render-action post#index
		   {:post_list (post/fetch-list)
		    :extra (map->soy {:editable editable})})))

;; Action /post/new is requested via GET.
;; It checks whether the user is authenticated
;; and if so, renders the post.new template.
(defaction new []
  (check-auth 
   (render-action post#new
		  {})))

;; Action /post/new is requested via POST.
;; It checks whether the user is authenticated
;; and if so 1) creates the specified post
;; using the posted html-form;
;; 2) redirects to the home page.
(defaction create []
  (check-auth
   (post/create params)
   (redirect-to "/post")))

;; Action /post/show/:id is requested via GET.
;; It fetches the specified post and
;; renders the post.show template.
(defaction show [id]
  (render-action post#show
		 {:post (post/fetch id)
		  :comment_list (comment/fetch-list id)}))

;; Action /post/edit/:id is requested via GET.
;; It checks whether the user is authenticated
;; and if so, it fetches the specified post and
;; renders the post.edit template.
(defaction edit [id]
  (check-auth
   (render-action post#edit
		  {:post (post/fetch id)
		   :comment_list (comment/fetch-list id)})))

;; Action /post/update is requested via POST.
;; It checks whether the user is authenticated
;; and if so 1) updates the specified post using
;; the posted html-form;
;; 2) redirects to the /post/show/:id
(defaction update []
  (check-auth
   (post/update params)
   (with-session {}
     (redirect-to (str "/post/show/" (:id params))))))

;; Action /post/delete/:id is requested via GET.
;; It checks whether the user is authenticated
;; and if so 1) deletes the specified post;
;; 2) redirects to the home page.
(defaction delete [id]
  (check-auth
   (post/delete id)
   (redirect-to "/post")))
