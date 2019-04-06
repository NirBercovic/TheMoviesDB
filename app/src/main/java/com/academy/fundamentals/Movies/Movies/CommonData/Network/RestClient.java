package com.academy.fundamentals.Movies.Movies.CommonData.Network;

import retrofit2.Retrofit;

public interface RestClient{

    Retrofit m_retrofit = new ServerConfig().getRestClient();
    MoviesService moviesService = m_retrofit.create(MoviesService.class);

}
