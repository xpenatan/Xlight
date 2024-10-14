package xlight.engine.ecs.entity;

import xlight.engine.list.XList;

public interface XEntityService {

    /**
     * @return a detached entity with a unique ID.
     */
    XEntity obtain();

    /**
     * @return a detached entity with a custom id. Will return null if entity is detached or attached.
     */
    XEntity obtain(int id);

    /**
     * Release the entity from the world if it's attached or not.
     * This entity will be changed to release state and will release all components from the systems.
     */
    boolean releaseEntity(XEntity entity);

    /**
     * Attach entity to the world.
     * This entity will be changed to attach state and will match all components to systems
     */
    boolean attachEntity(XEntity entity);
    /**
     * Detach entity from the world.
     * This entity will be changed to Detach state and will release all components from the systems.
     * Return true if detached is successfully.
     */
    boolean detachEntity(XEntity entity);

    XEntity getEntity(int id);

    XList<XEntity> getEntities();

    /**
     * Clear all entities and remove the components from the systems. This is done sync.
     */
    void clear();
}