package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.UserRemoteDataSource
import es.uva.retobici.frontend.domain.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
){
    suspend operator fun invoke(email: String, password: String): User {
        return remoteDataSource.login(email, password)
    }
}
