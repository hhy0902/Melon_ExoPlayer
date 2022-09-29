package com.example.melon_exoplayer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melon_exoplayer.service.MusicModel

class PlayListAdapter(val callback : (MusicModel) -> Unit) : ListAdapter<MusicModel, PlayListAdapter.ViewHolder> (diffUtil){

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : MusicModel) {

            val trackTextView = itemView.findViewById<TextView>(R.id.itemTrackTextView)
            val artistTextView = itemView.findViewById<TextView>(R.id.itemArtistTextView)
            val coverImageView = itemView.findViewById<ImageView>(R.id.itemCoverImageView)

            trackTextView.text = item.track
            artistTextView.text = item.artist

            Glide.with(coverImageView.context)
                .load(item.coverUrl)
                .into(coverImageView)

            if (item.isPlaying) {
                itemView.setBackgroundColor(Color.GRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                callback(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList.get(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MusicModel>() {
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem == newItem
            }

        }
    }


}













































