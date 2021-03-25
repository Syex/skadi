package io.github.syex.skadi

/**
 * A wrapper class that holds a `state` and optionally a list of `actions` to create a [SkadiEffect].
 *
 * @param State A [SkadiState] this effect will lead to.
 * @param Action The type of `actions` this effect will have as side effects.
 */
class EffectBuilder<State, Action, Signal> where State : SkadiState {

    private var state: State? = null
    private var actions = emptyList<Action>()
    private var signals = emptyList<Signal>()

    /**
     * Sets the `state` of the `effect`.
     */
    fun state(lambda: () -> State) {
        this.state = lambda()
    }

    /**
     * Sets the `side effects` of the `effect`.
     */
    fun actions(lambda: () -> List<Action>) {
        this.actions = lambda()
    }

    /**
     * Sets the single `side effect` of the `effect`.
     */
    fun action(lambda: () -> Action) {
        this.actions = lambda().run { listOf(this) }
    }

    /**
     * Sets the `signals` of the `effect`.
     */
    fun signals(lambda: () -> List<Signal>) {
        this.signals = lambda()
    }

    /**
     * Sets the single `signal` of the `effect`.
     */
    fun signal(lambda: () -> Signal) {
        this.signals = lambda().run { listOf(this) }
    }

    /**
     * Creates a new [SkadiEffect] using previously set [state] and optional [actions].
     *
     * **Caution** An effect *requires* a [state], make sure to set one before.
     */
    fun build(): SkadiEffect<State, Action, Signal> {
        val state = state ?: throw IllegalStateException(
            "Cannot build an effect without setting a state. " +
                    "Please set a state before calling build()"
        )
        return SkadiEffect(state, actions, signals)
    }
}

/**
 * Kotlin DSL method to create a [SkadiEffect], providing only a `state`.
 */
inline fun <State, Action, Signal> state(lambda: () -> State): SkadiEffect<State, Action, Signal> {
    return SkadiEffect(lambda())
}

/**
 * Kotlin DSL method to create a [SkadiEffect], providing an [EffectBuilder] to build the `effect`.
 */
inline fun <State : SkadiState, Action, Signal> effect(
    lambda: EffectBuilder<State, Action, Signal>.() -> Unit
): SkadiEffect<State, Action, Signal> {
    return EffectBuilder<State, Action, Signal>().apply(lambda).build()
}
