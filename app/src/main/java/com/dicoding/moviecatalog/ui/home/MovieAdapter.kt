package com.dicoding.moviecatalog.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.moviecatalog.R
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.databinding.ItemMovieBinding

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var listData = ArrayList<Movie>()
    var onItemClick: ((Movie) -> Unit)? = null

    fun setData(newListData: List<Movie>?) {
        if (newListData == null) return
        val diffCallback = MovieDiffCallback(listData, newListData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listData.clear()
        listData.addAll(newListData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Movie) {
            with(binding) {
                tvItemTitle.text = data.title
                tvItemRating.text = String.format("%.1f", data.voteAverage)
                tvItemDate.text = data.releaseDate
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w500${data.posterPath}")
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .into(ivItemPoster)
            }
            itemView.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }
    }
}

class MovieDiffCallback(
    private val oldList: List<Movie>,
    private val newList: List<Movie>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}