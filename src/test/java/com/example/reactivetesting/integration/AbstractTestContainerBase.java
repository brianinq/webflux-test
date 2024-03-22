package com.example.reactivetesting.integration;

import org.testcontainers.containers.MongoDBContainer;

public abstract class AbstractTestContainerBase {
    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");
        MONGO_DB_CONTAINER.start();
    }
}
