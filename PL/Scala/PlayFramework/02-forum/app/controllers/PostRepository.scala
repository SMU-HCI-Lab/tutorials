package controllers

object PostRepository {
    var posts: Seq[Post] = Vector()

    def findAll: Seq[Post] = posts

    def add(post: Post): Unit = { posts = posts :+ post }
}