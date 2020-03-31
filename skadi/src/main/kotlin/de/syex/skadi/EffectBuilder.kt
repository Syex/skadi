package de.syex.skadi

/**
 * A wrapper class that holds a `state` and optionally a list of `actions` to create a [SkadiEffect].
 *
 * @param State A [SkadiState] this effect will lead to.
 * @param Action The type of `actions` this effect will have as side effects.
 */
class EffectBuilder<State, Action> where State : SkadiState {

    private var state: State? = null
    private var actions = emptyList<Action>()

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
     * Creates a new [SkadiEffect] using previously set [state] and optional [actions].
     *
     * **Caution** An effect *requires* a [state], make sure to set one before.
     */
    fun build(): SkadiEffect<State, Action> {
        val state = state ?: throw IllegalStateException(
            "Cannot build an effect without setting a state. " +
                    "Please set a state before calling build()"
        )
        return SkadiEffect(state, actions)
    }
}

/**
 * Kotlin DSL method to create a [SkadiEffect], providing only a `state`.
 */
fun <State, Action> state(lambda: () -> State): SkadiEffect<State, Action> {
    return SkadiEffect(lambda())
}

/**
 * Kotlin DSL method to create a [SkadiEffect], providing an [EffectBuilder] to build the `effect`.
 */
fun <State : SkadiState, Action> effect(lambda: EffectBuilder<State, Action>.() -> Unit): SkadiEffect<State, Action> {
    return EffectBuilder<State, Action>().apply(lambda).build()
}