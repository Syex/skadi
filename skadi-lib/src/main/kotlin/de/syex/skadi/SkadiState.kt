package de.syex.skadi

/**
 * Simple marker interface for states that should be controlled by a [SkadiStore].
 */
interface SkadiState

/**
 * Creates a [SkadiEffect] that copies the [SkadiState] this method is called on.
 */
fun <T : SkadiState, Action, Signal> T.same() = state<T, Action, Signal> { this }

/**
 * Creates a [SkadiEffect] that copies the [SkadiState] this method is called on and sets the single
 * [signal] as the effect's signal.
 *
 * @param signal The `signal` to send.
 * @return A [SkadiEffect] copying the state and containing the set `signal`.
 */
fun <T : SkadiState, Action, Signal> T.signal(signal: Signal): SkadiEffect<T, Action, Signal> {
    val state = this
    return effect {
        state { state }
        signal { signal }
    }
}
