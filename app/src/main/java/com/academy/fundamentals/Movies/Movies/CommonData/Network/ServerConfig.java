package com.academy.fundamentals.Movies.Movies.CommonData.Network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.BASE_URL;

public class ServerConfig {
    HttpLoggingInterceptor m_loggingInterceptor;
    OkHttpClient m_client;

    public ServerConfig() {}

    public Retrofit getRestClient(){
        if (m_loggingInterceptor == null) {
            m_loggingInterceptor = new HttpLoggingInterceptor();
        }
        m_loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (m_client == null)
        {
            m_client = new OkHttpClient.Builder()
                    .addInterceptor(m_loggingInterceptor)
                    .build();
        }

      return new Retrofit.Builder().
                baseUrl(BASE_URL)
                .client(m_client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
