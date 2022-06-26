package es.uva.retobici.frontend.ui.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import es.uva.retobici.frontend.ui.data.LoginRepository
import es.uva.retobici.frontend.ui.data.Result

import com.mapbox.navigation.examples.R
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.domain.model.User
import es.uva.retobici.frontend.domain.usecase.LogOutUseCase
import es.uva.retobici.frontend.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    //private val loginRepository: LoginRepository,
    private val loginUseCase: LoginUseCase,
    private val logOutUseCase: LogOutUseCase
    ) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _logoutResult = MutableLiveData<Boolean>()
    val logoutResult: LiveData<Boolean> = _logoutResult

    val loading = MutableLiveData(false)

    fun saveAuthToken(token: String) = viewModelScope.launch {
        loginUseCase.saveAuthToken(token)
    }

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            loading.value = true
            val result:Result<User> = loginUseCase(username, password)
            loading.value = false
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success =  result.data)
                loginUseCase.saveAuthToken(result.data.token)
                loginUseCase.saveUserInfo(result.data)
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun logOut(){
        viewModelScope.launch {
            loading.postValue(true)
            val result = logOutUseCase()
            loading.postValue(false)
            _logoutResult.postValue(result)
        }
    }
}