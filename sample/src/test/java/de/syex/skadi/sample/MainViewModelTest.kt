package de.syex.skadi.sample

import com.google.common.truth.Truth.assertThat
import dev.olog.flow.test.observer.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test


@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val movies = listOf(MovieModel("TestMovie", "http://test.url"))
    private val loadMovies = mockk<LoadMoviesUseCase> {
        coEvery { execute() } returns movies
    }

    private val testCoroutineScope = TestCoroutineScope()

    private val viewModel = MainViewModel(loadMovies, testCoroutineScope, TestCoroutineDispatcher())

    @Test
    fun `on viewInit() executes loadMovies() and goes to DisplayMovies state`() = runBlockingTest {
        val flow = viewModel.skadiStore.stateFlow
        flow.test(this) {
            assertThat(flow.value).isEqualTo(MainViewState.Loading)
            viewModel.onViewInit()

            coVerify { loadMovies.execute() }
            assertValue(MainViewState.DisplayMovies(movies))
        }
    }
}
