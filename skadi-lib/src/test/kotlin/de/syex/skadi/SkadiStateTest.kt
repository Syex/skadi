package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class SkadiStateTest {

    @Test
    fun `calling same on a state creates an effect with that state and no changes`() {
        val state = TestSkadiState()

        val effect = state.same<TestSkadiState, Nothing, Nothing>()
        assertThat(effect.state).isEqualTo(state)
        assertThat(effect.actions).isEmpty()
        assertThat(effect.signals).isEmpty()
    }

    @Test
    fun `calling signal on a state creates an effect with same state and that signal`() {
        val state = TestSkadiState()
        val signal = TestSkadiSignal()

        val effect = state.signal<TestSkadiState, Nothing, TestSkadiSignal>(signal)
        assertThat(effect.state).isEqualTo(state)
        assertThat(effect.signals).isEqualTo(listOf(signal))
        assertThat(effect.actions).isEmpty()
    }
}
