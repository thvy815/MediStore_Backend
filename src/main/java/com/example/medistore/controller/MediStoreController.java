package com.example.medistore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediStoreController {

    @GetMapping("/mediStore")
    public String mediStore() {
        return "Test OK";
    }
}