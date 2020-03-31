package de.syex.skadi

/**
 * An `effect` combines a [SkadiEffect] with an optional list of [actions] to perform as side effects. It's the outcome
 * of a `reducer`.
 */
data class SkadiEffect<State, Action>(val state: State, val actions: List<Action> = emptyList())