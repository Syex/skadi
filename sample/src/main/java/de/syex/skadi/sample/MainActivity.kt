package de.syex.skadi.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val movieAdapter by lazy {
        MovieAdapter(
            onMovieClickListener = { viewModel.performViewEvent(MainViewEvent.MovieClicked(it)) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.apply {
            adapter = movieAdapter
            addItemDecoration(
                DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL)
            )
        }

        viewModel = ViewModelProvider(this, MainViewModelProvider()).get(MainViewModel::class.java)
        if (savedInstanceState == null) viewModel.onViewInit()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launchWhenResumed {
            viewModel.skadiStore.stateFlow.collect {
                ensureActive()
                renderState(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.skadiStore.signalFlow.collect {
                ensureActive()
                handleSignal(it)
            }
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

    private fun handleSignal(signal: MainViewSignal) {
        when (signal) {
            is MainViewSignal.ShowToast -> {
                Toast.makeText(this, signal.text, Toast.LENGTH_SHORT).show()
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