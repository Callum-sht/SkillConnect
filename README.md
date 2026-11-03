
# RMIT COSC2299 SEPT Major Project

# Group Information

## Group-P7-04

## Members
* Callum Shen s4060865
* Ting Rui Koh s4085675
* Yuxing Jing s4044662
* Zitong Huang s4076568
* Yihan Li s4078001


## Records

* Github repository: https://github.com/rmit-cosc2299/sept-2026-group-project-callum-sht
* Github Project Board : https://github.com/orgs/rmit-cosc2299/projects/22
* Microsoft Teams group: https://teams.microsoft.com/l/team/19%3AjeWc4vWYrGkHzQjFrXx7JzRAsma7kC73SiVn8zIkkh01%40thread.tacv2/conversations?groupId=51742ea2-29fd-4ae8-b612-bcb6f0868aa0&tenantId=d1323671-cdbe-4417-b4d4-bdb24b51316b

See [Instructions](INSTRUCTIONS.md)

## Run with Docker Compose

Prerequisites: Docker Desktop with Docker Compose.

1. Create the local environment file:

   ```bash
   cp .env.example .env
   ```

2. Replace the example passwords in `.env`.

3. Build and start the complete application:

   ```bash
   docker compose up --build -d
   ```

4. Open the frontend at <http://localhost:3000>.

The backend is available at <http://localhost:8080>. MySQL is available to
local database clients at `localhost:3307`; its database name, username and
password come from `.env`.

Check the running services and logs with:

```bash
docker compose ps
docker compose logs -f backend
```

Stop the application with:

```bash
docker compose down
```

The `mysql_data` volume keeps database data after `docker compose down`.
Running `docker compose down -v` also deletes that data volume.
