package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class SkadiStoreTest {

    private val testCoroutineScope = TestCoroutineScope()

    private val store by lazy {
        SkadiStore<TestState, TestSkadiAction, Nothing>(
            initialState = TestState.Default,
            reducer = { state, change ->
                effect {
                    state.same<TestState, TestSkadiAction, Nothing>()
                }
            },
            coroutineScope = testCoroutineScope
        )
    }

    @Test
    fun `performing a side effect although no actions are defined throws an exception`() {
        // TestCoroutineScope swallows the thrown exception, see https://github.com/Kotlin/kotlinx.coroutines/issues/1205
        assertThat(testCoroutineScope.uncaughtExceptions).isEmpty()

        store.performAction(TestSkadiAction())

        assertThat(testCoroutineScope.uncaughtExceptions).hasSize(1)
        assertThat(testCoroutineScope.uncaughtExceptions.first()).isInstanceOf(IllegalStateException::class.java)
    }

    private sealed class TestState : SkadiState {

        object Default : TestState()
    }
}