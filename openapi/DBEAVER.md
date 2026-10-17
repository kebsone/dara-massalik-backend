# DBeaver - local PostgreSQL

For the Docker PostgreSQL container used by Dara Massalik, create a PostgreSQL connection with:

- Host: `localhost`
- Port: `5432`
- Database: `dara_massalik`
- Username: `postgres`
- Password: `postgres`

JDBC URL:

```text
jdbc:postgresql://localhost:5432/dara_massalik
```

Make sure Docker Desktop is running and check with:

```bash
docker compose ps
```
