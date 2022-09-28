package com.example.melon_exoplayer.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {

    @GET("/v3/2c0c97aa-d084-410c-b5b6-e94f5779fa25")
    fun listMusics(
    ) : Call<MusicDto>
}