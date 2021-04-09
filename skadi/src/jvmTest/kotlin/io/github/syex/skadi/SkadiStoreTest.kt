package io.github.syex.skadi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertTrue(testCoroutineScope.uncaughtExceptions.isEmpty())

        store.performAction(TestSkadiAction())

        assertEquals(testCoroutineScope.uncaughtExceptions.size, 1)
        assertTrue {
            testCoroutineScope.uncaughtExceptions.first() is IllegalStateException
        }
    }

    private sealed class TestState : SkadiState {

        object Default : TestState()
    }
}