package com.healthcare.dynamicMaster.v1.generator.launcher;

import com.healthcare.dynamicMaster.v1.generator.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class GeneratorLauncher {

    public static void main(String[] args) {
        SpringApplication.run(GeneratorLauncher.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(CodeGenerator codeGenerator) {
        return args -> {
            if (args.length > 0) {
                String configPath = args[0];
                log.info("Generating code from config: {}", configPath);
                codeGenerator.generateModule(configPath);
                log.info("Code generation completed!");
            } else {
                log.warn("Please provide configuration file path as argument");
                log.info("Usage: java -jar generator.jar /path/to/config.json");
            }
        };
    }
}