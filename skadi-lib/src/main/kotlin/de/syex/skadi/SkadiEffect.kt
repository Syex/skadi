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

/**
 * Shortcut to handle an unexpected [change] in the given [state]. Handling, in this case, meanes
 * it will throw a meaningful exception.
 */
fun <State : SkadiState, Action, Signal> unexpected(
    state: State,
    change: SkadiChange
): SkadiEffect<State, Action, Signal> {
    throw IllegalStateException(
        "Encountered an illegal combination of a state and a change. Change " +
                "$change is not handled in state $state"
    )
}