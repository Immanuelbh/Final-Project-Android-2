package maim.com.finalproject.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAYSJQnMw:APA91bFeRzvpphnePTznPfcxHv92na7S-MrEETqhqb9pgqA4-uttGKyCvsu-_e3_ia5Jn0L-iVUbAstQcpH23A6oCB5KfA1GHD716kwfX56MifO48zNodr0SVXbG1mZfUzEzwm1ysi3R"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
