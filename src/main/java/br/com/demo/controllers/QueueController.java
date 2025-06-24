package br.com.demo.controllers;

import br.com.demo.services.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fila")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public ResponseEntity<Void> send(@RequestBody String mensagem) {
        queueService.sendMessage(mensagem);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> deleteQueue(@PathVariable String nome) {
        queueService.deleteQueue(nome);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nome}")
    public ResponseEntity<List<String>> getQueueMessages(@PathVariable String nome) {
        List<String> mensagens = queueService.getQueueMessages(nome);
        return ResponseEntity.ok(mensagens);
    }
}
