package com.optical.manage.controller;

import com.optical.manage.dto.user.UserRequest;
import com.optical.manage.DO.User;
import com.optical.manage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());

        Long id = userService.createUser(user);
        if (null == id || id <= 0) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("ok", false, "message", "创建用户失败")));
        }
        return Mono.just(ResponseEntity.ok(Map.of("ok", true, "userId", id)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (null == user) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.ok(user));
    }

    @GetMapping("/name/{name}")
    public Mono<ResponseEntity<User>> getUserByName(@PathVariable String name) {
        User user = userService.getUserByName(name);
        if (null == user) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.ok(user));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        User user = userService.getUserById(id);
        if (null == user) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        user.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());

        boolean success = userService.updateUser(user);
        return Mono.just(ResponseEntity.ok(Map.of("ok", success)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return Mono.just(ResponseEntity.ok(Map.of("ok", success)));
    }

    @PostMapping("/validate-password")
    public Mono<ResponseEntity<Map<String, Object>>> validatePassword(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String password = request.get("password");

        User user = userService.getUserByName(name);
        if (null == user) {
            return Mono.just(ResponseEntity.ok(Map.of("ok", false, "message", "用户不存在")));
        }

        boolean valid = userService.validatePassword(password, user.getPassword());
        return Mono.just(ResponseEntity.ok(Map.of("ok", valid)));
    }
}
