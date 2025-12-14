package com.webjava.lab1.service;

public class EntityNotFoundException extends RuntimeException {

  private final String entityName;
  private final Object entityId;

  public EntityNotFoundException(String entityName, Object entityId) {
    super(entityName + " with id " + entityId + " not found");
    this.entityName = entityName;
    this.entityId = entityId;
  }

  public String getEntityName() {
    return entityName;
  }

  public Object getEntityId() {
    return entityId;
  }
}
