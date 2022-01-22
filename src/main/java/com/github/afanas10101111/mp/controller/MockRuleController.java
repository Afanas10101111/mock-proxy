package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.MockRuleTo;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.repository.MockRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/_admin_config", produces = MediaType.APPLICATION_JSON_VALUE)
public class MockRuleController {
    private final MockRuleRepository repository;
    private final ModelMapper mapper;

    @GetMapping
    public List<MockRule> getAll() {
        log.info("getAll -> get all rules");
        return repository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<MockRule> add(@RequestBody MockRuleTo ruleTo) {
        log.info("add -> add rule:\n{}", ruleTo);
        repository.save(mapper.map(ruleTo, MockRule.class));
        return repository.findAll();
    }

    @PostMapping(value = "/group", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<MockRule> addGroup(@RequestBody List<MockRuleTo> ruleTos) {
        log.info("addGroup -> add group:\n{}", ruleTos);
        repository.saveAll(ruleTos.stream()
                .map(r -> mapper.map(r, MockRule.class))
                .collect(Collectors.toList()));
        return repository.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete -> delete rule with id = {}", id);
        repository.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        log.info("deleteAll -> delete all rules");
        repository.deleteAll();
    }
}
