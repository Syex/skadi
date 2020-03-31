package de.syex.skadi.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val movieAdapter by lazy { MovieAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, MainViewModelProvider()).get(MainViewModel::class.java)
        viewModel.stateLiveData.observe(this, Observer { renderState(it) })

        recyclerView.apply {
            adapter = movieAdapter
            addItemDecoration(
                DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL)
            )
        }
    }

    private fun renderState(state: MainViewState) {
        when (state) {
            is MainViewState.DisplayMovies -> {
                progressBar.hide()
                movieAdapter.movies = state.movies
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelProvider : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(
            LoadMoviesUseCase()
        ) as T
    }

}