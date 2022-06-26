package es.uva.retobici.frontend.data

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import es.uva.retobici.frontend.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object{
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_POINTS = intPreferencesKey("user_points")
    }

    suspend fun saveAuthToken(token: String){
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun saveUserInfo(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = user.email
            preferences[USER_POINTS] = user.points
        }
    }

    suspend fun updatePoints(points: Int){
        dataStore.edit { preferences ->
            preferences[USER_POINTS] = points
        }
    }

    suspend fun cleanUser() {
        dataStore.edit { preferences ->
            preferences[USER_POINTS] = -1
            preferences[USER_EMAIL] = "invalid"
            preferences[AUTH_TOKEN] = "invalid"
        }
    }

    val authToken: Flow<String?>
    get() = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    val points: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[USER_POINTS]
        }

    val email: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
}