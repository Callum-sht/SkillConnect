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
