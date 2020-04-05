package de.syex.skadi.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.syex.skadi.*

class MainViewModel(
    private val loadMovies: LoadMoviesUseCase
) : ViewModel() {

    val stateLiveData by lazy { skadiStore.stateFlow.asLiveData() }

    val skadiStore = SkadiStore<MainViewState, MainViewAction, MainViewSignal>(
        initialState = MainViewState.Loading,
        reducer = { state, change ->
            when (state) {
                MainViewState.Loading -> if (change is MainViewAction.LoadMovies.Success) {
                    state { MainViewState.DisplayMovies(change.movies) }
                } else {
                    throw IllegalStateException()
                }
                is MainViewState.DisplayMovies -> if (change is MainViewEvent.MovieClicked) {
                    state.signal(MainViewSignal.ShowToast(change.movie.movieName))
                } else {
                    throw IllegalStateException()
                }
            }
        },
        actions = { action ->
            when (action) {
                MainViewAction.LoadMovies -> {
                    val movies = loadMovies.execute()
                    MainViewAction.LoadMovies.Success(movies)
                }
            }
        },
        coroutineScope = viewModelScope
    )

    init {
        skadiStore.performAction(MainViewAction.LoadMovies)
    }

    fun performViewEvent(event: MainViewEvent) {
        skadiStore.perform(event)
    }

}

sealed class MainViewAction {

    object LoadMovies : MainViewAction() {
        data class Success(val movies: List<MovieModel>) : SkadiChange
    }
}

sealed class MainViewEvent : SkadiChange {

    data class MovieClicked(val movie: MovieModel) : MainViewEvent()

}

sealed class MainViewState : SkadiState {

    object Loading : MainViewState()

    data class DisplayMovies(val movies: List<MovieModel>) : MainViewState()
}

sealed class MainViewSignal {

    data class ShowToast(val text: String) : MainViewSignal()
}