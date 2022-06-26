package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.UserLocalDataSource
import es.uva.retobici.frontend.data.repositories.UserRemoteDataSource
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.ui.data.Result
import java.io.IOException
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
){
    suspend operator fun invoke(): Boolean {
        val success = remoteDataSource.logout()
        localDataSource.cleanUserInfo()
        return success
    }
}