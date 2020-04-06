package de.syex.skadi

/**
 * Simple marker interface for states that should be controlled by a [SkadiStore].
 */
interface SkadiState

/**
 * Creates a [SkadiEffect] that copies the [SkadiState] this method is called on. You optionally
 * can pass `actions` and `signals` to execute, though.
 */
fun <T : SkadiState, Action, Signal> T.same(
    actions: List<Action> = emptyList(),
    signals: List<Signal> = emptyList()
): SkadiEffect<T, Action, Signal> {
    val state = this
    return effect {
        state { state }
        actions { actions }
        signals { signals }
    }
}

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
