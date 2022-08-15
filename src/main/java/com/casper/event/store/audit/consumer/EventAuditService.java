package com.casper.event.store.audit.consumer;

import com.casper.sdk.model.event.EventType;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


/**
 * The service for storing events that from Kafka into the mongo database. The events are persisted as BSON documents
 * with the collection names matching the topic/event type name.
 *
 * @author ian@meywood.com
 */
@Service
public class EventAuditService {

    private final MongoOperations mongoOperations;

    public EventAuditService(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        createIndexes();
    }

    /**
     * Saves a JSON event as a BSON document in mongo. The json is stored in the collection of it's event type/topic
     *
     * @param jsonEvent the JSON event read from Kafka
     * @return the BSON document that was persisted in mongo
     */
    public Document save(final String jsonEvent) {

        // Convert the json to a Mongo document
        final Document document = Document.parse(jsonEvent);

        // Obtain the event to use as the collection/topic name
        final String eventType = Objects.requireNonNull(document.get("type", String.class),
                "\"type\" must be present in the JSON."
        );

        return mongoOperations.save(document, eventType);
    }


    public Optional<Document> findById(final ObjectId id, final EventType eventType) {
        return Optional.ofNullable(mongoOperations.findById(id, Document.class, getCollectionName(eventType)));
    }


    public Iterable<Document> findAllSince(long eventId, final EventType eventType, final Pageable pageable) {

        final Query query = Query.query(Criteria.where("id").gte(eventId))
                .with(pageable)
                .with(Sort.by("id"));

        return mongoOperations.find(query, Document.class, getCollectionName(eventType));
    }

    /**
     * Creates the indexes for the collections used for events
     */
    private void createIndexes() {

        // Create the common indexes for all topics
        for (EventType eventType : EventType.values()) {
            mongoOperations.getCollection(getCollectionName(eventType))
                    .createIndex(Indexes.ascending("type", "dataType", "source", "id"));
        }
    }


    /**
     * Obtains the collection name for a given eventType/topic.
     *
     * @param eventType the event type that is used as a kafka topic and mongo collection name
     * @return the collection name for the event type
     */
    private static String getCollectionName(final EventType eventType) {
        return eventType.name().toLowerCase();
    }
}
