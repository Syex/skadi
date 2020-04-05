package de.syex.skadi

/**
 * An `effect` combines a [SkadiEffect] with an optional list of [actions] to perform as side
 * effects and an optional list of one-time [signals]. It's the outcome of a `reducer`.
 */
data class SkadiEffect<State, Action, Signal>(
    val state: State,
    val actions: List<Action> = emptyList(),
    val signals: List<Signal> = emptyList()
)