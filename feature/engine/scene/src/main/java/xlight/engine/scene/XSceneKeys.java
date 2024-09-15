package xlight.engine.scene;

public enum XSceneKeys {
    SCENE_TYPE(31),
    CLASS(32),
    DATA(33),
    NAME(34),
    SCENE_ID(35),
    ENABLE(36),
    VISIBLE(37),
    ENABLE_FORCE(38),
    TAG(39),
    JSON_ID(40),
    SYSTEMS(51),
    MANAGERS(52),
    ENTITIES(53),
    COMPONENTS(54),
    CLASS_DEBUG_NAME(99999);

    private final int key;

    XSceneKeys(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}