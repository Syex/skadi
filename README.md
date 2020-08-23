![](https://github.com/Syex/skadi/workflows/skadi%20ci/badge.svg?branch=master)
[ ![Download](https://api.bintray.com/packages/syex/skadi/skadi/images/download.svg) ](https://bintray.com/syex/skadi/skadi/_latestVersion)

# skadi
A lightweight, redux-like MVI implementation for the JVM using Kotlin coroutines. 

The library internally uses [StateFlow](https://github.com/Kotlin/kotlinx.coroutines/issues/1973), 
which is in experimental state. Therefore, consider the state of this library as experimental, too.
I highly recommend **not** using this in production (yet). 

# Download
This library can be found on `jcenter()` and depends on the `kotlinx-coroutines-core` artifact.

```groovy
repositories {
    jcenter()
}

dependencies {
    implementation "de.syex:skadi:$version"
}
```

# Terminology
For a deeper understand of the redux architecture please refer to other blog posts like [this one](https://jayrambhia.com/blog/kotlin-redux-architecture). 
This sections serves as a quick overview of the most important classes you'll have to deal with using *skadi*.

## SkadiState
Defines the current state your application currently is in, e.g. `Loading`, when your app is currently
loading some content. The state can change as a result of applying a *reducer*. You have to implement
the *reducer*, which is a function which maps the current state and a *change* to a *SkadiEffect*.

## SkadiEffect
A `SkadiEffect` is the output of the reducer function. A state and an *action* performed in that state
trigger an effect. The effect contains a new state, an optional list of *actions*  to perform and an
optional list of *signals*  to send.

## Action
An action is something that leads to a new *change*. E.g. you may have an action to load the user
profile and on success you want to perform a change that leads to a new state. Any actions you want 
to perform have to be defined. In *skadi* your actions are automatically called on a new coroutine.

Actions are part of a `SkadiEffect`, so whenever you may want to move to a new state you can also
perform actions.

## SkadiChange
An *action* **or** an external event can trigger a change, which may lead to a new state. An example
for an external event is, for instance, when the user clicks on a button. 

## SkadiStore
The store is the base of any redux architecture, putting everything together and where you need to
define your intitial state, reducer, actions etc.

# Sample usage
For Android, there is a sample app showing how to use skadi in a `ViewModel`.

In general, you need to create a `SkadiStore`, which will be your single source of truth, managing a state
and handling any actions to change this state.

The `SkadiStore` expects three types to be defined, first is the type of your state. A possible
implementation, where you have one state saying you're loading data and one where you're 
displaying some data, could look like this:

```kotlin
sealed class ExampleState : SkadiState {

    object Loading : ExampleState()

    data class DisplayData(val data: List<String>) : ExampleState()
}
```

Second is the type of actions you want to perform. For instance, we could want to perform an action
that loads the data and additionally define a `SkadiChange` for the case this action is successful.


```kotlin
sealed class Action {

    object LoadData : Action() {
        data class Success(val data: List<String>) : SkadiChange
    }
}
```

Third are possible `signals` that you want to send. These will be covered in another area. We can
simply put Kotlin's `Nothing` here.

All put together, we can define our `SkadiStore` like this:

```kotlin
val store = SkadiStore<ExampleState, Action, Nothing>(
    initialState = Loading,
    reducer = { state, change ->
      when (state) {
          Loading -> when (change) {
              is LoadData.Success -> {
                  effect {
                      state { DisplayData(change.data) }
                  }
              }
              else -> unexpected(state, change)
          }
      }
    },
    actions = { action ->
      when (action) {
          LoadData -> {
              val data = // in coroutine context, you can call suspend functions here
              return LoadData.Success(movies)
          }
      }
    },
    coroutineScope = // scope in which the store should be active
)
```

You now can start collecting the state changes on `store.stateFlow`.

# Further concepts
This sections informs about more concepts *skadi* offers.

## Handling external changes
Sometimes you have to handle external events that trigger a change and therefore may lead to a new state.
The easiest example is the click on a login button that obviously should login the user and move to a new screen.

A `SkadiStore` has the `perform(SkadiChange)` method exactly for that purpose. It will simply
pass the current state along with that change to your reducer where you can handle this change. 
It's the easiest solution to just define your external events as another `sealed class`:

```kotlin
sealed class ViewEvent : SkadiChange {

    data class ItemClicked(val item: Item) : ViewEvent()

}
```

So you can easily call `SkadiStore.perform(ItemClicked(item))`.

## Manually performing actions
Actions are typically a side effect of a `SkadiEffect`. Sometimes you may want to trigger an 
action without calling your reducer to reduce boilerplate code. 

For instance, initially you want to load some data, what is defined as an action. You'd have to
create a `SkadiChange` for this purpose and handle this change in your reducer function, returning
a `SkadiEffect` with the same state and your desired action as a side effect. 

Instead, you can call `SKadiStore.performAction(action)`, which will do the mentioned procedure for you.

## Signals
You may not want to change the `state` on every change that is passed to your reducer. Maybe, instead
you only want to show some message to the user or open another application or screen. That is what
signals are for. They are what you would call "fire and forget".

Signals are part of a `SkadiEffect`, they can be collected via `SkadiStore.signalFlow`.

## Useful functions
There are some extension functions defined to reduce boilerplate code.

```kotlin
fun unexpected(state: SkadiState, change: SkadiChange)
```
Shortcut to throw an exception whenever you encounter a change in a state that you didn't expect.
To be used in your reducer function.

```kotlin
fun SkadiState.same(actions: List<Action> = emptyList(), signals: List<Signal> = emptyList()): SkadiEffect
```

Shortcut to create a `SkadiEffect` with the `state` the function was called on, but passing some
`actions` or `signals`.

```kotlin
fun SkadiState.signal(signal: Signal): SkadiEffect
```

Shortcut to create a `SkadiEffect` with the same state, only sending the passed `signal`.

# Testing
In general I recommend using [Flow test observer](https://github.com/ologe/flow-test-observer) for
easier testing of skadi's `stateFlow` and `signalFlow`.

**Important:** Please pay attention, that `StateFlow`, which is used internally to handle states, conflates its values,
meaning, fast state changes won't emit every change. E.g. if you test state transition from _a_ to
_b_ to _c_ then you will only be able to collect _c_, but not _b_.
