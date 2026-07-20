#!/usr/bin/env bash
# LinkVault — one-command installer
# Installs dependencies, migrates database, seeds demo data.
# Run this first, then start the server with: clj -M -m linkvault.core
set -euo pipefail
cd "$(dirname "$0")"

echo "==> Installing Clojure dependencies..."
clj -P

echo "==> Initializing database and seeding sample data..."
clj -M -e "
(require 'linkvault.db)
(linkvault.db/init-schema!)
(require 'linkvault.seed)
(linkvault.seed/seed!)
(println \"Database ready.\")
"

echo ""
echo "==> Setup complete. Start the server with:"
echo "    clj -M -m linkvault.core"
echo "    → http://localhost:\${PORT:-3000}"
