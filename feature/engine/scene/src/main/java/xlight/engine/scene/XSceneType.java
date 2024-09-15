package xlight.engine.scene;

public enum XSceneType {
    /** SCENE Contains all saved data */
    SCENE(1),

    ENTITIES(2),
    COMPONENTS(3),
    MANAGERS(4),
    SYSTEMS(5),
    ASSETS(6),

    ENTITY(7),
    COMPONENT(8),
    MANAGER(9),
    SYSTEM(10),
    ASSET(11);

    private final int key;

    XSceneType(int key) {
        this.key = key;
    }

    public int getValue() {
        return key;
    }
}