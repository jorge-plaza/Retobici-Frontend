package es.uva.retobici.frontend.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val applicationContext = context.applicationContext
    private val dataStore = applicationContext.dataStore

    companion object{
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthToken(token: String){
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    val authToken: Flow<String?>
    get() = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }
}