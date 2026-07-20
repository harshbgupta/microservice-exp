package com.kritsn.lib.logger

import org.slf4j.LoggerFactory
import org.slf4j.spi.LocationAwareLogger
import kotlin.jvm.java

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 08, 2025
 */

object Timber {
    @PublishedApi internal val base = LoggerFactory.getLogger("app")
    @PublishedApi internal val la = base as? LocationAwareLogger
    @PublishedApi internal val FQCN = Timber::class.java.name
//    @PublishedApi internal val tag = Throwable

    inline fun v(crossinline m: () -> String) = {
        log(LocationAwareLogger.TRACE_INT, null, m)
    }
    inline fun d(crossinline m: () -> String) = log(LocationAwareLogger.DEBUG_INT, null, m)
    inline fun i(crossinline m: () -> String) = log(LocationAwareLogger.INFO_INT,  null, m)
    inline fun w(crossinline m: () -> String) = log(LocationAwareLogger.WARN_INT,  null, m)
    inline fun e(crossinline m: () -> String) = log(LocationAwareLogger.ERROR_INT, null, m)
    inline fun e(t: Throwable, crossinline m: () -> String) =
        log(LocationAwareLogger.ERROR_INT, t, m)

    @PublishedApi internal inline fun log(level: Int, t: Throwable?, crossinline m: () -> String) {
        if (!isEnabled(level)) return
        val msg = m()
        la?.log(null, FQCN, level, msg, null, t) ?: when (level) {
            LocationAwareLogger.TRACE_INT -> if (t != null) base.trace(msg, t) else base.trace(msg)
            LocationAwareLogger.DEBUG_INT -> if (t != null) base.debug(msg, t) else base.debug(msg)
            LocationAwareLogger.INFO_INT  -> if (t != null) base.info (msg, t) else base.info (msg)
            LocationAwareLogger.WARN_INT  -> if (t != null) base.warn (msg, t) else base.warn (msg)
            else                          -> if (t != null) base.error(msg, t) else base.error(msg)
        }
    }
    @PublishedApi internal fun isEnabled(level: Int) = when (level) {
        LocationAwareLogger.TRACE_INT -> base.isTraceEnabled
        LocationAwareLogger.DEBUG_INT -> base.isDebugEnabled
        LocationAwareLogger.INFO_INT  -> base.isInfoEnabled
        LocationAwareLogger.WARN_INT  -> base.isWarnEnabled
        else                          -> base.isErrorEnabled
    }
}