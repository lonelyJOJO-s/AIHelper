package io.github.lonelyjojos.idempotent.example;

import io.github.lonelyjojos.idempotent.spring.Idempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/{requestId}")
    @Idempotent(key = "#requestId", namespace = "create-order")
    public String create(@PathVariable String requestId) {
        return "created:" + requestId;
    }
}

