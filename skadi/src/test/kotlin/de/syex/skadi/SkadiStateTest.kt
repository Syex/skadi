package de.syex.skadi

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class SkadiStateTest {

    @Test
    fun `calling same on a state creates an effect with that state and no actions`() {
        val state = TestSkadiState()

        val effect = state.same<TestSkadiState, Unit>()
        assertThat(effect.state).isEqualTo(state)
        assertThat(effect.actions).isEmpty()
    }
}

private class TestSkadiState : SkadiState