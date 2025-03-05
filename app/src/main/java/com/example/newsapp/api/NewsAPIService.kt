import com.example.newsapp.api.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("v2/top-headlines")
    fun getTopHeadlines(): Call<NewsResponse>
}
