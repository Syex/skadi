package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class StoreIntegrationTest {

    private val testCoroutineScope = TestCoroutineScope()

    private val store = SkadiStore(
        initialState = TestState.Init,
        reducer = { state: TestState, change: SkadiChange ->
            when (state) {
                TestState.Init -> when (change) {
                    TestViewAction.RequestData -> effect {
                        state { TestState.Loading }
                        action { TestAction.LoadData }
                    }
                    else -> unexpected(state, change)
                }
                TestState.Loading -> when (change) {
                    is TestAction.LoadData.Success -> effect {
                        state { TestState.DisplayData(change.data) }
                    }
                    else -> state.same()
                }
                is TestState.DisplayData -> {
                    when (change) {
                        TestViewAction.ButtonClicked -> effect {
                            state { state }
                            signal { TestSignal.ShowMessage }
                        }
                        else -> state.same<TestState, TestAction, TestSignal>()
                    }
                }
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

    @Test
    fun `performing ViewAction RequestData goes to Loading state, performs LoadData action, then moves to DisplayData`() =
        testCoroutineScope.runBlockingTest {
            store.stateFlow.test(testCoroutineScope) {

                store.perform(TestViewAction.RequestData)

                assertThat(testUseCase.executed).isTrue()

                assertValue { it is TestState.DisplayData }
            }
        }

    @Test
    fun `performing change ButtonClicked leads to signal ShowMessage`() =
        testCoroutineScope.runBlockingTest {
            store.perform(TestViewAction.RequestData)
            store.perform(TestViewAction.ButtonClicked)

            store.signalFlow.test(testCoroutineScope) {
                assertValue(TestSignal.ShowMessage)
            }
        }

    @Test
    fun `never emits the same state twice`() = testCoroutineScope.runBlockingTest {
        store.stateFlow.test(testCoroutineScope) {
            store.perform(TestViewAction.RequestData)
            assertThat(values().last()).isInstanceOf(TestState.DisplayData::class.java)
            assertThat(valuesCount()).isEqualTo(1)

            // in DisplayData state we map every change to the same state, so this change results in
            // same state
            store.perform(TestViewAction.RequestData)

            // verify there're still only 2 states
            assertThat(valuesCount()).isEqualTo(1)
        }
    }

    private sealed class TestState : SkadiState {

        object Init : TestState()

        object Loading : TestState()

        data class DisplayData(val data: String) : TestState()
    }

    private sealed class TestAction {

        object LoadData : TestAction() {
            class Success(val data: String) : SkadiChange
        }
    }

    private sealed class TestViewAction : SkadiChange {

        object RequestData : TestViewAction()

        object ButtonClicked : TestViewAction()
    }

    private sealed class TestSignal {

        object ShowMessage : TestSignal()
    }

    private class TestUseCase {

        var executed = false

        fun execute(): String {
            executed = true
            return "success"
        }
    }
}