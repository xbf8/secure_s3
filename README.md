## SS3
Secure S3 (SS3): a small service written in Java with sprig-boot uses to receive files and save
them to S3-compatible storage after they been encrypted, you can plug this service along with your
running system and use the APIs to create and manage the upload and download sessions.

Example use case: You have a heavy system need to deal with big and heavy amount of files with +5GB
file sizes, in this case you must use some tricks to receive and process these files and in the
main time you need to save them to an S3 storage, this where SS3 comes in the place.

Features:
- Manage big files (+5GB).
- File chunking based on the file size and some settings.
- Upload the files as chunks.
- Encrypt the file chunk using Java streams.
- Using the best Key Management System (Openbao).
- Save and manage the file chunks in a S3-compatible system (Rustfs).
- Download and Decrypt the file chunks using streams.
- Every connection must be authorized by session.

Security:
- Using Data Encryption Key (DEK) with Key Encryption Key (KEK) architecture.
- Using the AES-256 GCM encryption algorithm with 12 bytes Initialization Vector (IV) and 128-bit
    tag.
- Key Encryption Key (KEK) never leaves the Key Management System (KMS).
- Data Encryption Key (DEK) encrypted by the master key (KEK) and store with the chunk metadata.
- You can and you should put the KMS in a Demilitarized Zone (DMZ).
- You can and you should put a revers proxy in front of the Session Management APIs.
- Upload/Download sessions stored in in-memory storage (Redis).

SS3 needs:
- Database connection (PostgreSQL) to be able to manage the files metadata.
- KMS (Openbao) to manage the KEKs.
- Object Storage with S3-compatibility (Rustfs) to save and manage the files chunks.
- In-memory Storage (Redis) to save and manage the sessions.

## Pre-configuration
### Configuring Openbao
Before you run SS3 you must install and configure the Openbao, this section will tall you what you
need to do to run Openbao using Docker Compose, for more advance configurations please see
[Openbao Documentation](https://openbao.org/docs/configuration/).

In docker compose file (see [SS3 Docker Compose example](./docker-compose.dev.yml)) you need to put:
```yml
services:
  openbao:
    image: openbao/openbao:latest
    container_name: openbao
    user: "0:0"
    ports:
      - "8200:8200"
    volumes:
      - ./openbao-config:/openbao/config
      - ./volume/openbao:/openbao/file
    environment:
      VAULT_ADDR: http://0.0.0.0:8200
    command: server -config=/openbao/config/openbao.hcl
    restart: unless-stopped
```
this configurations uses the `volume` folder and a config file named `openbao.hcl`, example for the
config file `openbao.hcl`, see [SS3 Openbao.hcl](./openbao-config/openbao.hcl). After that you need
to set some settings to make the service available and to get the tokens for it.

After running the Openbao service using docker compose:
- Init the service, make sure to set the address based on your usage, here we using it locally:
```bash
docker exec -it openbao bao operator init -address=http://127.0.0.1:8200
```
This command will print 5 Unseal Key and one Initial Root Token. Please save them in a secure place.

> [!NOTE]
> The 5 keys uses to open the Openbao database and Openbao require 3 of the 5 keys to open it, so
if you lose 2 is ok! But if you lose 3 keys you will not be able to open the database.

- Login to the authorize the CLI:
```bash
sudo docker exec -it openbao bao login -address=http://127.0.0.1:8200 <INITIAL-ROOT-TOKEN>
```

- Enable the transit service:
```bash
docker exec -it openbao bao secrets enable -address=http://127.0.0.1:8200 transit
```

- Create a transit key as AES-256 GCM-96 (The KEK):
```bash
docker exec -it openbao bao write -f transit/keys/ss3 type=aes256-gcm96 -address=http://127.0.0.1:8200
```

- Unseal the database:
```bash
docker exec -it openbao bao operator unseal -address=http://127.0.0.1:8200
```
> [!NOTE]
> You need to run that command 3 times and to enter one different key from the 5 keys you saved.
Also note that you need to run this command every time the service stop.

### Configuring Rustfs
[Rustfs](https://docs.rustfs.com/en) is an S3-compatible object storage, we use it to save the and
manage the files, here is an example of a docker compose file (see [SS3 Docker Compose example](./docker-compose.dev.yml)):
```yml
services:
  perms:
    image: alpine
    user: root
    volumes:
      - ./volume/rustfs/data:/fix_path/rustfs-data
    command: chown -R 10001:10001 /fix_path/rustfs-data
    restart: "no"

  rustfs:
    image: rustfs/rustfs:latest
    container_name: rustfs
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - ./volume/rustfs/data:/data
    environment:
      - RUSTFS_ACCESS_KEY=your_strong_access_key
      - RUSTFS_SECRET_KEY=your_strong_secret_key
      - RUSTFS_CONSOLE_ENABLE=true
      - RUSTFS_VOLUMES=/data
    restart: unless-stopped
    depends_on:
      perms:
        condition: service_completed_successfully
```
> [!NOTE]
> Permissions for the volume folder must be set, that is the job of the `perms` service. Make sure also
to set the `RUSTFS_ACCESS_KEY` and `RUSTFS_SECRET_KEY` to a strong tokens.

After the service is started you must go to the web shell of it by visiting the port `9001` and
create new bucket with the name `ss3`.

### Configuring PostgreSQL, Redis and running the Migrations
We use docker compose heavily to run and manage the services, for that we will show you how to run
the these services using docker compose (see [SS3 Docker Compose example](./docker-compose.dev.yml) for full example):
- To run PostgreSQL:
```yml
services:
  postgres:
    image: postgres:18-alpine
    environment:
      POSTGRES_USER: secure_s3
      POSTGRES_PASSWORD: Password123
      POSTGRES_DB: secure_s3
    ports:
      - "5432:5432"
    volumes:
      - ./volume/database:/var/lib/postgresql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U secure_s3 -d secure_s3"]
      interval: 5s
      timeout: 5s
      retries: 5
```
> [!NOTE]
> Make sure to change the database settings to use your main database or any database you are using.
The tables that SS3 uses are only 2 and start with `ss3_`.

- Running the migrations (see [Migration Folder](./migrations/) for more):
```yml
services:
  migrate:
    image: migrate/migrate
    volumes:
      - ./migrations:/migrations:ro
    command:
      - "-source=file:///migrations"
      - "-database=postgresql://secure_s3:Password123@postgres:5432/secure_s3?sslmode=disable"
      - "up"
    depends_on:
      postgres:
        condition: service_healthy
    restart: "no"
```
Make sure to set the database to your own database. If you do not want to run the migrations
with this way full free to run them as you want but make sure to run them before running the SS3.

- Running Redis:
```yml
services:
  redis:
    image: redis:8.2.4-alpine
    ports:
      - "6379:6379"
    volumes:
      - ./volume/redis:/data
    environment:
      REDIS_ARGS: --requirepass Password123 --appendonly yes
```
Make sure to change the password to secure your Redis connection.

## Run SS3
### From the source code
- You need `Java 25`.
- You need `make`.
- You need `docker`.
- Run `docker compose -f ./docker-compose.dev.yml up`
- Configure Openbao see [configuring Openbao for SS3](#configuring-openbao).
- Configure Rustfs see [configuring Rustfs for SS3](#configuring-rustfs).
- Set the `.env` file, see [SS3 env file example](./.env.example).
- Run `make run`.
