package ie.wit.forumzillav2.models

class User(val uid: String, val username: String) {
    constructor() : this("", "")
}