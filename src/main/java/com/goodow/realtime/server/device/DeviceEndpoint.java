package com.goodow.realtime.server.device;

import com.goodow.realtime.server.RealtimeApisModule;
import com.goodow.realtime.server.persist.jpa.EMF;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Api(name = "device", version = RealtimeApisModule.DEFAULT_VERSION, defaultVersion = AnnotationBoolean.TRUE, namespace = @ApiNamespace(ownerDomain = "goodow.com", ownerName = "Goodow", packagePath = "api.services"))
public class DeviceEndpoint {

  @Inject
  Provider<EntityManager> em;

  @ApiMethod(name = "findBySessionId")
  public DeviceInfo findBySessionId(@Named("sessionId") String sessionId) {
    Query query = em.get().createQuery("select from DeviceInfo as d where d.sessionId = ?0");
    query.setParameter(0, sessionId);
    try {
      return (DeviceInfo) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public DeviceInfo findByToken(@Named("token") String token) {
    Query query = em.get().createQuery("select from DeviceInfo as d where d.token = ?0");
    query.setParameter(0, token);
    return EMF.getSingleResult(query);
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   * 
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod(name = "getDeviceInfo")
  public DeviceInfo getDeviceInfo(@Named("id") String id) {
    DeviceInfo deviceinfo = null;
    deviceinfo = em.get().find(DeviceInfo.class, id);
    return deviceinfo;
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already exists in the
   * datastore, an exception is thrown. It uses HTTP POST method.
   * 
   * @param deviceinfo the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod(name = "insertDeviceInfo")
  public DeviceInfo insertDeviceInfo(DeviceInfo deviceinfo) {
    if (containsDeviceInfo(deviceinfo)) {
      throw new EntityExistsException("Object already exists");
    }
    em.get().persist(deviceinfo);
    return deviceinfo;
  }

  /**
   * This method lists all the entities inserted in datastore. It uses HTTP GET method and paging
   * support.
   * 
   * @return A CollectionResponse class containing the list of all entities persisted and a cursor
   *         to the next page.
   */
  @SuppressWarnings({"unchecked", "unused"})
  @ApiMethod(name = "listDeviceInfo")
  public CollectionResponse<DeviceInfo> listDeviceInfo(
      @Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

    Cursor cursor = null;
    List<DeviceInfo> execute = null;

    StringBuilder q = new StringBuilder("select from DeviceInfo as d order by timestamp DESC ");
    Query query = em.get().createQuery(q.toString());
    if (cursorString != null && cursorString != "") {
      cursor = Cursor.fromWebSafeString(cursorString);
      query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
    }

    if (limit != null) {
      query.setFirstResult(0);
      query.setMaxResults(limit);
    }

    execute = query.getResultList();
    cursor = JPACursorHelper.getCursor(execute);
    if (cursor != null) {
      cursorString = cursor.toWebSafeString();
    }

    // Tight loop for fetching all entities from datastore and accomodate
    // for lazy fetch.
    for (DeviceInfo obj : execute) {
      ;
    }

    return CollectionResponse.<DeviceInfo> builder().setItems(execute).setNextPageToken(
        cursorString).build();
  }

  /**
   * This method removes the entity with primary key id. It uses HTTP DELETE method.
   * 
   * @param id the primary key of the entity to be deleted.
   * @return The deleted entity.
   */
  @ApiMethod(name = "removeDeviceInfo", httpMethod = HttpMethod.POST)
  public DeviceInfo removeDeviceInfo(@Named("id") String id) {
    DeviceInfo deviceinfo = null;
    deviceinfo = em.get().find(DeviceInfo.class, id);
    em.get().remove(deviceinfo);
    return deviceinfo;
  }

  /**
   * This method is used for updating an existing entity. If the entity does not exist in the
   * datastore, an exception is thrown. It uses HTTP PUT method.
   * 
   * @param deviceinfo the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(path = "updateDeviceInfo", httpMethod = HttpMethod.POST)
  public DeviceInfo updateDeviceInfo(DeviceInfo deviceinfo) {
    if (!containsDeviceInfo(deviceinfo)) {
      throw new EntityNotFoundException("Object does not exist");
    }
    em.get().persist(deviceinfo);
    return deviceinfo;
  }

  private boolean containsDeviceInfo(DeviceInfo deviceinfo) {
    boolean contains = true;
    DeviceInfo item = em.get().find(DeviceInfo.class, deviceinfo.getId());
    if (item == null) {
      contains = false;
    }
    return contains;
  }

}
