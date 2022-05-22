package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.FileServiceResult;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.MockRuleFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.github.afanas10101111.mp.controller.MockRuleFileController.URL;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class MockRuleFileController {
    public static final String URL = MockRuleController.URL + "/file";
    public static final String SAVE = "/save";
    public static final String LOAD = "/load";

    private final MockRuleFileService service;

    @PostMapping(value = SAVE + "/{fileName}")
    @ResponseStatus(HttpStatus.CREATED)
    public FileServiceResult saveToFile(@PathVariable String fileName) {
        List<MockRule> saved = service.save(fileName);
        String filePathString = service.getFilePathString(fileName);
        log.info("saveToFile -> {}\nsaved to {}", saved, filePathString);
        return new FileServiceResult(FileServiceResult.Operation.SAVE, filePathString);
    }

    @GetMapping(value = LOAD + "/{fileName}")
    public FileServiceResult loadFromFile(@PathVariable String fileName) {
        List<MockRule> loaded = service.load(fileName);
        String filePathString = service.getFilePathString(fileName);
        log.info("loadFromFile -> {}\nloaded from {}", loaded, filePathString);
        return new FileServiceResult(FileServiceResult.Operation.LOAD, filePathString);
    }

    @GetMapping()
    public FileServiceResult getSavedFilesNames() {
        Set<String> savedFilesNames = service.getSavedFilesNames();
        log.info("getSavedFilesNames -> saved files names: {}", savedFilesNames);
        return new FileServiceResult(FileServiceResult.Operation.SAVED_FILES_NAMES, savedFilesNames.toString());
    }

    @GetMapping(value = "/{fileName}")
    public List<MockRule> viewFile(@PathVariable String fileName) {
        log.info("view -> show content of {}", service.getFilePathString(fileName));
        return service.view(fileName);
    }

    @DeleteMapping(value = "/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable String fileName) {
        log.info("delete -> delete {}}", service.getFilePathString(fileName));
        service.delete(fileName);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllFiles() {
        log.info("deleteAll -> delete all saved files");
        service.deleteAll();
    }
}
