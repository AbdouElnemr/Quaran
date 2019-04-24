package com.elnemr.quaran.network;

 import com.elnemr.quaran.model.QuranModel;

 import org.json.JSONArray;

 import java.util.HashMap;
 import java.util.List;

 import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
 import retrofit2.http.Field;
 import retrofit2.http.FormUrlEncoded;
 import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
//
//    @POST("GetQuran.php")
//    Single<QuranModel.Api> getQuran(@Body HashMap<String, Integer> stringIntegerHashMap);


    @FormUrlEncoded
    @POST("GetQuran.php")
    Single<List<QuranModel.Api>> getQuran(
            @Field("reader_id") int readerId
    );
}