package com.example.dynatracetestapp;


import com.dynatrace.android.agent.DTXAction;
import com.dynatrace.android.agent.Dynatrace;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiWeatherRestClient extends BaseRestClient {

    private static final String BASE_URL = "https://api.forecast.io/forecastXXX/";
    private static final String API_KEY = "94ee4ee31c23d1f268eab3cc72ab623a";   //register on https://darksky.net/
    private static final String API_URL = BASE_URL + API_KEY + "/";
    private static final int TIMEOUT = 10;
    private static final int SLEEP_TIMEOUT = 5;
    private static final boolean USE_SLEEP_INTERCEPTOR = false;
    private static final ApiWeatherRestClient INSTANCE = new ApiWeatherRestClient();

    private Retrofit mRetrofit;
    private ApiWeather mApiWeather;
    private RestCall mApiWeatherRestCall;

    /**
     * Singleton instance of {@link ApiWeatherRestClient}.
     *
     * @return instance of {@link ApiWeatherRestClient}.
     */
    public static ApiWeatherRestClient getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton construct.
     */
    private ApiWeatherRestClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            okHttpClientBuilder
                    .addInterceptor(httpLoggingInterceptor);
            okHttpClientBuilder.addInterceptor(
                    new Interceptor() {
                        @NotNull
                        @Override
                        public Response intercept(@NotNull Chain chain) throws IOException {
                            DTXAction action = Dynatrace.enterAction("API_Service4");

                            try {
                                Request original = chain.request();
                                Request request = original.newBuilder()
                                        .addHeader(Dynatrace.getRequestTagHeader(), action.getRequestTag())
                                        .method(original.method(), original.body())
                                        .build();
                                return chain.proceed(request);
                            }
                            finally {
                                action.leaveAction();
                            }
                        }
                    }
            );
        }

        //simulate long running request
        if (USE_SLEEP_INTERCEPTOR) {
            NetworkSleepInterceptor networkSleepInterceptor = new NetworkSleepInterceptor(
                    SLEEP_TIMEOUT, TimeUnit.SECONDS);
            okHttpClientBuilder
                    .addInterceptor(networkSleepInterceptor);
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

        mApiWeather = mRetrofit.create(ApiWeather.class);
    }

    /**
     * Invoke getWeather via {@link Call} request.
     *
     * @param latitude of location.
     * @param longitude of location.
     */
    public void getWeather(double latitude, double longitude) {
        Call apiWeatherCall = mApiWeather.getWeather(
                String.valueOf(latitude),
                String.valueOf(longitude));

        RestRequest restRequest = new RestRequest.Builder()
                .call(apiWeatherCall)
                .baseResponseEvent(new GetWeatherResponseEvent())
                .useStickyIntent(true)
                .build();

        mApiWeatherRestCall = call(restRequest);
    }

    /**
     * Cancel of getWeather {@link Call} request.
     */
    public void cancelGetWeather() {
        cancelCall(mApiWeatherRestCall);
    }
}