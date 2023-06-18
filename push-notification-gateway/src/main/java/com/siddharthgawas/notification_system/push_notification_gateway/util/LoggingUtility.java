package com.siddharthgawas.notification_system.push_notification_gateway.util;

import lombok.experimental.UtilityClass;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Map;

/**
 * The type Logging utility.
 */
@UtilityClass
public class LoggingUtility {

    /**
     * Log event.
     *
     * @param logger        the logger
     * @param level         the level
     * @param eventName     the event name
     * @param markerEntries the marker entries
     * @param message       the message
     */
    public void logEvent(final Logger logger,
                         final Level level,
                         final String eventName, final Map<?, ?> markerEntries, final String message) {
        logger.atLevel(level)
                .setMessage(message)
                .addMarker(Markers.append("event", eventName))
                .addMarker(Markers.appendEntries(markerEntries)).log();
    }

    /**
     * Log event.
     *
     * @param logger        the logger
     * @param eventName     the event name
     * @param throwable     the throwable
     * @param markerEntries the marker entries
     * @param message       the message
     */
    public void logEvent(final Logger logger,
                         final String eventName,
                         final Throwable throwable,
                         final Map<?, ?> markerEntries, final String message) {
        logger.atLevel(Level.ERROR)
                .setMessage(message)
                .setCause(throwable)
                .addMarker(Markers.append("event", eventName))
                .addMarker(Markers.appendEntries(markerEntries)).log();
    }
}
