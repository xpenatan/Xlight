package xlight.engine.ecs.entity;

public interface XEntityService {

    /**
     * @return a detached entity with a unique ID.
     */
    XEntity obtain();

    /**
     * Release the entity from the world if it's attached or not.
     * This entity will be changed to release state and will release all components from the systems.
     */
    void releaseEntity(XEntity entity);

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

    /**
     * Clear all entities and remove the components from the systems. This is done sync.
     */
    void clear();
}