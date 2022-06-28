package es.uva.retobici.frontend.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.uva.retobici.frontend.data.source.api.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//Este modulo va a proveer de dependencias que no se puedan injectar directamente con hilt
@Module
@InstallIn(SingletonComponent::class) //activity
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.15:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * This is provided because you can not Inject an interface
     */
    @Singleton
    @Provides
    fun providesStopApi(retrofit: Retrofit): StopAPI {
        return retrofit.create(StopAPI::class.java)
    }

    /**
     * This is provided because you can not Inject an interface
     */
    @Singleton
    @Provides
    fun providesBikeApi(retrofit: Retrofit): BikeAPI {
        return retrofit.create(BikeAPI::class.java)
    }

    /**
     * This is provided because you can not Inject an interface
     */
    @Singleton
    @Provides
    fun providesRouteApi(retrofit: Retrofit): RouteAPI {
        return retrofit.create(RouteAPI::class.java)
    }

    /**
     * This is provided because you can not Inject an interface
     */
    @Singleton
    @Provides
    fun providesRewardApi(retrofit: Retrofit): RewardAPI {
        return retrofit.create(RewardAPI::class.java)
    }

    /**
     * This is provided because you can not Inject an interface
     */
    @Singleton
    @Provides
    fun providesUserApi(retrofit: Retrofit): UserAPI {
        return retrofit.create(UserAPI::class.java)
    }
}