package de.syex.skadi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A [SkadiStore] manages and controls the state of your application. It reacts on internal or external changes and
 * with the help of the passed `reducer` translates a pair of the current `state` and a `change` to a new `state` with
 * possible `signals` to publish or `actions` to perform, which may lead to a new `state` with new `actions` to perform.
 *
 * After instantiation, you can interact with the [SkadiStore] via [perform], which takes a subclass of a [SkadiChange].
 * This `change` can e.g. be a button click event:
 *
 * ```
 * sealed class ViewAction : SkadiChange {
 *
 *   object ButtonClicked : ViewAction()
 * }
 * ```
 *
 * This `change` will be passed to your [reducer] along with the current [state], where you have to reduce this tuple
 * to a new [SkadiState], which represents the state of your application:
 *
 * ```
 * sealed class ViewState : SkadiState {
 *
 *  object Init : ViewState()
 *
 *  object Loading : ViewState()
 * }
 *
 * Store(
 *   initialState = ViewState.Init,
 *    reducer = { state: ViewState, change: Change ->
 *      when (state) {
 *         is ViewState.Init -> when (change) {
 *           is ViewAction.ButtonClicked -> state { ViewState.Loading }
 *         }
 *         else -> throw IllegalStateException()
 *      }
 *    },
 *    ...
 * )
 * ```
 *
 * Here you define to go to `Loading` when a button was clicked. However, in this scenario you typically would want to
 * load some data, that's why your `reducer` returns a [SkadiEffect], which consists of a [SkadiState] and optionally a
 * list of `actions` to perform as a side effect. Each of this `actions` can lead to a new [SkadiChange]:
 *
 * ```
 * sealed class Action {
 *
 *   object LoadData : Action() {
 *
 *     class Success(val data: List<String>) : Change
 *
 *     object Failure : Change
 *   }
 * }
 *
 * Store(
 *   initialState = ViewState.Init,
 *    reducer = { state: ViewState, change: Change ->
 *      when (state) {
 *         is ViewState.Init -> when (change) {
 *           is ViewAction.ButtonClicked -> effect {
 *             state { ViewState.Loading }
 *             action { Action.LoadData }
 *           }
 *         }
 *         is ViewState.Loading -> when (change) {
 *           is Action.LoadData.Success -> state { ViewState.DisplayData(change.data) }
 *         }
 *         else -> throw IllegalStateException()
 *      }
 *    },
 *    actions = { action: Action ->
 *      when (action) {
 *        is Action.LoadData -> {
 *          val data = useCase.getData()
 *          Action.LoadData.Success(data)
 *        }
 *      }
 *   }
 * )
 * ```
 *
 * We now additionally tell [SkadiStore] to perform the `action` `LoadData`, which behavior you define below via
 * [actions]. This block is called from a `coroutine`, so you can call `suspending` functions here.
 *
 * @param initialState The `state` your application starts with.
 * @param reducer The reducer function with translates tuples of [SkadiState] and [SkadiChange] to a [SkadiEffect].
 * @param actions A function where you define what [SkadiChange] your actions should perform.
 * This is called in a new coroutine, so it's safe to call blocking functions.
 * @param coroutineScope A [CoroutineScope] where all `coroutines` started by this `store` should run in.
 * When the scope gets canceled this store cannot be used anymore.
 */
class SkadiStore<State, Action, Signal>(
    initialState: State,
    private val reducer: (State, SkadiChange) -> SkadiEffect<State, Action, Signal>,
    private val actions: suspend (Action) -> SkadiChange = unhandledAction(),
    private val coroutineScope: CoroutineScope
) where State : SkadiState {

    /**
     * Backing property where new states are posted to. We only make the immutable interface
     * accessible via [stateFlow].
     */
    private val _stateFlow = MutableStateFlow(initialState)

    /**
     * Observe this [StateFlow] to be notified whenever the internal [state] changes.
     *
     * Current value can be retrieved via [StateFlow.value]. The current value will be emitted upon
     * collection start.
     */
    val stateFlow get() = _stateFlow.asStateFlow()

    /**
     * Internal channel where new signals are posted to.
     */
    private val _signalFlow = MutableSharedFlow<Signal>()

    /**
     * Observe this [SharedFlow] to be notified whenever the a new `signal` is published. Only new values
     * will be emitted to this `Flow`. Signals can be used as "fire and forget" type of events.
     */
    val signalFlow = _signalFlow.asSharedFlow()

    /**
     * Performs all `actions` from [sideEffects] in a new coroutine and [performs][perform] its resulting [SkadiChange].
     */
    private fun performSideEffect(sideEffects: List<Action>) {
        for (sideEffect in sideEffects) {
            coroutineScope.launch {
                val sideEffectChange = actions(sideEffect)
                perform(sideEffectChange)
            }
        }
    }

    private fun sendSignals(signals: List<Signal>) {
        for (signal in signals) {
            coroutineScope.launch { _signalFlow.emit(signal) }
        }
    }

    /**
     * Performs the given [SkadiChange], passing it along with the current [state] to the [reducer].
     */
    fun perform(change: SkadiChange) = coroutineScope.launch {
        val (newState, actions, signals) = reducer(_stateFlow.value, change)

        _stateFlow.value = newState

        performSideEffect(actions)
        sendSignals(signals)
    }

    /**
     * Performs a single action, not using the [reducer]. This is basically a shortcut to change
     * the current `state`, as the `action` can lead to a new `state`.
     */
    fun performAction(action: Action) {
        performSideEffect(listOf(action))
    }

    companion object {

        /**
         * Default value if no `actions` are passed. Can only be called if an `action` should be performed, but
         * no `actions` are defined.
         */
        private fun <Action> unhandledAction(): suspend (Action) -> Nothing =
            { action -> throw IllegalStateException("You didn't specify any actions, but passed $action as a side effect") }
    }
}
