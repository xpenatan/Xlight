package xlight.engine.core.ecs;

public interface XPreferencesManager {

    /**
     * One time call to initialize the Preference manager.
     */
    void setup(String key);

    /**
     * Put a property data to memory. Need to be saved to be permanent.
     */
    void putValue(String section, String key, String value);

    /**
     * Get a property data from memory.
     */
    String getValue(String section, String key, String defaultValue);

    /**
     * Put a property data to memory. Need to be saved to be permanent.
     */
    void putValue(String section, String key, boolean value);

    /**
     * Get a property data from memory.
     */
    boolean getValue(String section, String key, boolean defaultValue);

    /**
     * Put a property data to memory. Need to be saved to be permanent.
     */
    void putValue(String section, String key, int value);

    /**
     * Get a property data from memory.
     */
    int getValue(String section, String key, int defaultValue);

    /**
     * Put a property data to memory. Need to be saved to be permanent.
     */
    void putValue(String section, String key, float value);

    /**
     * Get a property data from memory.
     */
    float getValue(String section, String key, float defaultValue);

    /**
     * Save all memory properties to editor file.
     */
    void save();

    /**
     * Load all properties from file to memory. Memory will be cleared before loading.
     */
    void load();
}