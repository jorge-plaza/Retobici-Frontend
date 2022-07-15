package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.UserLocalDataSource
import es.uva.retobici.frontend.data.repositories.UserRemoteDataSource
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.ui.viewmodels.states.Result
import java.io.IOException
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
){
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            val user = remoteDataSource.login(email, password)
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }
    suspend fun saveAuthToken(token: String){
        localDataSource.saveAuthToken(token)
    }

    suspend fun saveUserInfo(user: User) {
        localDataSource.saveUserInfo(user)
    }
}
