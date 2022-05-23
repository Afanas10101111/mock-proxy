package com.github.afanas10101111.mp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.afanas10101111.mp.dto.ErrorTo;
import com.github.afanas10101111.mp.dto.FileServiceResult;
import com.github.afanas10101111.mp.service.MockRuleFileService;
import com.github.afanas10101111.mp.service.exception.SavedFileAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockRuleFileController.class)
@MockBean(MockRuleFileService.class)
class MockRuleFileControllerTest extends ControllerTest {
    private static final String URL = MockRuleFileController.URL + '/';
    private static final String SAVE = MockRuleFileController.SAVE + '/';
    private static final String LOAD = MockRuleFileController.LOAD + '/';
    private static final String FILE_NAME = "savedOne";
    private static final String FILE_PATH = "/dir/savedOne.rules";
    private static final String ERROR = "Error!!!";
    private static final Set<String> SAVED_FILES_NAMES = new HashSet<>(Arrays.asList("one", "two"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRuleFileService serviceMock;

    @BeforeEach
    void serviceSetup() {
        super.mockMvc = mockMvc;
        Mockito.when(serviceMock.getFilePathString(FILE_NAME)).thenReturn(FILE_PATH);
    }

    @Test
    void saveToFileShouldReturnCorrectResult() throws Exception {
        Mockito.when(serviceMock.save(FILE_NAME)).thenReturn(RULES);
        checkPost(getJsonResult(FileServiceResult.Operation.SAVE, FILE_PATH));
    }

    @Test
    void loadFromFileShouldReturnCorrectResult() throws Exception {
        Mockito.when(serviceMock.load(FILE_NAME)).thenReturn(RULES);
        checkGet(URL + LOAD + FILE_NAME, getJsonResult(FileServiceResult.Operation.LOAD, FILE_PATH));
    }

    @Test
    void getSavedFilesNamesShouldReturnNamesSet() throws Exception {
        Mockito.when(serviceMock.getSavedFilesNames()).thenReturn(SAVED_FILES_NAMES);
        checkGet(URL, getJsonResult(FileServiceResult.Operation.SAVED_FILES_NAMES, SAVED_FILES_NAMES.toString()));
    }

    @Test
    void viewFileShouldReturnFileContent() throws Exception {
        Mockito.when(serviceMock.view(FILE_NAME)).thenReturn(RULES);
        checkGet(URL + FILE_NAME, MAPPER.writeValueAsString(RULES));
    }

    @Test
    void deleteFileShouldNotReturnContent() throws Exception {
        checkDelete(URL + FILE_NAME);
    }

    @Test
    void deleteAllFilesShouldNotReturnContent() throws Exception {
        checkDelete(URL);
    }

    @Test
    void shouldReturnErrorOnServiceError() throws Exception {
        IOException cause = new IOException(ERROR);
        Mockito.when(serviceMock.getSavedFilesNames()).thenThrow(new SavedFileAccessException(cause));
        checkPostOrGet(
                MockMvcRequestBuilders.get(URL),
                status().isBadRequest(),
                getJsonError(ErrorTo.ErrorType.FILE, getErrorToReason(cause))
        );
    }

    private void checkPost(String json) throws Exception {
        checkPostOrGet(MockMvcRequestBuilders.post(URL + SAVE + FILE_NAME), status().isCreated(), json);
    }

    private void checkGet(String url, String json) throws Exception {
        checkPostOrGet(MockMvcRequestBuilders.get(url), status().isOk(), json);
    }

    private String getJsonResult(FileServiceResult.Operation operation, String file) throws JsonProcessingException {
        return MAPPER.writeValueAsString(new FileServiceResult(operation, file));
    }

    private String getErrorToReason(Throwable cause) {
        return String.format("%s: %s", cause.getClass().getSimpleName(), ERROR);
    }
}
