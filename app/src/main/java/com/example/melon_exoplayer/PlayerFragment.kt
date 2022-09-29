package com.example.melon_exoplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.melon_exoplayer.databinding.FragmentPlayerBinding
import com.example.melon_exoplayer.service.MusicDto
import com.example.melon_exoplayer.service.MusicModel
import com.example.melon_exoplayer.service.MusicService
import com.example.melon_exoplayer.service.mapper
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var model : PlayerModel = PlayerModel()
    private var binding : FragmentPlayerBinding? = null
    private lateinit var playListAdapter: PlayListAdapter
    private var player : SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayView(fragmentPlayerBinding)
        initPlayListButton(fragmentPlayerBinding)
        initPlayControlButtons(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        getVideoListFromServer()

    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playControlImageView.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }

        fragmentPlayerBinding.skipNextImageView.setOnClickListener {
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        fragmentPlayerBinding.skipPrevImageView.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun initPlayView(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.playerView.player = player

        binding?.let { binding ->
            player?.addListener(object : Player.EventListener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition = newIndex.toInt()
                    playListAdapter.submitList(model.getAdapterModles())
                }
            })

        }
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        playListAdapter = PlayListAdapter {
            // 음악을 재생
            playMusic(it)
        }

        fragmentPlayerBinding.playListRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playListImageView.setOnClickListener {
            // 예외처리 서버에서 데이터가 다 안넘어왔을때
            if(model.currentPosition == -1) {
                return@setOnClickListener
            }

            fragmentPlayerBinding.playListViewGroup.isVisible = model.isWatchingPlayListView
            fragmentPlayerBinding.playerViewGroup.isVisible = model.isWatchingPlayListView.not()

            model.isWatchingPlayListView = !model.isWatchingPlayListView
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

                    Log.d("asdf playerFragment", "${response.body()}")

                    response.body()?.let { musicDto ->

                        model = musicDto.mapper()

                        setMusicList(model.getAdapterModles())
                        playListAdapter.submitList(model.getAdapterModles())
                    }

                }

                override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                }

            })
        }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        context?.let {
            player?.addMediaItems(modelList.map { musicModle ->
                MediaItem.Builder()
                    .setMediaId(musicModle.id.toString())
                    .setUri(musicModle.streamUrl)
                    .build()
            })
            player?.prepare()

        }
    }

    private fun playMusic(musicModel : MusicModel) {
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()
    }

    companion object {
        fun newInstance() : PlayerFragment{
            return PlayerFragment()
        }
    }
}









































