package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.data.source.api.UserAPI
import es.uva.retobici.frontend.data.source.dto.toUserAuthenticated
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val api:UserAPI,
    private val userPreferences: UserPreferences
): UserRepository {
    override suspend fun login(email: String, password:String): User {
        return withContext(Dispatchers.IO){
            val response = api.login(email, password)
            Log.d("ibai", response.body().toString())
            response.body()!!.toUserAuthenticated()
        }
    }

    override suspend fun logout(): Boolean {
        return withContext(Dispatchers.IO){
            val token = userPreferences.authToken.first()
            Log.d("ibai", token!!)
            val response = api.logout("Bearer ${token!!}")
            Log.d("ibai", response.body().toString())
            Log.d("ibai", response.code().toString())
            response.body()!!
        }
    }

}