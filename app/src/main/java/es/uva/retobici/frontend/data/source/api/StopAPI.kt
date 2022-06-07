package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.StopDTO
import es.uva.retobici.frontend.domain.model.Stop
import retrofit2.Response
import retrofit2.http.GET

interface StopAPI {
    @GET("/retobici/stops")
    suspend fun getAllStops():Response<List<StopDTO>>
}