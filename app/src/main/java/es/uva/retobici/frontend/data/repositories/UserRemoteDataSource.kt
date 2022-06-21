package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.source.api.UserAPI
import es.uva.retobici.frontend.data.source.dto.toUserAuthenticated
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val api:UserAPI
): UserRepository {
    override suspend fun login(email: String, password:String): User {
        return withContext(Dispatchers.IO){
            val response = api.login(email, password)
            response.body()!!.toUserAuthenticated()
        }
    }

    override suspend fun logout(): User {
        TODO("Not yet implemented")
    }

}