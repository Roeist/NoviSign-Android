package com.steinberg.novisign.data

import com.steinberg.novisign.domain.ApiService
import com.steinberg.novisign.repository.MediaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PlaylistResponseTest {
    @Mock
    private lateinit var apiService: ApiService

    private lateinit var mediaRepository: MediaRepository


    @Before
    fun setUp() {
        //Mock the api service and inject it into mediaRepository
        MockitoAnnotations.openMocks(this)
        mediaRepository = MediaRepository(apiService)
    }

    @Test
    fun testGetPlaylistsFailure() = runTest {
        `when`(apiService.getPlaylists("test_screen_key")).thenThrow(RuntimeException("Simulated IOException"))

        val result = runCatching { mediaRepository.getPlaylists("test_screen_key") }

        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
    

}