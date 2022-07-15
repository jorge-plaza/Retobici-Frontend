package es.uva.retobici.frontend.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.domain.usecase.LogOutUseCase
import es.uva.retobici.frontend.domain.usecase.LoginUseCase
import es.uva.retobici.frontend.ui.viewmodels.states.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @RelaxedMockK
    private lateinit var loginUseCase: LoginUseCase
    @RelaxedMockK
    private lateinit var logOutUseCase: LogOutUseCase
    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        loginViewModel = LoginViewModel(loginUseCase,logOutUseCase)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful login for user`() = runTest {
        val email = "jorge@uva.es"
        val password = "12345678"
        //Given
        val user = User(1,email,"token", 100)
        val response: Result<User> = Result.Success(user)
        coEvery { loginUseCase(email,password) } returns response as Result.Success

        //When
        loginViewModel.login(email,password)

        //Then
        coVerify(exactly = 1) { loginUseCase.invoke(email,password) }
        assertEquals(loginViewModel.loginResult.value?.success, response.data )
    }
}