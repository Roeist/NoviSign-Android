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

  /*  @Test
    fun testGetPlaylistsSuccess() = runTest {
        // Mock the PlaylistResponse with sample data
       val mockPlaylistResponse = PlaylistResponse(
            screenKey = "test_screen_key",
            breakpointInterval = 10,
            playList = listOf(
                Playlist(10, listOf("file1", "file2")),
                Playlist(20, listOf("file3"))
            )
        )
 *//*
        `when`(apiService.getPlaylists("test_screen_key")).thenReturn(mockPlaylistResponse)

        val result = mediaRepository.getPlayLists("test_screen_key")

        assertEquals(mockPlaylistResponse, result)*//*

       *//* val mockResponse = mockResponse(200, mockPlaylistResponse) // Assuming a helper function for creating mock Retrofit responses
        `when`(apiService.getPlaylists("test_screen_key")).thenReturn(mockResponse)

        val result = mediaRepository.getPlayLists("test_screen_key")

        assertEquals(mockPlaylistResponse, result)*//*


       *//* val mockPlaylistResponse = PlaylistResponse(
            screenKey = "test_screen_key",
            breakpointInterval = 10,
            playList = listOf(
                Playlist(10, listOf("file1", "file2")),
                Playlist(20, listOf("file3"))
            )
        )*//*

        val mockResponse: Response<PlaylistResponse> = mock(Response::class.java) as Response<PlaylistResponse>
        val mockCall: Call<PlaylistResponse> = mock(Call::class.java) as Call<PlaylistResponse>

        `when`(mockResponse.body()).thenReturn(mockPlaylistResponse)
        `when`(mockCall.execute()).thenReturn(mockResponse)
        `when`(apiService.getPlaylists("test_screen_key")).thenReturn(mockCall)

        val result = mediaRepository.getPlayLists("test_screen_key")

        assertEquals(mockPlaylistResponse, result)

       *//* val mockCall: Call<PlaylistResponse> = mock(Call::class.java) as Call<PlaylistResponse>
        val mockResponse = mock(Response::class.java)

        // Configure the mock response
        `when`(mockResponse.body()).thenReturn(mockPlaylistResponse)
        `when`(mockCall.execute()).thenReturn(mockResponse)

        // Mock the API service to return the mock Call
        `when`(apiService.getPlaylists("test_screen_key")).thenReturn(mockCall)

        val result = mediaRepository.getPlayLists("test_screen_key")

        assertEquals(mockPlaylistResponse, result)*//*
    }*/

    @Test
    fun testGetPlaylistsFailure() = runTest {
        `when`(apiService.getPlaylists("test_screen_key")).thenThrow(RuntimeException("Simulated IOException"))

//        val result = runCatching { mediaRepository.getPlayLists("test_screen_key") }
        val result = runCatching { mediaRepository.getPlaylists("test_screen_key") }

        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
    

}