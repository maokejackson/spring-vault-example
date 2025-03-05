package com.dtxmaker.vault.controller;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultMount;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Engine")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/engines")
@RestController
class EngineController {

    private final VaultTemplate vaultTemplate;

    @Operation(summary = "List all engines")
    @GetMapping
    List<Engine> listEngines() {
        VaultSysOperations operations = vaultTemplate.opsForSys();
        return operations.getMounts().entrySet().stream().map(Engine::new).toList();
    }

    @Builder
    record Engine(
            @NonNull String path,
            @NonNull String type,
            String description,
            Map<String, Object> config,
            Map<String, String> options
    ) {

        Engine(Map.Entry<String, VaultMount> mount) {
            this(
                    mount.getKey(),
                    mount.getValue().getType(),
                    mount.getValue().getDescription(),
                    mount.getValue().getConfig(),
                    mount.getValue().getOptions()
            );
        }
    }

    @Operation(summary = "Add new engine")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    void addEngine(
            @RequestBody @Valid AddEngineBody body
    ) {
        VaultSysOperations operations = vaultTemplate.opsForSys();
        Map<String, VaultMount> mounts = operations.getMounts();

        Assert.state(!mounts.containsKey(body.path + "/"), "Engine path already exists");

        operations.mount(body.path, VaultMount.builder()
                .type("kv")
                .description(body.description)
                .options(Map.of("version", "2"))
                .build()
        );
    }

    record AddEngineBody(
            @NotBlank String path,
            String description
    ) {}

    @Operation(summary = "Delete an engine")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{engine}")
    void deleteEngine(
            @PathVariable String engine
    ) {
        VaultSysOperations operations = vaultTemplate.opsForSys();
        operations.unmount(engine);
    }
}
