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
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Agent properties configured by the config.properties file
 */
public class AgentProperties {

    private static final Logger logger = JoularJXLogging.getLogger();

    //Properties names in the config.properties file
    private static final String FILTER_METHOD_NAME_PROPERTY = "filter-method-names";
    private static final String POWER_MONITOR_PATH_PROPERTY = "powermonitor-path";
    private static final String SAVE_RUNTIME_DATA_PROPERTY = "save-runtime-data";
    private static final String OVERWRITE_RUNTIME_DATA_PROPERTY = "overwrite-runtime-data";
    private static final String LOGGER_LEVEL_PROPERTY = "logger-level";
    private static final String TRACK_CONSUMPTION_EVOLUTION_PROPERTY = "track-consumption-evolution";
    private static final String EVOLUTION_DATA_PATH_PROPERTY = "evolution-data-path";

    /**
     * Loaded configuration properties
     */
    private final Properties properties;
    private final Collection<String> filterMethodNames;
    private final String powerMonitorPath;
    private final boolean saveRuntimeData;
    private final boolean overwriteRuntimeData;
    private final Level loggerLevel;
    private final boolean consumptionEvolution;
    private final String evolutionDataPath;

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
        this.consumptionEvolution = loadConsumptionEvolution();
        this.evolutionDataPath = loadEvolutionDataPath();
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

    public boolean trackConsumptionEvolution() {
        return consumptionEvolution;
    }

    public String getEvolutionDataPath() {
        return this.evolutionDataPath;
    }

    private Properties loadProperties(FileSystem fileSystem) {
        Properties result = new Properties();

        // Read properties file
        try (InputStream input = new BufferedInputStream(Files.newInputStream(getPropertiesPathIfExists(fileSystem)))) {
            result.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load properties: \"{0}\"", e.getMessage());
            logger.throwing(getClass().getName(), "loadProperties", e);
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
        return Boolean.parseBoolean(properties.getProperty(SAVE_RUNTIME_DATA_PROPERTY));
    }

    public boolean loadOverwriteRuntimeData() {
        return Boolean.parseBoolean(properties.getProperty(OVERWRITE_RUNTIME_DATA_PROPERTY));
    }

    public Level loadLoggerLevel() {
        String property = properties.getProperty(LOGGER_LEVEL_PROPERTY);
        if (property == null) {
            return Level.INFO;
        }

        try {
            return Level.parse(property);
        } catch (IllegalArgumentException exception) {
            return Level.INFO;
        }
    }

    public boolean loadConsumptionEvolution() {
        return Boolean.parseBoolean(properties.getProperty(TRACK_CONSUMPTION_EVOLUTION_PROPERTY));
    }

    public String loadEvolutionDataPath() {
        String property = properties.getProperty(EVOLUTION_DATA_FILEPATH_PROPERTY);
        if (property == null){
            //Default path
            return "evolution";
        }
        return property;
    }

    private Path getPropertiesPathIfExists(FileSystem fileSystem) {
        Path path = fileSystem.getPath("config.properties");

        if (Files.notExists(path)) {
            logger.log(Level.SEVERE, "Could not locate config.properties");
            System.exit(1);
        }

        return path;
    }
}
