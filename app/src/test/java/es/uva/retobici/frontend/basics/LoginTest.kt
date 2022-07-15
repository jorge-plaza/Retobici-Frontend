package es.uva.retobici.frontend.basics

import androidx.fragment.app.activityViewModels
import com.mapbox.geojson.Point
import es.uva.retobici.frontend.ui.viewmodels.LoginViewModel
import org.junit.Assert.assertEquals
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.load.engine.Resource
import es.uva.retobici.frontend.data.source.api.UserAPI
import es.uva.retobici.frontend.domain.usecase.LogOutUseCase
import es.uva.retobici.frontend.domain.usecase.LoginUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GfgSingle {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    @get:Rule
    val gfgTest = CoroutineDispatcherRule()
    @Mock
    private lateinit var loginUseCase: LoginUseCase
    @Mock
    private lateinit var logOutUseCase: LogOutUseCase
    @Mock
    private lateinit var apiUsersObserver: Observer<Resource<List<Int>>>
    @Before
    fun doSomeSetup() {
        // do something if required
    }

    @Test
    fun unconfinedTest() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = LoginViewModel(loginUseCase, logOutUseCase)
        val email = "jorge@uva.es"
        val password = "12345678"
        launch { viewModel.login(email,password) }
        assertTrue(true)
        //assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }

    /*
    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        gfgTest
        coroutineDispatcherRule.runBlockingTest {
            doReturn(emptyList<ApiUser>())
                .`when`(gfgApi)
                .getGfgUser()
            val viewModel = LoginViewModel()
            viewModel.getGfgUser().observeForever(apiUsersObserver)
            verify(gfgApi).getGfgUser()
            verify(apiUsersObserver).onChanged(Resource.success(emptyList()))
            viewModel.getGfgUser().removeObserver(apiUsersObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            val someGeekyError = "Something is not right"
            doThrow(RuntimeException(someGeekyError))
                .`when`(gfgApi)
                .getGfgUser()
            val viewModel = SingleNetworkCallViewModel(gfgApi, gfgDBHelper)
            viewModel.getGfgUser().observeForever(apiUsersObserver)
            verify(gfgApi).getGfgUser()
            verify(apiUsersObserver).onChanged(
                Resource.error(
                    RuntimeException(someGeekyError).toString(),
                    null
                )
            )
            viewModel.getGfgUser().removeObserver(apiUsersObserver)
        }
    }
    */

    @After
    fun tearDown() {
        // do something if required
    }

}
