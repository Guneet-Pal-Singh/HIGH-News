import com.example.newsapp.api.NewsResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") countryCode: String?=null,
        @Query("category") category: String?=null,
        @Query("sources") sources:String?=null,
        @Query("query") q:String?=null,
        @Query("pageSize") pageSize:Int?=null,
        @Query("page") page:Int?=null
    ): Response<NewsResponse>
}