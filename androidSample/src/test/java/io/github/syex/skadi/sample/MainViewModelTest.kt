package io.github.syex.skadi.sample

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime


@ExperimentalTime
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
        viewModel.skadiStore.stateFlow.test {
            assertThat(expectItem()).isEqualTo(MainViewState.Loading)
            viewModel.onViewInit()

            coVerify { loadMovies.execute() }
            assertThat(expectItem()).isEqualTo(MainViewState.DisplayMovies(movies))
        }
    }
}
