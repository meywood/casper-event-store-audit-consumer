package com.casper.event.store.audit.consumer.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

/**
 * Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.casper.event.store.audit")
public class MongoConfig extends AbstractMongoClientConfiguration {

    /** The name of the database to connect to */
    @Value("${spring.data.mongodb.database:casper-events}")
    private String databaseName;
    /** The mongo database host name */
    @Value("${spring.data.mongodb.host:localhost}")
    private String host;
    /** The mongo database port number */
    @Value("${spring.data.mongodb.host:27017}")
    private int port;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {

        final ConnectionString connectionString = new ConnectionString("mongodb://" + host + ":" + port + "/" + databaseName);

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build()
        );
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.casper");
    }
}