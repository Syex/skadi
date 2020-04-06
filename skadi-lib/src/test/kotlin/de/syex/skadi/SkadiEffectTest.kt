package de.syex.skadi

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SkadiEffectTest {

    @Test()
    fun `method unexpected throws an enxception`() {
        assertThrows<IllegalStateException> {
            unexpected<TestSkadiState, TestSkadiAction, TestSkadiSignal>(
                TestSkadiState(),
                TestSkadiChange()
            )
        }
    }
}