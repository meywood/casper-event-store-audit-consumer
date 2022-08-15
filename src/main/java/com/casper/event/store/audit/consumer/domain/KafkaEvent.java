package com.casper.event.store.audit.consumer.domain;

import com.casper.sdk.model.event.EventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * An object that represents the JSON read from
 * @author ian@meywood.com
 */
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class KafkaEvent {
    /** The source of the event */
    private final String source;
    /** The optional event ID */
    private final Long id;
    /** The type of the event, which is it's kafka topic */
    private final EventType eventType;
    /** The raw JSON event data */
    private final String data;
}
