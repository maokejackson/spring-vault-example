package com.dtxmaker.vault.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Secret")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/{engine}/{vault}")
@RestController
class SecretController {

    private final VaultTemplate vaultTemplate;

    @Operation(summary = "List all secrets in specific vault")
    @GetMapping
    Map<String, Object> listSecrets(
            @PathVariable String engine,
            @PathVariable String vault
    ) {
        return vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2).get(vault).getData();
    }

    @Operation(summary = "Get the value of a secret")
    @GetMapping("/{secretKey}")
    Object getSecret(
            @PathVariable String engine,
            @PathVariable String vault,
            @PathVariable String secretKey
    ) {
        return vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2).get(vault).getData().get(secretKey);
    }

    @Operation(summary = "Add new secret to specific vault")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    void addSecret(
            @PathVariable String engine,
            @PathVariable String vault,
            @RequestBody @Valid AddSecretBody body
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        Map<String, Object> map = operations.get(vault).getData();

        Assert.state(!map.containsKey(body.key), "Secret key already exists");

        map.putIfAbsent(body.key, body.value);
        operations.put(vault, map);
    }

    record AddSecretBody(
            @NotBlank String key,
            String value
    ) {}

    @Operation(summary = "Update the value of a secret")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{secretKey}")
    void updateSecret(
            @PathVariable String engine,
            @PathVariable String vault,
            @PathVariable String secretKey,
            @RequestBody @Valid UpdateSecretBody body
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        Map<String, Object> map = operations.get(vault).getData();

        Assert.state(map.containsKey(secretKey), "Secret key does not exist");

        map.put(secretKey, body.value);
        operations.put(vault, map);
    }

    record UpdateSecretBody(
            String value
    ) {}

    @Operation(summary = "Delete a secret from specific vault")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{secretKey}")
    void deleteSecret(
            @PathVariable String engine,
            @PathVariable String vault,
            @PathVariable String secretKey
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        Map<String, Object> map = operations.get(vault).getData();

        Assert.state(map.containsKey(secretKey), "Secret key does not exist");

        map.remove(secretKey);
        operations.put(vault, map);
    }
}
