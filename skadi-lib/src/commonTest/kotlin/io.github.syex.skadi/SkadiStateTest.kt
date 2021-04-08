package io.github.syex.skadi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class SkadiStateTest {

    @Test
    fun `calling same on a state creates an effect with that state and no changes`() {
        val state = TestSkadiState()

        val effect = state.same<TestSkadiState, Nothing, Nothing>()
        assertEquals(effect.state, state)
        assertTrue(effect.actions.isEmpty())
        assertTrue(effect.signals.isEmpty())
    }

    @Test
    fun `calling same on a state with actions and signals sets these actions and signals`() {
        val state = TestSkadiState()

        val actions = listOf(TestSkadiAction())
        val signals = listOf(TestSkadiSignal())
        val effect = state.same(actions, signals)
        assertEquals(effect.state, state)
        assertEquals(effect.actions, actions)
        assertEquals(effect.signals, signals)
    }

    @Test
    fun `calling signal on a state creates an effect with same state and that signal`() {
        val state = TestSkadiState()
        val signal = TestSkadiSignal()

        val effect = state.signal<TestSkadiState, Nothing, TestSkadiSignal>(signal)
        assertEquals(effect.state, state)
        assertTrue(effect.actions.isEmpty())
        assertEquals(effect.signals, listOf(signal))
    }
}
