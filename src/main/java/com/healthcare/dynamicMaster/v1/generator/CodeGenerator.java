package com.healthcare.dynamicMaster.v1.generator;

import com.healthcare.dynamicMaster.v1.generator.config.ModuleConfig;
import com.healthcare.dynamicMaster.v1.generator.moduler.*;
import com.healthcare.dynamicMaster.v1.generator.parser.ConfigParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final ConfigParser configParser;
    private final EntityGenerator entityGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final DTOGenerator dtoGenerator;
    private final MapperGenerator mapperGenerator;
    private final ProjectionGenerator projectionGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ControllerGenerator controllerGenerator;

    private static final String BASE_PATH = "src/main/java/";

    public void generateModule(String configPath) throws Exception {
        log.info("Starting code generation for module from config: {}", configPath);

        ModuleConfig config = configParser.parseConfig(configPath);

        // Generate all components
        generateEntity(config);
        generateRepository(config);
        generateDTOs(config);
        generateProjection(config);
        generateMapper(config);
        generateServices(config);
        generateController(config);

        log.info("Code generation completed successfully for module: {}", config.getModuleName());
    }

    private void generateEntity(ModuleConfig config) throws IOException {
        String content = entityGenerator.generate(config);
        String filePath = getEntityPath(config);
        writeToFile(filePath, content);
        log.info("Generated Entity: {}", filePath);
    }

    private void generateRepository(ModuleConfig config) throws IOException {
        String content = repositoryGenerator.generate(config);
        String filePath = getRepositoryPath(config);
        writeToFile(filePath, content);
        log.info("Generated Repository: {}", filePath);
    }

    private void generateDTOs(ModuleConfig config) throws IOException {
        // Request DTO
        String requestDtoContent = dtoGenerator.generateRequestDTO(config);
        String requestDtoPath = getRequestDTOPath(config);
        writeToFile(requestDtoPath, requestDtoContent);
        log.info("Generated Request DTO: {}", requestDtoPath);

        // Response DTO
        String responseDtoContent = dtoGenerator.generateResponseDTO(config);
        String responseDtoPath = getResponseDTOPath(config);
        writeToFile(responseDtoPath, responseDtoContent);
        log.info("Generated Response DTO: {}", responseDtoPath);
    }

    private void generateProjection(ModuleConfig config) throws IOException {
        String content = projectionGenerator.generate(config);
        String filePath = getProjectionPath(config);
        writeToFile(filePath, content);
        log.info("Generated Projection: {}", filePath);
    }

    private void generateMapper(ModuleConfig config) throws IOException {
        String content = mapperGenerator.generate(config);
        String filePath = getMapperPath(config);
        writeToFile(filePath, content);
        log.info("Generated Mapper: {}", filePath);
    }

    private void generateServices(ModuleConfig config) throws IOException {
        // Service Interface
        String interfaceContent = serviceGenerator.generateInterface(config);
        String interfacePath = getServiceInterfacePath(config);
        writeToFile(interfacePath, interfaceContent);
        log.info("Generated Service Interface: {}", interfacePath);

        // Service Implementation
        String implContent = serviceGenerator.generateImpl(config);
        String implPath = getServiceImplPath(config);
        writeToFile(implPath, implContent);
        log.info("Generated Service Implementation: {}", implPath);
    }

    private void generateController(ModuleConfig config) throws IOException {
        String content = controllerGenerator.generate(config);
        String filePath = getControllerPath(config);
        writeToFile(filePath, content);
        log.info("Generated Controller: {}", filePath);
    }

    private void writeToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.write(content);
        }
    }

    // Path helper methods
    private String getEntityPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/entity/" + getEntityName(config) + ".java";
    }

    private String getRepositoryPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/repository/" + getEntityName(config) + "Repository.java";
    }

    private String getRequestDTOPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/dto/request/" + getEntityName(config) + "RequestDTO.java";
    }

    private String getResponseDTOPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/dto/response/" + getEntityName(config) + "ResponseDTO.java";
    }

    private String getProjectionPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/dto/projection/" + getEntityName(config) + "Projection.java";
    }

    private String getMapperPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/mapper/" + getEntityName(config) + "Mapper.java";
    }

    private String getServiceInterfacePath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/service/" + getEntityName(config) + "Service.java";
    }

    private String getServiceImplPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/service/impl/" + getEntityName(config) + "ServiceImpl.java";
    }

    private String getControllerPath(ModuleConfig config) {
        return BASE_PATH + packageToPath(config.getPackageName()) + "/controller/" + getEntityName(config) + "Controller.java";
    }

    private String packageToPath(String packageName) {
        return packageName.replace('.', '/');
    }

    private String getEntityName(ModuleConfig config) {
        String[] parts = config.getModuleName().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}