package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.StopRemoteDataSource
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.repository.StopRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class GetStopsUseCaseTest {

    @RelaxedMockK
    private lateinit var stopRepository: StopRemoteDataSource
    lateinit var getStopsUseCase: GetStopsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getStopsUseCase = GetStopsUseCase(stopRepository)
    }

    @Test
    fun `get stop list from the api`() = runBlocking {
        //Given
        val listStops = listOf(Stop(1,-4.731,41.653,"Plaza de Poniente", 10, listOf(), 0,0))
        coEvery { stopRepository.getAllStops() } returns listStops
        //When
        val response = getStopsUseCase()

        //Then
        coVerify(exactly = 1) { stopRepository.getAllStops() }
        assertEquals(response, listStops)
    }
}