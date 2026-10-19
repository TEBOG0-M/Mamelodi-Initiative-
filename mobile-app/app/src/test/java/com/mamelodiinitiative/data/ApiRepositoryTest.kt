package com.mamelodiinitiative.data

import com.mamelodiinitiative.data.api.MamelodiApiService
import com.mamelodiinitiative.data.models.NewsItem
import com.mamelodiinitiative.data.models.Programme
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field

class ApiRepositoryTest {

    private lateinit var apiService: MamelodiApiService
    private lateinit var repository: ApiRepository

    @Before
    fun setup() {
        apiService = mock()
        repository = ApiRepository()
        
        // Use reflection to inject mock service into private field
        val field: Field = ApiRepository::class.java.getDeclaredField("apiService")
        field.isAccessible = true
        field.set(repository, apiService)
    }

    @Test
    fun `getProgrammes returns success when api call succeeds`() = runTest {
        val mockData = listOf(Programme(1, "Title", "Description"))
        whenever(apiService.getProgrammes()).thenReturn(mockData)

        val result = repository.getProgrammes()

        assertTrue(result.isSuccess)
        assertEquals(mockData, result.getOrNull())
    }

    @Test
    fun `getProgrammes returns failure when api call throws exception`() = runTest {
        val exception = Exception("Network error")
        whenever(apiService.getProgrammes()).thenThrow(exception)

        val result = repository.getProgrammes()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getNews returns success when api call succeeds`() = runTest {
        val mockData = listOf(NewsItem(1, "Title", "Description", "Date"))
        whenever(apiService.getNews()).thenReturn(mockData)

        val result = repository.getNews()

        assertTrue(result.isSuccess)
        assertEquals(mockData, result.getOrNull())
    }
}
