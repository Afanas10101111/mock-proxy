package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.MockRuleTo;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.MockRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.afanas10101111.mp.controller.MockRuleController.URL;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class MockRuleController {
    public static final String URL = "/_admin_config";
    public static final String GROUP = "/group";

    private final MockRuleService service;
    private final ModelMapper mapper;

    @GetMapping
    public List<MockRule> getAll() {
        log.info("getAll -> get all rules");
        return service.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<MockRule> add(@Valid @RequestBody MockRuleTo ruleTo) {
        log.info("add -> add rule:\n{}", ruleTo);
        service.save(mapper.map(ruleTo, MockRule.class));
        return service.getAll();
    }

    @PostMapping(value = GROUP, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<MockRule> addGroup(@RequestBody List<@Valid MockRuleTo> ruleTos) {
        log.info("addGroup -> add group:\n{}", ruleTos);
        service.saveAll(ruleTos.stream()
                .map(r -> mapper.map(r, MockRule.class))
                .collect(Collectors.toList()));
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete -> delete rule with id = {}", id);
        service.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        log.info("deleteAll -> delete all rules");
        service.deleteAll();
    }
}
