# OpenAPI

Start the backend first, then export the live OpenAPI document:

```bash
curl --fail http://localhost:8080/v3/api-docs -o openapi/openapi.json
```

Swagger UI is available at:

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

The authentication contract is `POST /api/auth/login` and returns a JWT bearer token.
