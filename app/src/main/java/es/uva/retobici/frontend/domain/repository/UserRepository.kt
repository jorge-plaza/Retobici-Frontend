package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): User
    suspend fun logout():User
}