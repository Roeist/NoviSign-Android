package com.steinberg.novisign.repository

import com.steinberg.novisign.domain.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class MediaRepositoryTest {
    @Mock
    lateinit var repository: MediaRepository

    @Mock
    lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = mock(ApiService::class.java)
        repository = MediaRepository(apiService)
    }
    @Test
    fun testGetPlayListsFailure() = runTest {
        `when`(apiService.getPlaylists("test_key")).thenThrow(RuntimeException("Simulated error"))

        val result = runCatching { repository.getPlaylists("test_key") }

        assertTrue(result.exceptionOrNull() is RuntimeException)
        val exception = result.exceptionOrNull() as? RuntimeException
        if (exception != null) {
            assertTrue(exception.message?.contains("Simulated error") == true)
        }
    }


    @Test
    fun testGetMediaFileFailure() = runTest {
        `when`(apiService.getMediaFile("file_key")).thenThrow(RuntimeException("Simulated testGetMediaFileFailure IOException"))

        val result = runCatching { repository.getMediaFile("file_key") }

        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}