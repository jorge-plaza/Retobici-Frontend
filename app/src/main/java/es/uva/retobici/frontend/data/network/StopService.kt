package es.uva.retobici.frontend.data.network

import es.uva.retobici.frontend.core.RetrofitHelper
import es.uva.retobici.frontend.data.models.Stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StopService {

    private val retrofit = RetrofitHelper().getRetrofit()

    suspend fun getStops():List<Stop>{
        return withContext(Dispatchers.IO){
            val response = retrofit.create(StopAPI::class.java).getAllStops()
            // ?: if response is null empty list
            response.body() ?: emptyList()
        }
    }
}