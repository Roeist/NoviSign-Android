package com.steinberg.novisign.repository

import com.steinberg.novisign.data.Playlist
import com.steinberg.novisign.data.PlaylistResponse
import com.steinberg.novisign.domain.ApiService
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

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

/*    @Test
    fun getPlayListsSuccess() = runTest{


        val mockPlaylistResponse = PlaylistResponse(
            screenKey = "test_key",
            breakpointInterval = 0,
            playList = listOf(Playlist(0, listOf("file_key"))),
        )

      *//*  `when`(apiService.getPlaylists("test_key")).thenReturn(mockPlaylistResponse)

        val result = repository.getPlayLists("test_key")

        assertEquals(mockPlaylistResponse, result)*//*

        val mockResponse: retrofit2.Response<PlaylistResponse> = mock(retrofit2.Response::class.java) as retrofit2.Response<PlaylistResponse>
        val mockCall: Call<PlaylistResponse> = mock(Call::class.java) as Call<PlaylistResponse>

        `when`(mockResponse.body()).thenReturn(mockPlaylistResponse)
        `when`(mockCall.execute()).thenReturn(mockResponse)
        `when`(apiService.getPlaylists("test_screen_key")).thenReturn(mockCall)

        val result = repository.getPlayLists("test_screen_key")

        assertEquals(mockPlaylistResponse, result)
    }*/

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


  /*  @Test
    fun testGetMediaFileSuccess() = runTest {
      *//*  val mockResponseBody = mock(ResponseBody::class.java)
        `when`(mockResponseBody.bytes()).thenReturn(byteArrayOf(1, 2, 3))

        val mockResponse = mock(Response::class.java)
        `when`(mockResponse.body()).thenReturn(mockResponseBody)
        `when`(mockResponse.isSuccessful).thenReturn(true)

        `when`(apiService.getMediaFile("file_key")).thenReturn(mockResponse as retrofit2.Response<ResponseBody>?)

        val result = repository.getMediaFile("file_key")

        assertTrue(result.isSuccessful)
        assertEquals(mockResponseBody.bytes(), result.body()?.bytes())*//*

        val mockResponse = mock(retrofit2.Response::class.java) as retrofit2.Response<ResponseBody>


        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.google.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val mockResponseBody = mock(ResponseBody::class.java)
        `when`(mockResponseBody.bytes()).thenReturn(byteArrayOf(1, 2, 3))

        `when`(apiService.getMediaFile("file_key")).thenReturn(mockResponse(200, mockResponseBody))

    }

    // Helper function to create a mock Response object
    fun <T> mockResponse(code: Int, body: T): retrofit2.Response<T> {
        val mockResponse = mock(retrofit2.Response::class.java) as retrofit2.Response<T>
        `when`(mockResponse.isSuccessful).thenReturn(code == 200)
        `when`(mockResponse.code()).thenReturn(code)
        `when`(mockResponse.body()).thenReturn(body)
        return mockResponse
    }*/

    @Test
    fun testGetMediaFileFailure() = runTest {
        `when`(apiService.getMediaFile("file_key")).thenThrow(RuntimeException("Simulated testGetMediaFileFailure IOException"))

        val result = runCatching { repository.getMediaFile("file_key") }

        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}