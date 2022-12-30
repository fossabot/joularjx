/*
 * Copyright (c) 2021-2022, Adel Noureddine, Université de Pays et des Pays de l'Adour.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the
 * GNU General Public License v3.0 only (GPL-3.0-only)
 * which accompanies this distribution, and is available at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Author : Adel Noureddine
 */

package org.noureddine.joularjx.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

/**
 * Agent properties configured by the config.properties file
 */
public class AgentProperties {

    private static final String FILTER_METHOD_NAME_PROPERTY = "filter-method-names";

    private static final String POWER_MONITOR_PATH_PROPERTY = "powermonitor-path";

    private static final String SAVE_RUNTIME_DATA_PROPERTY = "save-runtime-data";

    private static final String OVERWRITE_RUNTIME_DATA_PROPERTY = "overwrite-runtime-data";

    private static final String LOGGER_LEVEL_PROPERTY = "logger-level";

    /**
     * Loaded configuration properties
     */
    private final Properties properties;
    private final Collection<String> filterMethodNames;
    private final String powerMonitorPath;
    private final boolean saveRuntimeData;
    private final boolean overwriteRuntimeData;
    private final Level loggerLevel;

    /**
     * Instantiate a new instance which will load the properties
     */
    public AgentProperties(FileSystem fileSystem) {
        this.properties = loadProperties(fileSystem);
        this.filterMethodNames = loadFilterMethodNames();
        this.powerMonitorPath = loadPowerMonitorPath();
        this.saveRuntimeData = loadSaveRuntimeData();
        this.overwriteRuntimeData = loadOverwriteRuntimeData();
        this.loggerLevel = loadLoggerLevel();
    }

    public boolean filtersMethod(String methodName) {
        for (String filterMethod : filterMethodNames) {
            if (methodName.startsWith(filterMethod)) {
                return true;
            }
        }
        return false;
    }

    public Level getLoggerLevel() {
        return loggerLevel;
    }

    public String getPowerMonitorPath() {
        return powerMonitorPath;
    }

    public boolean savesRuntimeData() {
        return saveRuntimeData;
    }

    public boolean overwritesRuntimeData() {
        return overwriteRuntimeData;
    }

    private Properties loadProperties(FileSystem fileSystem) {
        Properties result = new Properties();

        // Read properties file
        try (InputStream input = new BufferedInputStream(Files.newInputStream(fileSystem.getPath("config.properties")))) {
            result.load(input);
        } catch (IOException e) {
            System.exit(1);
        }
        return result;
    }

    private Collection<String> loadFilterMethodNames() {
        String filterMethods = properties.getProperty(FILTER_METHOD_NAME_PROPERTY);
        if (filterMethods == null || filterMethods.isEmpty()) {
            return Collections.emptyList();
        }
        return Set.of(filterMethods.split(","));
    }

    public String loadPowerMonitorPath() {
        return properties.getProperty(POWER_MONITOR_PATH_PROPERTY);
    }

    public boolean loadSaveRuntimeData() {
        return "true".equalsIgnoreCase(properties.getProperty(SAVE_RUNTIME_DATA_PROPERTY));
    }

    public boolean loadOverwriteRuntimeData() {
        return "true".equalsIgnoreCase(properties.getProperty(OVERWRITE_RUNTIME_DATA_PROPERTY));
    }

    public Level loadLoggerLevel() {
        final String loggerLevel = properties.getProperty(LOGGER_LEVEL_PROPERTY);
        if (loggerLevel == null) {
            return Level.INFO;
        }

        final Level loggerLevelEnum;
        switch (loggerLevel) {
            case "OFF":
                loggerLevelEnum = Level.OFF;
                break;
            case "WARNING":
                loggerLevelEnum = Level.WARNING;
                break;
            case "SEVERE":
                loggerLevelEnum = Level.SEVERE;
                break;
            case "INFO":
            default:
                loggerLevelEnum = Level.INFO;
                break;
        }

        return loggerLevelEnum;
    }

}
