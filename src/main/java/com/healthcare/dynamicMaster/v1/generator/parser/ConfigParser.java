package com.healthcare.dynamicMaster.v1.generator.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
public class ConfigParser {

    private final ObjectMapper objectMapper;

    public ConfigParser() {
        this.objectMapper = new ObjectMapper();
    }

    public ModuleConfig parseConfig(String configPath) throws Exception {
        log.info("Parsing configuration from: {}", configPath);
        File configFile = new File(configPath);
        if (configFile.exists()) {
            return objectMapper.readValue(configFile, ModuleConfig.class);
        }

        // Try to load from classpath
        InputStream inputStream = getClass().getResourceAsStream(configPath);
        if (inputStream != null) {
            return objectMapper.readValue(inputStream, ModuleConfig.class);
        }

        throw new IllegalArgumentException("Config file not found: " + configPath);
    }
}