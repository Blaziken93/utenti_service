# utenti-service

Microservizio per la gestione degli utenti, dei ruoli e dei gruppi. Fa parte di un sistema fullstack a microservizi e si occupa di tutto ciò che riguarda l'autenticazione e l'organizzazione delle persone all'interno della piattaforma.

---

## Cosa fa

- Registrazione e login degli utenti con JWT
- Gestione del ruolo di ogni utente (ADMIN, MANAGER, RECEPTION, USER, GUEST)
- Creazione e gestione di gruppi
- Assegnazione degli utenti ai gruppi

---

## Tecnologie usate

| Cosa | Tecnologia |
|---|---|
| Linguaggio | Java 17 |
| Framework | Spring Boot 4 |
| Database | PostgreSQL 16 |
| Autenticazione | JWT (JSON Web Token) |
| ORM | Spring Data JPA / Hibernate |
| Build tool | Maven |
| Containerizzazione DB | Docker / Docker Compose |

---

## Prerequisiti

Prima di avviare il progetto assicurati di avere installato:

- **Java 17** — `java -version`
- **Maven** — `mvn -version` (oppure usa il wrapper `./mvnw` incluso nel progetto)
- **Docker Desktop** — per avviare il database PostgreSQL

---

## Configurazione

Il servizio legge la configurazione dalle **variabili d'ambiente**. I segreti non sono hardcoded nel codice.

### 1. Crea il file `.env`

Copia il file di esempio e compilalo con i tuoi valori:

```bash
cp .env.example .env
```

Contenuto del `.env`:

```env
JWT_SECRET=cambia-con-una-chiave-sicura-di-almeno-32-caratteri
JWT_EXPIRATION=86400000

DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USERNAME=utenti_user
DB_PASSWORD=cambia-con-password-sicura
```

> Il file `.env` non viene committato su Git (è nel `.gitignore`). Non condividerlo mai.

---

## Avvio

### Passo 1 — Avvia il database

```bash
docker-compose up -d
```

Questo avvia un container PostgreSQL sulla porta `5432` e inizializza automaticamente il database con lo schema (`init.sql`).

Per verificare che sia partito:

```bash
docker ps
```

Dovresti vedere il container `utenti_postgres` in stato `Up`.

### Passo 2 e 3 — Carica le variabili d'ambiente e avvia il servizio

Il modo più comodo è un solo comando che legge il `.env` e avvia l'app insieme:

**Su Linux/macOS/Git Bash:**
```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

**Su Windows (PowerShell):**
```powershell
Get-Content .env | Where-Object { $_ -match '=' } | ForEach-Object { $n,$v = $_.split('=',2); [System.Environment]::SetEnvironmentVariable($n,$v) }; ./mvnw spring-boot:run
```

**Con IntelliJ IDEA:**
Apri la Run Configuration → tab "Environment variables" → inserisci tutte le variabili del `.env` → da quel momento avvii normalmente con il tasto Play.

Il servizio sarà disponibile su: `http://localhost:8081`

---

## Struttura del progetto

```
src/main/java/it/exprivia/utenti/
├── config/
│   └── SecurityConfig.java        # Configurazione Spring Security e CORS
├── controller/
│   ├── AuthController.java        # Endpoint login e registrazione
│   ├── UtenteController.java      # Endpoint gestione utenti
│   └── GruppoController.java      # Endpoint gestione gruppi
├── dto/
│   ├── LoginRequest.java          # Corpo della richiesta di login
│   ├── LoginResponse.java         # Risposta con il token JWT
│   ├── RegisterRequest.java       # Corpo della richiesta di registrazione
│   └── UtenteDTO.java             # Rappresentazione pubblica di un utente
├── entity/
│   ├── Utente.java                # Tabella utenti
│   ├── Gruppo.java                # Tabella gruppi
│   ├── GruppoUtente.java          # Tabella di join utenti-gruppi
│   └── RuoloUtente.java           # Enum dei ruoli
├── exception/
│   └── GlobalExceptionHandler.java # Gestione centralizzata degli errori
├── repository/                    # Interfacce JPA per l'accesso al DB
├── security/
│   ├── JwtAuthFilter.java         # Filtro che valida il token ad ogni richiesta
│   └── JwtUtils.java              # Genera e legge i token JWT
└── service/
    ├── AuthService.java           # Logica di registrazione e login
    ├── UtenteService.java         # Logica di gestione utenti
    └── GruppoService.java         # Logica di gestione gruppi
```

---

## API Reference

La porta di base è `8081`. Tutti gli endpoint iniziano con `/api`.

---

### Autenticazione

Questi endpoint sono pubblici — non richiedono token.

#### `POST /api/auth/register` — Registra un nuovo utente

**Request body:**
```json
{
  "fullName": "Mario Rossi",
  "email": "mario.rossi@exprivia.com",
  "password": "password123",
  "ruolo": "USER"
}
```

**Regole di validazione:**
- `fullName` — obbligatorio, max 50 caratteri
- `email` — obbligatoria, deve rispettare il formato `nome.cognome@exprivia.com`
- `password` — obbligatoria, minimo 8 caratteri
- `ruolo` — opzionale, se non specificato viene assegnato `USER`

**Risposta `200 OK`:**
```json
{
  "id": 1,
  "fullName": "Mario Rossi",
  "email": "mario.rossi@exprivia.com",
  "ruolo": "USER"
}
```

---

#### `POST /api/auth/login` — Effettua il login

**Request body:**
```json
{
  "email": "mario.rossi@exprivia.com",
  "password": "password123"
}
```

**Risposta `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> Salva questo token — va incluso in tutte le richieste successive come header `Authorization: Bearer <token>`.

---

### Come usare il token JWT

Tutte le richieste agli endpoint protetti richiedono il token nell'header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Il token scade dopo **24 ore** (configurabile con `JWT_EXPIRATION`).

---

### Utenti

Questi endpoint sono accessibili **solo agli utenti con ruolo ADMIN**.

#### `GET /api/utenti` — Lista tutti gli utenti

**Risposta `200 OK`:**
```json
[
  {
    "id": 1,
    "fullName": "Mario Rossi",
    "email": "mario.rossi@exprivia.com",
    "ruolo": "USER"
  },
  {
    "id": 2,
    "fullName": "Laura Bianchi",
    "email": "laura.bianchi@exprivia.com",
    "ruolo": "MANAGER"
  }
]
```

---

#### `GET /api/utenti/{id}` — Ottieni un utente per ID

**Risposta `200 OK`:**
```json
{
  "id": 1,
  "fullName": "Mario Rossi",
  "email": "mario.rossi@exprivia.com",
  "ruolo": "USER"
}
```

**Risposta `404 Not Found`** se l'utente non esiste.

---

#### `PUT /api/utenti/{id}` — Aggiorna un utente

**Request body:**
```json
{
  "fullName": "Mario Rossi Aggiornato",
  "email": "mario.rossi@exprivia.com",
  "ruolo": "MANAGER"
}
```

**Risposta `200 OK`** con l'utente aggiornato.

---

#### `DELETE /api/utenti/{id}` — Elimina un utente

**Risposta `204 No Content`** se eliminato con successo.

---

### Gruppi

Questi endpoint sono accessibili **solo agli utenti con ruolo ADMIN**.

#### `POST /api/gruppi?nome=NomeGruppo` — Crea un gruppo

**Esempio:**
```
POST /api/gruppi?nome=Team Backend
```

**Risposta `200 OK`:**
```json
{
  "id": 1,
  "nome": "Team Backend"
}
```

---

#### `GET /api/gruppi` — Lista tutti i gruppi

**Risposta `200 OK`:**
```json
[
  { "id": 1, "nome": "Team Backend" },
  { "id": 2, "nome": "Team Frontend" }
]
```

---

#### `POST /api/gruppi/{idGruppo}/utenti/{idUtente}` — Aggiungi un utente a un gruppo

**Esempio:**
```
POST /api/gruppi/1/utenti/3
```

**Risposta `200 OK`** se aggiunto con successo.

**Risposta `400 Bad Request`** se l'utente è già nel gruppo.

---

#### `DELETE /api/gruppi/{idGruppo}/utenti/{idUtente}` — Rimuovi un utente da un gruppo

**Esempio:**
```
DELETE /api/gruppi/1/utenti/3
```

**Risposta `204 No Content`** se rimosso con successo.

---

#### `GET /api/gruppi/{idGruppo}/utenti` — Lista gli utenti di un gruppo

**Risposta `200 OK`:**
```json
[
  {
    "id": 3,
    "fullName": "Luca Verdi",
    "email": "luca.verdi@exprivia.com",
    "ruolo": "USER"
  }
]
```

---

## Ruoli disponibili

| Ruolo | Descrizione |
|---|---|
| `ADMIN` | Accesso completo a tutte le funzionalità |
| `MANAGER` | Ruolo intermedio con permessi di gestione |
| `RECEPTION` | Ruolo per la reception |
| `USER` | Utente standard (default alla registrazione) |
| `GUEST` | Ospite con accesso limitato |

> Al momento gli endpoint sono protetti solo con il controllo `ADMIN`. Gli altri ruoli sono pronti per essere usati quando si aggiungono nuovi endpoint agli altri microservizi.

---

## Formato degli errori

Tutti gli errori restituiscono la stessa struttura JSON:

```json
{
  "timestamp": "2026-03-24T10:30:00.123",
  "status": 400,
  "errore": "Descrizione del problema"
}
```

| Codice | Quando viene restituito |
|---|---|
| `400` | Input non valido o operazione non consentita (es. email duplicata) |
| `401` | Token JWT mancante o non valido |
| `403` | Utente autenticato ma senza i permessi necessari |
| `404` | Risorsa non trovata (es. utente con quell'ID non esiste) |

---

## Database

Lo schema del database viene creato automaticamente da `init.sql` al primo avvio di Docker.

Le tabelle principali sono:

| Tabella | Descrizione |
|---|---|
| `utenti` | Contiene tutti gli utenti registrati |
| `gruppi` | Contiene i gruppi creati |
| `gruppi_utente` | Tabella di collegamento tra utenti e gruppi |

Per connetterti al database direttamente:

```bash
docker exec -it utenti_postgres psql -U utenti_user -d postgres
```

---

## Fermare il servizio

Per fermare solo il database:

```bash
docker-compose down
```

Per fermare e **cancellare anche i dati**:

```bash
docker-compose down -v
```

> Attenzione: `-v` rimuove il volume, quindi tutti i dati nel database vengono persi.
