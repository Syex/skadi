package io.github.syex.skadi.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie.*

class MovieAdapter(
    private val onMovieClickListener: ((MovieModel) -> Unit)
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    var movies: List<MovieModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        )
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val movie = movies[position]
        movieTitle.text = movie.movieName
        moviePosterView.load(movie.moviePosterUrl)
        itemView.setOnClickListener { onMovieClickListener(movie) }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer

}