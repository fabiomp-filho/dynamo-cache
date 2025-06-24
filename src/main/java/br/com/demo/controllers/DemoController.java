package br.com.demo.controllers;

import br.com.demo.domain.dtos.DemoSaveDTO;
import br.com.demo.domain.entities.Demo;
import br.com.demo.services.DemoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody DemoSaveDTO demo) {
        demoService.save(demo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demo> getAll(@PathVariable UUID id) {
        return ResponseEntity.ok(demoService.getById(id));
    }

}
