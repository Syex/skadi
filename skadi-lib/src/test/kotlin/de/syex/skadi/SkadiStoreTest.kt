package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class SkadiStoreTest {

    private val testCoroutineScope = TestCoroutineScope()

    private val store = SkadiStore<TestState, TestSkadiAction, Nothing>(
        initialState = TestState.Default,
        reducer = { state, change ->
            effect {
                state.same<TestState, TestSkadiAction, Nothing>()
            }
        },
        coroutineScope = testCoroutineScope
    )

    @Test
    fun `performing a side effect although no actions are defined throws an exception`() {
        // TestCoroutineScope swallows the thrown exception, see https://github.com/Kotlin/kotlinx.coroutines/issues/1205
        assertThat(testCoroutineScope.uncaughtExceptions).isEmpty()

        store.performAction(TestSkadiAction())

        assertThat(testCoroutineScope.uncaughtExceptions).hasSize(1)
    }

    @Test
    fun `when coroutineScope is canceled, state flow is canceled`() = runBlockingTest {
        store.stateFlow.test(testCoroutineScope) {
            assertNotComplete()
            testCoroutineScope.cancel()
            assertComplete()
        }
    }

    @Test
    fun `when coroutineScope is canceled, signal flow is canceled`() = runBlockingTest {
        store.signalFlow.test(testCoroutineScope) {
            assertNotComplete()
            testCoroutineScope.cancel()
            assertComplete()
        }
    }

    private sealed class TestState : SkadiState {

        object Default : TestState()
    }
}