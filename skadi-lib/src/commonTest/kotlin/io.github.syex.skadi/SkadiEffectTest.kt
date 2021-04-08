package io.github.syex.skadi

import kotlin.test.Test
import kotlin.test.assertFailsWith


internal class SkadiEffectTest {

    @Test()
    fun `method unexpected throws an exception`() {
        assertFailsWith<IllegalStateException> {
            unexpected<TestSkadiState, TestSkadiAction, TestSkadiSignal>(
                TestSkadiState(),
                TestSkadiChange()
            )
        }
    }
}