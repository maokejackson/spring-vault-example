Sample code to manage secrets using `spring-cloud-starter-vault-config`, with HashiCorp Vault (or OpenBao) as the
vault backend.

How to Run
====================

1. Start vault backend (refer to next section).
2. Start Spring Boot application: `mvn spring-boot:run`.

Start Vault Backend
--------------------

### HashiCorp Vault

#### Linux

```shell
# Download & Install
wget -O vault.zip https://releases.hashicorp.com/vault/1.18.5/vault_1.18.5_linux_amd64.zip
unzip -p vault.zip vault >vault
chmod +x vault
rm -f vault.zip

# Start Server
./vault server -dev -dev-root-token-id="00000000-0000-0000-0000-000000000000"

# Set Environment Variables
set VAULT_TOKEN=00000000-0000-0000-0000-000000000000
set VAULT_ADDR=http://127.0.0.1:8200/

# Create Secret Engine
./vault secrets enable -path spring -version 2 kv
```

#### Windows

```powershell
# Download & Install
$ProgressPreference = "SilentlyContinue"
Invoke-WebRequest -Uri "https://releases.hashicorp.com/vault/1.18.5/vault_1.18.5_windows_amd64.zip" -OutFile "vault.zip"
Expand-Archive -Path "vault.zip"
Move-Item -Path "vault\vault.exe"
Remove-Item -Path "vault" -Force -Recurse
Remove-Item -Path "vault.zip" -Force
```

```bat
# Start Server
vault.exe server -dev -dev-root-token-id="00000000-0000-0000-0000-000000000000"

# Set Environment Variables
set VAULT_TOKEN=00000000-0000-0000-0000-000000000000
set VAULT_ADDR=http://127.0.0.1:8200/

# Create Secret Engine
vault.exe secrets enable -path spring -version 2 kv
```

### OpenBao

#### Linux

```sh
# Download & Install
wget -O bao.tar.gz https://github.com/openbao/openbao/releases/download/v2.1.1/bao_2.1.1_Linux_x86_64.tar.gz
tar -zxvf bao.tar.gz bao
chmod +x bao
rm -f bao.tar.gz

# Start Server
./bao server -dev -dev-root-token-id="00000000-0000-0000-0000-000000000000"

# Set Environment Variables
set VAULT_TOKEN=00000000-0000-0000-0000-000000000000
set VAULT_ADDR=http://127.0.0.1:8200/

# Create Secret Engine
./bao secrets enable -path spring -version 2 kv
```

#### Windows

```powershell
# Download & Install
$ProgressPreference = 'SilentlyContinue'
Invoke-WebRequest -Uri "https://github.com/openbao/openbao/releases/download/v2.1.1/bao_2.1.1_Windows_x86_64.zip" -OutFile "bao.zip"
Expand-Archive -Path "bao.zip"
Move-Item -Path "bao\bao.exe"
Remove-Item -Path "bao" -Force -Recurse
Remove-Item -Path "bao.zip" -Force
```

```bat
# Start Server
bao.exe server -dev -dev-root-token-id="00000000-0000-0000-0000-000000000000"

# Set Environment Variables
set VAULT_TOKEN=00000000-0000-0000-0000-000000000000
set VAULT_ADDR=http://127.0.0.1:8200/

# Create Secret Engine
bao.exe secrets enable -path spring -version 2 kv
```

Rest Endpoints
--------------------

Available at http://localhost:8080/swagger-ui/index.html once the server is started.
