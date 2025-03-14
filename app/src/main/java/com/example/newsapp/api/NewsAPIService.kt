import com.example.newsapp.api.NewsResponse
import com.example.newsapp.constants.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("1/latest")
    suspend fun getTopHeadlines(
        @Query("apikey") apikey: String= Constants.API_KEY,
        @Query("id") id: String?=null,
        @Query("q") q: String?=null,
        @Query("qInTitle") qInTitle: String?=null,
        @Query("qInMeta") qInMeta: String?=null,
        @Query("timeframe") timeframe: String?=null,
        @Query("country") country: String?=null,
        @Query("category") category: String?=null,
        @Query("excludecategory") excludecategory: String?=null,
        @Query("language") language: String?=null,
        @Query("tag") tag: String?=null,
        @Query("sentiment") sentiment: String?=null,
        @Query("region") region: String?=null,
        @Query("domain") domain: String?=null,
        @Query("domainurl") domainurl: String?=null,
        @Query("excludeDomain") excludeDomain: String?=null,
        @Query("excludefield") excludefield: String?=null,
        @Query("prioritydomain") prioritydomain: String?=null,
        @Query("timezone") timezone: String?=null,
        @Query("full_content") full_content: Int?=null,
        @Query("image") image: Int?=null,
        @Query("video") video: Int?=null,
        @Query("removeduplicate") removeduplicate: Int?=null,
        @Query("size") size: Int?=null,
        @Query("page") page: Int?=null,
    ): Response<NewsResponse>
}