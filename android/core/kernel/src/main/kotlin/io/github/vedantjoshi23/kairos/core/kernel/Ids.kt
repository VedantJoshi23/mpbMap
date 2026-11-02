package io.github.vedantjoshi23.kairos.core.kernel

@JvmInline
value class TripId(val value: String)

@JvmInline
value class AlarmId(val value: String)

/**
 * Time source, injected so domain logic stays testable (STD-CODE rule 2).
 * [elapsedMillis] is monotonic within one boot; [wallMillis] is UTC epoch time.
 */
interface Clock {
    fun wallMillis(): Long
    fun elapsedMillis(): Long
}
