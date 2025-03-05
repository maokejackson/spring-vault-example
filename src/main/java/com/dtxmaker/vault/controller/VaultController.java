package com.dtxmaker.vault.controller;

import java.util.HashMap;
import java.util.List;

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
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vault")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/engines/{engine}")
@RestController
class VaultController {

    private final VaultTemplate vaultTemplate;

    @Operation(summary = "List all vaults in specific engine")
    @GetMapping
    List<String> listVaults(
            @PathVariable String engine
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        return operations.list("/");
    }

    @Operation(summary = "Add new vault to specific engine")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    void addVault(
            @PathVariable String engine,
            @RequestBody @Valid AddVaultBody body
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        VaultResponse vaultResponse = operations.get(body.name);

        Assert.isNull(vaultResponse, "Vault already exists");

        operations.put(body.name, new HashMap<>());
    }

    record AddVaultBody(
            @NotBlank String name
    ) {}

    @Operation(summary = "Delete a vault from specific engine")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{vault}")
    void deleteVault(
            @PathVariable String engine,
            @PathVariable String vault
    ) {
        VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue(engine, KeyValueBackend.KV_2);
        VaultResponse vaultResponse = operations.get(vault);

        Assert.notNull(vaultResponse, "Vault does not exist");

        operations.delete(vault);
    }
}
