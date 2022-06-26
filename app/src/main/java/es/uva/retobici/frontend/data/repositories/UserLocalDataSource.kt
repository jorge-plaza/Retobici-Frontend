package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.data.source.api.UserAPI
import es.uva.retobici.frontend.data.source.dto.toUserAuthenticated
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val userPreferences: UserPreferences,
){

    suspend fun saveAuthToken(token: String){
        userPreferences.saveAuthToken(token)
    }

    suspend fun saveUserInfo(user: User) {
        userPreferences.saveUserInfo(user)
    }

    suspend fun cleanUserInfo(){
        userPreferences.cleanUser()
    }
}