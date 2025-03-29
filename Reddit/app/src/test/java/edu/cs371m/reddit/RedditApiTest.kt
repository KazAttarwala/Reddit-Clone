package edu.cs371m.reddit

import edu.cs371m.reddit.api.RedditApi
import edu.cs371m.reddit.api.RedditApi.ListingResponse
import edu.cs371m.reddit.api.RedditApi.ListingData
import edu.cs371m.reddit.api.RedditApi.RedditChildrenResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.mock.Calls

// Replace this with your actual RedditPost model and initialization.
data class RedditPost(val id: String = "dummy", val title: String = "Dummy Post")

class RedditApiTest {

    @Test
    fun testMockGetPosts() = runBlocking {
        // Create a mock instance of RedditApi
        val redditApi: RedditApi = mock()

        // Prepare dummy response data
        val dummyPost = RedditPost()
        val dummyListingResponse = ListingResponse(
            ListingData(
                children = listOf(RedditChildrenResponse(dummyPost)),
                after = null,
                before = null
            )
        )

        // Stub the getPosts() method to return a successful response with dummyListingResponse
        whenever(redditApi.getPosts("aww", null, 25)).thenReturn(Calls.response(dummyListingResponse))

        // Call the stubbed method and execute synchronously
        val responseCall = redditApi.getPosts("aww", null, 25)
        val responseBody = responseCall.execute().body()

        // Assert the response is not null and contains dummy data
        assertNotNull(responseBody)
        assertTrue("Expected at least one post", responseBody?.data?.children?.isNotEmpty() == true)
    }
}