package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EffectBuilderTest {

    @Test
    fun `dsl method state() creates an effect only with that state`() {
        val testState = TestState()
        val effect = state<TestState, Unit> { testState }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEmpty()
    }

    @Test
    fun `dsl method effect() creates an effect with state and actions`() {
        val testState = TestState()
        val actions = listOf(TestAction(), TestAction())
        val effect = effect<TestState, TestAction> {
            state { testState }
            actions { actions }
        }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEqualTo(actions)
    }

    @Test
    fun `dsl method effect() creates an effect with state and a single action`() {
        val testState = TestState()
        val action = TestAction()
        val effect = effect<TestState, TestAction> {
            state { testState }
            action { action }
        }

        assertThat(effect.state).isEqualTo(testState)
        assertThat(effect.actions).isEqualTo(listOf(action))
    }

    @Test
    fun `dsl method effect() throws if no state is passed`() {
        val action = TestAction()
        assertThrows<IllegalStateException> {
            effect<TestState, TestAction> {
                action { action }
            }
        }
    }
}

private class TestAction

private class TestState : SkadiState