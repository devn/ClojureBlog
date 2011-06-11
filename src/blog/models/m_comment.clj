(ns blog.models.m-comment
  (:use blog.system.db
	blog.system.utils)
  (:require [clojure.contrib.sql :as sql]))

(defn fetch-list [post-id]
  (doall
   (map struct-map->soy
	(doall
	 (sql/with-connection *db*
	   (sql/with-query-results comment
	     [(str "SELECT * FROM comment where post_id = " post-id " order by id desc")]
	     (doall comment)))))))

(defn create [comment]
  (sql/with-connection *db*
    (sql/insert-values "comment" ["title" "body" "post_id"]
		       [(:title comment)
			(:text comment)
			(:post_id comment)])))

(defn fetch [post-id id]
  (struct-map->soy
   (sql/with-connection *db*
     (sql/with-query-results comment
       [(str "SELECT * FROM comment where id = " id " and post_id = " post-id)]
       (first (doall comment))))))

(defn update [comment]
  (sql/with-connection *db*
    (sql/update-values "comment" [(str "id=" (:id comment))] comment)))


(defn delete [id]
  (sql/with-connection *db*
    (sql/delete-rows "comment" [(str "id=" id)])))

