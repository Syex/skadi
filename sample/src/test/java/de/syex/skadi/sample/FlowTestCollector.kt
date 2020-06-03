package de.syex.skadi.sample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Closeable

fun <T> Flow<T>.test2(scope: CoroutineScope): TestCollector<T> {
    return TestCollector(scope, this)
}

class TestCollector<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) : Closeable {

    val values = mutableListOf<T>()

    private val job = scope.launch {
        flow.collect { values.add(it) }
    }

    override fun close() {
        job.cancel()
    }
}