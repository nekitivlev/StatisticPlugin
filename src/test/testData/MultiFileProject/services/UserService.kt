package services

import models.User

class UserService {
    fun getUser(id: Int): User {
        return User(id, "Sample User")
    }
}
