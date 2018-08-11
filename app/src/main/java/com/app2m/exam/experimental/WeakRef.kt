package com.app2m.exam.experimental

import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * kotlin协程高级玩法之弱引用、解决协程导致的内存泄露
 * http://www.jcodecraeer.com/a/anzhuokaifa/2017/0830/8448.html
 *
 */
class WeakRef<T> internal constructor(any: T) {
    private val wearRef = WeakReference(any)

    suspend operator fun invoke() : T {
        return suspendCoroutine {
            wearRef.get() ?: throw CancellationException()
        }
    }
}

fun<T: Any> T.weakReference() = WeakRef(this)