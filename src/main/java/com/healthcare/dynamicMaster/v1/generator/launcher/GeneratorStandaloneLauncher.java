package com.healthcare.dynamicMaster.v1.generator.launcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.dynamicMaster.v1.generator.CodeGenerator;
import com.healthcare.dynamicMaster.v1.generator.moduler.*;
import com.healthcare.dynamicMaster.v1.generator.parser.ConfigParser;

import java.io.File;

public class GeneratorStandaloneLauncher {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Please provide configuration file path");
                System.err.println("Usage: java -jar generator.jar /path/to/config.json");
                System.exit(1);
            }

            String configPath = args[0];
            System.out.println("Generating code from config: " + configPath);

            // Initialize components manually (without Spring)
            ObjectMapper objectMapper = new ObjectMapper();
            ConfigParser configParser = new ConfigParser(objectMapper);
            EntityGenerator entityGenerator = new EntityGenerator();
            RepositoryGenerator repositoryGenerator = new RepositoryGenerator();
            DTOGenerator dtoGenerator = new DTOGenerator();
            MapperGenerator mapperGenerator = new MapperGenerator();
            ProjectionGenerator projectionGenerator = new ProjectionGenerator();
            ServiceGenerator serviceGenerator = new ServiceGenerator();
            ControllerGenerator controllerGenerator = new ControllerGenerator();

            CodeGenerator codeGenerator = new CodeGenerator(
                    configParser, entityGenerator, repositoryGenerator,
                    dtoGenerator, mapperGenerator, projectionGenerator,
                    serviceGenerator, controllerGenerator
            );

            codeGenerator.generateModule(configPath);
            System.out.println("Code generation completed successfully!");

        } catch (Exception e) {
            System.err.println("Error during code generation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}