package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.UserRemoteDataSource
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.ui.data.Result
import es.uva.retobici.frontend.ui.data.model.LoggedInUser
import java.io.IOException
import java.util.*
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
){
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            val user = remoteDataSource.login(email, password)
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }
}
