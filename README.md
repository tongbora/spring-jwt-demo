# spring-jwt

A simple Spring Boot project that shows how to generate and validate JWT tokens with Spring Security. Tokens are signed with an RSA key pair using Spring's built-in Nimbus support.

## Tech stack

- Java 17+
- Spring Boot
- Spring Security (OAuth2 Resource Server)
- Gradle
- Lombok

## Project structure

```
src/main/java/bora/tong/springjwt
├── config        # Security, JWT (RSA keys, encoder/decoder), password encoder
├── controller    # AuthController (/token, /hello)
├── dto           # LoginRequest, TokenResponse
├── exception     # GlobalExceptionHandler (401 on bad credentials)
├── security      # CustomUserDetails, CustomUserDetailsService
└── service       # AuthService, AuthServiceImpl (generates the JWT)
postman/
└── spring-jwt.postman_collection.json
```

## Run

```bash
./gradlew bootRun
```

On Windows:

```bash
gradlew.bat bootRun
```

The app starts at `http://localhost:8080`.

## Demo user

| Username | Password   | Role      |
|----------|------------|-----------|
| alice    | `password` | ROLE_USER |

The user is hardcoded in `CustomUserDetailsService`. Replace it with a database lookup for real use.

## Endpoints

| Method | Path     | Auth   | Description                    |
|--------|----------|--------|--------------------------------|
| POST   | `/token` | Public | Log in and receive a JWT       |
| GET    | `/hello` | Bearer | Returns the current user info  |

### Get a token

```bash
curl -X POST http://localhost:8080/token \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password"}'
```

Response:

```json
{
  "accessToken": "eyJraWQiOi...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Call a protected endpoint

```bash
curl http://localhost:8080/hello \
  -H "Authorization: Bearer <accessToken>"
```

Response:

```json
{
  "username": "alice",
  "roles": ["ROLE_USER"]
}
```

### Errors

Wrong credentials return `401`:

```json
{ "error": "Invalid username or password" }
```

Calling `/hello` without a token also returns `401`.

## Postman

1. Open Postman and click **Import**.
2. Select `postman/spring-jwt.postman_collection.json`.
3. Run **Get Token (login)** first. It saves the token to the `{{token}}` variable automatically.
4. Run **Hello (protected)**. It uses the saved token.

The base URL is the `baseUrl` collection variable (default `http://localhost:8080`).

## How it works

1. `POST /token` checks the username and password with `DaoAuthenticationProvider`.
2. On success, `AuthServiceImpl` builds a JWT (subject, scope, 1 hour expiry) and signs it with the RSA private key.
3. For every other request, Spring Security validates the `Authorization: Bearer ...` header using the RSA public key.
4. `JwtAuthenticationConverter` loads the user's roles from `UserDetailsService` on each request.

## Notes

- The RSA key pair is generated at startup, so all tokens become invalid when the app restarts. For production, load a fixed key from a keystore or PEM file.
- Token lifetime is hardcoded to 1 hour.
- CSRF is disabled and sessions are stateless, which is appropriate for a token-based API.