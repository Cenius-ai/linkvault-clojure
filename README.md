# LinkVault — Clojure bookmark knowledge base app reference implementation

**LinkVault** is a free, open-source bookmark knowledge base app built with Clojure. Build a bookmark manager web application in Clojure using Ring/Compojure for routing, Hiccup for server-rendered HTML, and SQLite for storage. Run it locally, deploy it as a self-hosted knowledge base app, or [remix it on cenius.ai](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure) to make it your own — the whole application (code, design, seeded demo data) ships in this repository under the MIT license.

[![License: MIT](https://img.shields.io/badge/License-MIT-16b881.svg)](LICENSE) ![Stack](https://img.shields.io/badge/Stack-Clojure-3b82f6) [![Built with cenius.ai](https://img.shields.io/badge/Built%20with-cenius.ai-8b5cf6)](https://cenius.ai)

## Demo

![LinkVault — bookmark knowledge base app](.github/media/poster.png)

![LinkVault demo — bookmark knowledge base app built with Clojure](.github/media/hero_flagship.gif)

▶ **[Watch the full demo video](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure)** — the complete walkthrough, playing on the project's cenius.ai page · [MP4 file](.github/media/demo.mp4)

## Screenshots

<img src=".github/media/shot-1.png" width="32%" alt="LinkVault knowledge base app screenshot 1"/> <img src=".github/media/shot-2.png" width="32%" alt="LinkVault knowledge base app screenshot 2"/> <img src=".github/media/shot-3.png" width="32%" alt="LinkVault knowledge base app screenshot 3"/>

## Features

- View Bookmarks List
- Add Bookmark
- Edit Bookmark
- Browse by Tag
- About Page
- Seed Sample Bookmarks

## Quick start

```bash
./install.sh   # installs dependencies + seeds demo data
```

See [`INSTALL.md`](INSTALL.md) for full setup and usage instructions.

## Usage guide

A walkthrough of every feature in LinkVault.

### Home Page

Visiting `/` redirects you to the bookmark list at `/bookmarks`.

### Bookmark List (`/bookmarks`)

The main view shows all your bookmarks in a table:

- **Title** — clickable link that opens the bookmark in a new tab
- **URL** — displayed in a monospace font
- **Tags** — clickable pills that take you to that tag's filtered view
- **Actions** — Edit button for each bookmark

The sidebar on the left provides navigation to all sections.

### Adding a Bookmark

1. Click **Add Bookmark** in the sidebar, or navigate to `/bookmarks/new`
2. Fill in the form:
   - **URL** (required) — the full web address, e.g. `https://example.com`
   - **Title** (required) — a descriptive name for the bookmark
   - **Tags** (optional) — comma-separated tag names, e.g. `clojure, tutorial, web`
3. Click **Save**

You'll be redirected back to the bookmark list where the new entry appears at the top.

### Editing a Bookmark

1. Click the **Edit** button next to any bookmark
2. Modify the URL, title, or tags as needed
3. Click **Save**

The changes are applied immediately and you return to the list.

### Browsing by Tag

#### Tag Index (`/tags`)

Shows every tag in the system as cards, each displaying the tag name and how many bookmarks use it. Click any card to filter.

#### Tag Detail (`/tags/<name>`)

Shows only bookmarks tagged with the given tag. The breadcrumb at the top confirms which tag you're viewing. Each bookmark's tag pills remain clickable, letting you pivot to other tags.

### About Page (`/about`)

_Full guide: [`USAGE.md`](USAGE.md)_

## Architecture

Clojure application, delivered as a complete, runnable project (15 files). Top-level layout: `resources/`, `src/`. `install.sh` provisions dependencies and seeds demo data, so the app boots with something to show. Setup details live in [`INSTALL.md`](INSTALL.md).

## FAQ

### How do I run LinkVault on my own server?

Clone this repository and run `./install.sh`, then start the app as described in [`INSTALL.md`](INSTALL.md). LinkVault is fully self-hostable — no external services are required to try it.

### Can I rebrand or white-label LinkVault?

Yes. You can edit the source directly under the MIT license, or [remix it on cenius.ai](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure) — the platform route grants full rebrand and relicense rights over your derivative.

### Is there a no-code way to modify LinkVault?

Yes — [load it on cenius.ai](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure), describe the change in plain English, and you get back a new downloadable build with the modification applied.

### What powers LinkVault under the hood?

The app is built with Clojure. What you see in this repo is the full production source, demo data included. Highlights include about Page.

### Is LinkVault free for commercial use?

Yes — it ships under the MIT license, which permits commercial use, modification and redistribution. The full text is in [LICENSE](LICENSE).

## License & rebranding

Released under the [MIT License](LICENSE) (© 2026 Cenius AI) — free for personal and commercial use.

**Need a customized version?** [Remix this app on cenius.ai](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure) — modifications made on the platform come with **full rebrand & relicense rights** over your derivative.

## Built with cenius.ai

This entire application — code, design, seeded demo data — was generated on **[cenius.ai](https://cenius.ai)** from a plain-English description.

- 🚀 [Build your own app on cenius.ai](https://cenius.ai)
- 🎛️ [Remix LinkVault on the marketplace](https://cenius.ai/marketplace/p/linkvault?ref=gh&utm_campaign=linkvault-clojure) — open it in a workspace, prompt for changes, and ship your own version.

More open-source apps: [the Cenius-ai catalog](https://github.com/Cenius-ai) · [showcase index](https://github.com/Cenius-ai/showcase)
