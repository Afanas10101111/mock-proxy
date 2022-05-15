package com.github.afanas10101111.mp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.exception.SavedFileAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class MockRuleFileService {
    public static final String FILE_EXTENSION = ".rules";
    public static final String FULL_PATH_FORMAT = "%s%s%s";

    private final MockRuleService service;
    private final ObjectMapper mapper;

    private String appFolderLocationString;
    private Path appFolderPath;

    @PostConstruct
    void appPathConstruct() {
        appFolderLocationString = MockRuleFileService.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toString()
                .replaceFirst("^jar:", "")
                .replaceFirst("[^/]+\\.jar.+$", "");
        appFolderPath = Paths.get(URI.create(appFolderLocationString));
    }

    public List<MockRule> save(String fileName) {
        try {
            List<MockRule> rules = service.getAll();
            Files.write(getFilePath(fileName), mapper.writeValueAsString(rules).getBytes(StandardCharsets.UTF_8));
            return rules;
        } catch (IOException e) {
            throw new SavedFileAccessException(e.getMessage(), e);
        }
    }

    public List<MockRule> load(String fileName) {
        try {
            List<MockRule> rules = readRulesFromFile(fileName);
            rules.forEach(r -> r.setId(null));
            service.saveAll(rules);
            return service.getAll();
        } catch (IOException e) {
            throw new SavedFileAccessException(e.getMessage(), e);
        }
    }

    public Set<String> getSavedFilesNames() {
        try (Stream<Path> stream = Files.list(appFolderPath)) {
            return stream
                    .filter(f -> !Files.isDirectory(f))
                    .map(f -> f.getFileName().toString())
                    .filter(f -> f.endsWith(FILE_EXTENSION))
                    .map(f -> f.replaceFirst(FILE_EXTENSION, ""))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new SavedFileAccessException(e.getMessage(), e);
        }
    }

    public void delete(String fileName) {
        try {
            Files.delete(getFilePath(fileName));
        } catch (IOException e) {
            throw new SavedFileAccessException(e.getMessage(), e);
        }
    }

    public void deleteAll() {
        getSavedFilesNames().forEach(this::delete);
    }

    public List<MockRule> view(String fileName) {
        try {
            return readRulesFromFile(fileName);
        } catch (IOException e) {
            throw new SavedFileAccessException(e.getMessage(), e);
        }
    }

    private Path getFilePath(String fileName) {
        return Paths.get(
                URI.create(String.format(FULL_PATH_FORMAT, appFolderLocationString, fileName, FILE_EXTENSION))
        );
    }

    private List<MockRule> readRulesFromFile(String fileName) throws IOException {
        String fileAsString = String.join("", Files.readAllLines(getFilePath(fileName), StandardCharsets.UTF_8));
        return mapper.readerFor(MockRule.class).readValues(fileAsString).readAll().stream()
                .map(MockRule.class::cast)
                .collect(Collectors.toList());
    }
}
