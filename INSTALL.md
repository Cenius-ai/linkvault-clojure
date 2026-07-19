# LinkVault — Installation Guide

Full step-by-step setup instructions for a clean environment.

## Prerequisites

- **Java 17** or later (OpenJDK Temurin recommended)
- **Clojure CLI** version 1.11.x or later ([installation guide](https://clojure.org/guides/install_clojure))

Verify your installation:

```bash
java -version   # should show 17+
clj --version   # should show 1.11+
```

## Step 1: Clone and enter the project

```bash
cd linkvault
```

## Step 2: Run the installer

The installer handles dependency resolution, database creation, and seeding:

```bash
bash install.sh
```

This step:
- Downloads all Clojure dependencies (Ring, Compojure, Hiccup, SQLite JDBC)
- Creates the SQLite database file (`linkvault.db`)
- Creates the schema (tables, indexes)
- Seeds 12 sample bookmarks with realistic tags

The database file is created in the project root. You can change its location by setting `DB_PATH` in your environment or `.env` file.

## Step 3: Start the development server

```bash
clj -M -m linkvault.core
```

The server binds to `0.0.0.0:3000` by default. Set the `PORT` environment variable to use a different port:

```bash
PORT=8080 clj -M -m linkvault.core
```

## Step 4: Open in your browser

Navigate to **http://localhost:3000** — you should see the bookmark list with 12 seeded bookmarks.

## Optional: Configure environment

```bash
cp .env.example .env
# Edit .env to set PORT or DB_PATH as needed
```

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `clj: command not found` | Install Clojure CLI from https://clojure.org |
| Port already in use | Set a different `PORT` or kill the existing process |
| Database locked errors | Ensure only one server instance is running; delete `linkvault.db` to start fresh |

## Production deployment

For production use, consider:
- Running behind a reverse proxy (nginx, Caddy) for TLS
- Setting up a process manager (systemd, supervisord)
- Regular backups of the SQLite database file
