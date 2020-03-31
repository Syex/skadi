package de.syex.skadi.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import de.syex.skadi.SkadiChange
import de.syex.skadi.SkadiState
import de.syex.skadi.SkadiStore
import de.syex.skadi.state
import kotlinx.coroutines.flow.collect

class MainViewModel(
    private val loadMovies: LoadMoviesUseCase
) : ViewModel() {

    val stateLiveData by lazy {
        liveData {
            skadiStore.stateFlow.collect { emit(it) }
        }
    }

    private val skadiStore = SkadiStore<MainViewState, MainViewAction>(
        initialState = MainViewState.Loading,
        reducer = { state, change ->
            when (state) {
                MainViewState.Loading -> if (change is MainViewAction.LoadMovies.Success) {
                    state { MainViewState.DisplayMovies(change.movies) }
                } else {
                    throw IllegalStateException()
                }
                else -> throw IllegalStateException()
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

}

sealed class MainViewAction {

    object LoadMovies : MainViewAction() {
        data class Success(val movies: List<MovieModel>) : SkadiChange
    }
}

sealed class MainViewEvent : SkadiChange {

    data class MovieClicked(val movieName: String) : MainViewEvent()

}

sealed class MainViewState : SkadiState {

    object Loading : MainViewState()

    data class DisplayMovies(val movies: List<MovieModel>) : MainViewState()
}