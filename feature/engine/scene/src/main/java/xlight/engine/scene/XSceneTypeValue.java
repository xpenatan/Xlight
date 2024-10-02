package xlight.engine.scene;

public enum XSceneTypeValue {
    /** SCENE Contains all saved data */
    SCENE(1),

    ASSETS(14),
    ENTITIES(10),
    COMPONENTS(11),
    MANAGERS(12),
    SYSTEMS(13),

    ASSET(20),
    ENTITY(21),
    COMPONENT(22),
    MANAGER(23),
    SYSTEM(24);

    private final int key;

    XSceneTypeValue(int key) {
        this.key = key;
    }

    public int getValue() {
        return key;
    }
}