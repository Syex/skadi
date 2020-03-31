package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class StoreTest {

    private val testCoroutineScope = TestCoroutineScope()

    private val store = SkadiStore(
        initialState = TestState.Init,
        reducer = { state: TestState, change: SkadiChange ->
            when {
                state == TestState.Init && change == TestViewAction.RequestData -> effect {
                    state { TestState.Loading }
                    action { TestAction.LoadData }
                }
                state == TestState.Loading && change is TestAction.LoadData.Success -> state {
                    TestState.DisplayData(
                        change.data
                    )
                }
                state is TestState.DisplayData -> state.same()
                else -> throw IllegalStateException()
            }
        },
        actions = { action: TestAction ->
            when (action) {
                is TestAction.LoadData -> {
                    val result = testUseCase.execute()
                    TestAction.LoadData.Success(result)
                }
            }
        },
        coroutineScope = testCoroutineScope
    )

    private val testUseCase = TestUseCase()

    private val stateFlow by lazy { store.stateFlow }

    @Test
    fun `performing ViewAction RequestData goes to Loading state and performs LoadData action`() = runBlockingTest {
        store.perform(TestViewAction.RequestData)

        assertThat(testUseCase.executed).isTrue()
        val state = stateFlow.first()
        assertThat(state).isEqualTo(TestState.Loading)
    }

    @Test
    fun `when LoadData executes successfully, then moves to DisplayData`() = runBlockingTest {
        store.perform(TestViewAction.RequestData)

        val states = mutableListOf<TestState>()
        stateFlow.take(2).toList(states)
        assertThat(states.last()).isInstanceOf(TestState.DisplayData::class.java)
    }

    sealed class TestState : SkadiState {

        object Init : TestState()

        object Loading : TestState()

        data class DisplayData(val data: String) : TestState()
    }

    sealed class TestAction {

        object LoadData : TestAction() {
            class Success(val data: String) : SkadiChange
        }
    }

    sealed class TestViewAction : SkadiChange {

        object RequestData : TestViewAction()
    }

    private class TestUseCase {

        var executed = false

        fun execute(): String {
            executed = true
            return "success"
        }
    }
}