package io.github.syex.skadi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EffectBuilderTest {

    @Test
    fun `dsl method state() creates an effect only with that state`() {
        val testState = TestSkadiState()
        val effect = state<TestSkadiState, Nothing, Nothing> { testState }

        assertEquals(effect.state, testState)
        assertTrue(effect.actions.isEmpty())
        assertTrue(effect.signals.isEmpty())
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

        assertEquals(effect.state, testState)
        assertEquals(effect.actions, actions)
        assertEquals(effect.signals, signals)
    }

    @Test
    fun `passing single action to effect builder sets this as actions`() {
        val testState = TestSkadiState()
        val action = TestSkadiAction()
        val effect = effect<TestSkadiState, TestSkadiAction, Nothing> {
            state { testState }
            action { action }
        }

        assertEquals(effect.state, testState)
        assertEquals(effect.actions, listOf(action))
        assertTrue(effect.signals.isEmpty())
    }

    @Test
    fun `passing single signal to effect builder sets this as signals`() {
        val testState = TestSkadiState()
        val signal = TestSkadiSignal()
        val effect = effect<TestSkadiState, TestSkadiAction, TestSkadiSignal> {
            state { testState }
            signal { signal }
        }

        assertEquals(effect.state, testState)
        assertTrue(effect.actions.isEmpty())
        assertEquals(effect.signals, listOf(signal))
    }

    @Test
    fun `dsl method effect() throws if no state is passed`() {
        val action = TestSkadiAction()
        assertFailsWith<IllegalStateException> {
            effect<TestSkadiState, TestSkadiAction, Nothing> {
                action { action }
            }
        }
    }
}