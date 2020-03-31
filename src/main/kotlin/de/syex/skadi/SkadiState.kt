package de.syex.skadi

/**
 * Simple marker interface for states that should be controlled by a [SkadiStore].
 */
interface SkadiState

/**
 * Creates a [SkadiEffect] that copies the [SkadiState] this method is called on.
 */
fun <T : SkadiState, Action> T.same() = state<T, Action> { this }
