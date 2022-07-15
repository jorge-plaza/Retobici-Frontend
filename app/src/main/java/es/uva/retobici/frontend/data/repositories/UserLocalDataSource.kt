package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.domain.model.User
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