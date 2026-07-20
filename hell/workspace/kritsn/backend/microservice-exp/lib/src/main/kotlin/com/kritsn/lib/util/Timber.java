package com.kritsn.lib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Aug 13, 2025
 */

public class Timber {

    // Automatically get the caller's class name as the log tag
    private static String getTag() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            // Skip this logger class and Java internal classes
            if (!className.equals(Timber.class.getName())
                    && !className.startsWith("java.")
                    && !className.startsWith("sun.")) {
                // Only return the simple class name, not the full package
                return className.substring(className.lastIndexOf('.') + 1);
            }
        }
        return "UnknownClass";
    }

    public static void d(String message) {
        Logger logger = LoggerFactory.getLogger(getTag());
        logger.debug(message);
    }

    public static void i(String message) {
        Logger logger = LoggerFactory.getLogger(getTag());
        logger.info(message);
    }

    public static void w(String message) {
        Logger logger = LoggerFactory.getLogger(getTag());
        logger.warn(message);
    }

    public static void e(String message) {
        Logger logger = LoggerFactory.getLogger(getTag());
        logger.error(message);
    }

    public static void e(String message, Throwable throwable) {
        Logger logger = LoggerFactory.getLogger(getTag());
        logger.error(message, throwable);
    }
}
