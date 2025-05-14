package com.sipa.boot.java8.common.config.config;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class Configurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configurer.class);

    /**
     * The standard separator "." that go between parts of config keys.
     */
    public static final String CONFIG_KEY_PART_SEPARATOR = ".";

    private static final String DEFAULT_CONFIG_FILE_NAME = "application.properties";

    private static CompositeConfiguration configInstance;

    private static boolean loaded = false;

    private Configurer() {
        // Add a private constructor to hide the implicit public one
    }

    public static synchronized CompositeConfiguration getInstance() {
        if (!loaded) {
            configInstance = load(DEFAULT_CONFIG_FILE_NAME);
            loaded = true;
        }
        return configInstance;
    }

    public static synchronized CompositeConfiguration getInstance(String filename) {
        if (!loaded) {
            configInstance = load(filename);
            loaded = true;
        }
        return configInstance;
    }

    public static synchronized void reset() {
        LOGGER.warn("Config is being reset, this may cause unusual app behavior");
        loaded = false;
        configInstance = null;
    }

    /**
     * Load the configuration for this Config instance. The following load procedure/configuration locations are used.
     * This is also the order in which properties will be resolved when they are requested from this Config instance.
     * <p>
     * NOTE: This method will only load the configurations once. All subsequent calls do nothing.
     * </p>
     * <UL>
     * <LI>file for "override" system property if it exists</LI>
     * <LI>System Properties</LI>
     * <LI>ENV vars</LI>
     * <LI>classpath:application.properties</LI>
     * </UL>
     */
    public static CompositeConfiguration load(String filename) {
        // an empty in memory configuration that trumps everything
        BaseConfiguration inMemoryOverrideConfiguration = new BaseConfiguration();
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration(inMemoryOverrideConfiguration);

        String overrideFilePath = System.getProperty("override");
        if (StringUtils.isNotBlank(overrideFilePath)) {
            FileConfiguration overrideConfig = loadConfiguration(findFile(overrideFilePath));
            if (overrideConfig != null) {
                FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
                // 10 sec
                strategy.setRefreshDelay(10 * 1000L);
                overrideConfig.setReloadingStrategy(strategy);
                printConfiguration("override", overrideConfig);
                compositeConfiguration.addConfiguration(overrideConfig);
            }
        }

        Configuration sysConfig = new SystemConfiguration();
        printConfiguration("sys", sysConfig);
        compositeConfiguration.addConfiguration(sysConfig);

        Configuration envConfig = new EnvironmentConfiguration();
        printConfiguration("env", envConfig);
        compositeConfiguration.addConfiguration(envConfig);

        Configuration appConfig = loadConfiguration(findFile(filename));
        if (appConfig != null) {
            printConfiguration("app", appConfig);
            compositeConfiguration.addConfiguration(appConfig);
        }

        printConfiguration("combined", compositeConfiguration);

        return compositeConfiguration;
    }

    /**
     * Internal method to lookup a file base on the name - first using the path of the file, then using the class loader
     */
    private static URL findFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw Throwables.propagate(e);
            }
        }

        return Configurer.class.getClassLoader().getResource(filename.replace("\\", "/"));
    }

    private static FileConfiguration loadConfiguration(URL loc) {
        if (loc == null) {
            return null;
        }

        try {
            return new PropertiesConfiguration(loc);
        } catch (ConfigurationException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Internal method to print the configuration used as we load the various configs
     */
    private static void printConfiguration(String type, Configuration configuration) {
        Iterator<String> iter = configuration.getKeys();
        while (iter.hasNext()) {
            String key = iter.next();
            Object value = configuration.getProperty(key);
            LOGGER.trace("{} {} = {}", type, key, value);
        }
    }

    /**
     * Get the full list of keys in this Config instance.
     *
     * @return A string iterator containing the keys of this Config instance.
     */
    public static Iterator<String> getKeys() {
        return getInstance().getKeys();
    }

    /**
     * Get the list of keys that are declared under a specific prefx in this config. For example a prefix of "login"
     * would return keys such as login.url, login.username, login.password..."
     *
     * @param prefix
     *            The prefix for which matching keys should be returned.
     * @return A string iterator with the matching keys.
     */
    public static Iterator<String> getKeys(String prefix) {
        return getInstance().getKeys(prefix);
    }

    /**
     * Return the requested config key as a boolean.
     *
     * @param key
     *            The key for which a boolean value should be returned.
     * @return The value of the key as a boolean.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a boolean.
     * @throws java.util.NoSuchElementException
     *             if the value cannot be found in the Config.
     */
    public static boolean getBoolean(String key) {
        return getInstance().getBoolean(key);
    }

    /**
     * Return the requested config key as a boolean, with a default if the value does not exist.
     *
     * @param key
     *            The key for which a boolean value should be returned.
     * @param defaultValue
     *            The default boolean value if the key is not found.
     * @return The value of the key as a boolean.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a boolean.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return getInstance().getBoolean(key, defaultValue);
    }

    /**
     * Return the requested config key as a Boolean, with a default if the value does not exist.
     *
     * @param key
     *            The key for which a Boolean value should be returned.
     * @param defaultValue
     *            The default Boolean value if the key is not found.
     * @return The value of the key as a Boolean.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a Boolean.
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return getInstance().getBoolean(key, defaultValue);
    }

    /**
     * Return the requested config key as a byte.
     *
     * @param key
     *            The key for which a byte value should be returned.
     * @return The value of the key as a byte.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a byte.
     * @throws java.util.NoSuchElementException
     *             if the value cannot be found in the Config.
     */
    public static byte getByte(String key) {
        return getInstance().getByte(key);
    }

    /**
     * Return the requested config key as a byte with a default byte value if the key is not found.
     *
     * @param key
     *            The key for which a byte value should be returned.
     * @param defaultValue
     *            The default byte value if the key is not found.
     * @return The value of the key as a byte.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a byte.
     */
    public static byte getByte(String key, byte defaultValue) {
        return getInstance().getByte(key, defaultValue);
    }

    /**
     * Return the requested config key as a Byte with a default Byte value if the key is not found.
     *
     * @param key
     *            The key for which a Byte value should be returned.
     * @param defaultValue
     *            The default Byte value if the key is not found.
     * @return The value of the key as a Byte.
     * @throws org.apache.commons.configuration.ConversionException
     *             if the value cannot be converted to a Byte.
     */
    public static Byte getByte(String key, Byte defaultValue) {
        return getInstance().getByte(key, defaultValue);
    }

    public static double getDouble(String key) {
        return getInstance().getDouble(key);
    }

    public static double getDouble(String key, double defaultValue) {
        return getInstance().getDouble(key, defaultValue);
    }

    public static Double getDouble(String key, Double defaultValue) {
        return getInstance().getDouble(key, defaultValue);
    }

    public static float getFloat(String key) {
        return getInstance().getFloat(key);
    }

    public static float getFloat(String key, float defaultValue) {
        return getInstance().getFloat(key, defaultValue);
    }

    public static Float getFloat(String key, Float defaultValue) {
        return getInstance().getFloat(key, defaultValue);
    }

    public static int getInt(String key) {
        return getInstance().getInt(key);
    }

    public static int getInt(String key, int defaultValue) {
        return getInstance().getInt(key, defaultValue);
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        return getInstance().getInteger(key, defaultValue);
    }

    public static long getLong(String key) {
        return getInstance().getLong(key);
    }

    public static long getLong(String key, long defaultValue) {
        return getInstance().getLong(key, defaultValue);
    }

    public static Long getLong(String key, Long defaultValue) {
        return getInstance().getLong(key, defaultValue);
    }

    public static short getShort(String key) {
        return getInstance().getShort(key);
    }

    public static short getShort(String key, short defaultValue) {
        return getInstance().getShort(key, defaultValue);
    }

    public static Short getShort(String key, Short defaultValue) {
        return getInstance().getShort(key, defaultValue);
    }

    public static BigDecimal getBigDecimal(String key) {
        return getInstance().getBigDecimal(key);
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return getInstance().getBigDecimal(key, defaultValue);
    }

    public static BigInteger getBigInteger(String key) {
        return getInstance().getBigInteger(key);
    }

    public static BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return getInstance().getBigInteger(key, defaultValue);
    }

    public static String getString(String key) {
        return getInstance().getString(key);
    }

    public static String getString(String key, String defaultValue) {
        return getInstance().getString(key, defaultValue);
    }

    public static String[] getStringArray(String key) {
        return getInstance().getStringArray(key);
    }

    public static List<Object> getList(String key) {
        return getInstance().getList(key);
    }

    public static List<Object> getList(String key, List<Object> defaultValue) {
        return getInstance().getList(key, defaultValue);
    }

    /**
     * Set a property on the in memory configuration of our Config object.
     * <p>
     * NOTE: This overrides any values set at any level in the config.
     */
    public static void setProperty(String key, Object value) {
        LOGGER.info("Property [{}] is being added to the top level in memory config", key);
        getInstance().getInMemoryConfiguration().setProperty(key, value);
    }

    /**
     * Unset a property on the in memory configuration of our Config object.
     * <p>
     * NOTE: This does not unset the property at other levels of the config. Those cannot be changed after they are
     * loaded.
     */
    public static void clearProperty(String key) {
        LOGGER.info("Property [{}] is being added removed from the top level in memory config", key);
        getInstance().getInMemoryConfiguration().clearProperty(key);
    }
}
