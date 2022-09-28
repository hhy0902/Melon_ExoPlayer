package com.example.melon_exoplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.melon_exoplayer.databinding.FragmentPlayerBinding
import com.example.melon_exoplayer.service.MusicDto
import com.example.melon_exoplayer.service.MusicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var binding : FragmentPlayerBinding? = null
    private var isWatchingPlayListView = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayListButton(fragmentPlayerBinding)
        getVideoListFromServer()

    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playListImageView.setOnClickListener {
            // 예외처리 서버에서 데이터가 다 안넘어왔을때
            fragmentPlayerBinding.playListViewGroup.isVisible = isWatchingPlayListView
            fragmentPlayerBinding.playerViewGroup.isVisible = isWatchingPlayListView.not()

            isWatchingPlayListView = !isWatchingPlayListView
        }
    }

    private fun getVideoListFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java).also {
            it.listMusics().enqueue(object : Callback<MusicDto> {
                override fun onResponse(call: Call<MusicDto>, response: Response<MusicDto>) {

                    if(response.isSuccessful) {
                        Log.d("asdf playerFragment","${response.body()}")

                    }
                }

                override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                }

            })
        }
    }

    companion object {
        fun newInstance() : PlayerFragment{
            return PlayerFragment()
        }
    }
}









































