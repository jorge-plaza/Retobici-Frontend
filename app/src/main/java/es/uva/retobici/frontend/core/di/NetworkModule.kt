package es.uva.retobici.frontend.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.uva.retobici.frontend.data.source.api.BikeAPI
import es.uva.retobici.frontend.data.source.api.StopAPI
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
            .baseUrl("https://c4804667-8963-4bac-9b82-cee4ef549e3a.mock.pstmn.io")
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
}