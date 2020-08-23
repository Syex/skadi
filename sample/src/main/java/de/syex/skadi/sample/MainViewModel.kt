package de.syex.skadi.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syex.skadi.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

class MainViewModel(
    private val loadMovies: LoadMoviesUseCase,
    private val coroutineScope: CoroutineScope? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    val skadiStore = SkadiStore<MainViewState, MainViewAction, MainViewSignal>(
        initialState = MainViewState.Loading,
        reducer = { state, change ->
            when (state) {
                MainViewState.Loading -> when (change) {
                    is MainViewAction.LoadMovies.Success -> {
                        effect {
                            state { MainViewState.DisplayMovies(change.movies) }
                        }
                    }
                    else -> unexpected(state, change)
                }
                is MainViewState.DisplayMovies -> when (change) {
                    is MainViewEvent.MovieClicked -> {
                        // type redundant if using new type inference algorithm
                        state.signal(
                            MainViewSignal.ShowToast(
                                change.movie.movieName
                            )
                        )
                    }
                    else -> unexpected(state, change)
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
        coroutineScope = (coroutineScope ?: viewModelScope) + dispatcher
    )

    fun onViewInit() {
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