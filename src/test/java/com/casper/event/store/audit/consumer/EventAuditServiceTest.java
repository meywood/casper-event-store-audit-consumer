package com.casper.event.store.audit.consumer;

import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EventAuditServiceTest {

    private static final String MAIN_EVENTS_JSON = "/kafka-data/kafka-events-main.json";
    @Autowired
    private EventAuditService eventAuditService;

    @Autowired
    private MongoOperations mongoOperations;

    private JsonNode jsonNode;

    @BeforeEach
    void setUp() throws IOException {
        final InputStream jsonIn = EventAuditServiceTest.class.getResourceAsStream(MAIN_EVENTS_JSON);
        jsonNode = new ObjectMapper().readTree(jsonIn);
    }

    @AfterEach
    void tearDown() {
        // Ensure database is dropped after every test
        ((MongoTemplate) mongoOperations).getDb().drop();
    }

    @Test
    void eventAuditServiceCreation() {
        assertThat(eventAuditService, is(notNullValue()));
    }


    @Test
    void saveAndFindApiVersionMainEvent() throws IOException {

        final Document document = eventAuditService.save(jsonNode.get(0).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(document, is(notNullValue()));
        ;
        final ObjectId id = document.getObjectId("_id");
        assertThat(id, is(notNullValue()));

        assertThat(document.get("type", String.class), is("main"));
        assertThat(document.get("data", Document.class).get("ApiVersion", String.class), is("1.4.7"));
        assertThat(document.containsKey("id"), is(false));

        final Optional<Document> found = eventAuditService.findById(id, EventType.MAIN);
        assertThat(found.isPresent(), is(true));

        assertThat(found.get().get("type", String.class), is("main"));
        assertThat(found.get().get("data", Document.class).get("ApiVersion", String.class), is("1.4.7"));
        assertThat(found.get().containsKey("id"), is(false));
    }

    @Test
    void saveBlockAddedMainEvent() {

        final Document document = eventAuditService.save(jsonNode.get(1).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(document, is(notNullValue()));
        ;
        final ObjectId id = document.getObjectId("_id");
        assertThat(id, is(notNullValue()));

        assertThat(document.get("type", String.class), is("main"));
        assertThat(document.get("id", Integer.class), is(65027303));
        final Document blockAdded = document.get("data", Document.class).get("BlockAdded", Document.class);
        assertThat(blockAdded, is(notNullValue()));
        assertThat(blockAdded.get("block_hash", String.class), is("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"));
        assertThat(blockAdded.get("block", Document.class).get("hash", String.class), is("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"));
    }

    @Test
    void saveDeployProcessedMainEvent() {

        final Document document = eventAuditService.save(jsonNode.get(2).toPrettyString());

        // Assert a unique ID is assigned to the event
        assertThat(document, is(notNullValue()));
        ;
        final ObjectId id = document.getObjectId("_id");
        assertThat(id, is(notNullValue()));

        assertThat(document.get("type", String.class), is("main"));
        assertThat(document.get("id", Integer.class), is(65028921));
        final Document deployProcessed = document.get("data", Document.class).get("DeployProcessed", Document.class);
        assertThat(deployProcessed, is(notNullValue()));
        assertThat(deployProcessed.get("deploy_hash", String.class), is("c7d0840f2275a18efcd716f425c06691f2ca1a0e6d7d7ecff49cab06c2428ee8"));
    }
}