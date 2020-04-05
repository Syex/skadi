package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EffectBuilderTest {

    @Test
    fun `dsl method state() creates an effect only with that state`() {
        val testState = TestSkadiState()
        val effect = state<TestSkadiState, Nothing, Nothing> { testState }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEmpty()
        assertThat(effect.signals).isEmpty()
    }

    @Test
    fun `dsl method effect() creates an effect with state, actions and signals`() {
        val testState = TestSkadiState()
        val actions = listOf(TestSkadiAction(), TestSkadiAction())
        val signals = listOf(TestSkadiSignal(), TestSkadiSignal())
        val effect = effect<TestSkadiState, TestSkadiAction, TestSkadiSignal> {
            state { testState }
            actions { actions }
            signals { signals }
        }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEqualTo(actions)
        assertThat(effect.signals).isEqualTo(signals)
    }

    @Test
    fun `passing single action to effect builder sets this as actions`() {
        val testState = TestSkadiState()
        val action = TestSkadiAction()
        val effect = effect<TestSkadiState, TestSkadiAction, Nothing> {
            state { testState }
            action { action }
        }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEqualTo(listOf(action))
        assertThat(effect.signals).isEmpty()
    }

    @Test
    fun `passing single signal to effect builder sets this as signals`() {
        val testState = TestSkadiState()
        val signal = TestSkadiSignal()
        val effect = effect<TestSkadiState, TestSkadiAction, TestSkadiSignal> {
            state { testState }
            signal { signal }
        }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEmpty()
        assertThat(effect.signals).isEqualTo(listOf(signal))
    }

    @Test
    fun `dsl method effect() throws if no state is passed`() {
        val action = TestSkadiAction()
        assertThrows<IllegalStateException> {
            effect<TestSkadiState, TestSkadiAction, Nothing> {
                action { action }
            }
        }
    }
}
