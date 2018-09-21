package com.vidyo.io.demo.network;


import com.vidyo.io.demo.model.GetAccessTokenResponseBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Summary: Interface for api calling
 * Description: Interface uses the Calling api abstract methods with type request
 * @author RSI
 * @date 20.08.2018
 */
public interface AppService {

    /**
     * Call for get access token api
     */
    @GET("getAccessToken")
    Observable<GetAccessTokenResponseBean> getAccessToken(@Query("username") String username);

}
