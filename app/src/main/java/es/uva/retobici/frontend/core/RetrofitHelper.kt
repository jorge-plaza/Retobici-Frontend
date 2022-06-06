package es.uva.retobici.frontend.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://c4804667-8963-4bac-9b82-cee4ef549e3a.mock.pstmn.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}