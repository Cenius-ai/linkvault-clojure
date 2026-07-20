# LinkVault — User Guide

A walkthrough of every feature in LinkVault.

## Home Page

Visiting `/` redirects you to the bookmark list at `/bookmarks`.

## Bookmark List (`/bookmarks`)

The main view shows all your bookmarks in a table:

- **Title** — clickable link that opens the bookmark in a new tab
- **URL** — displayed in a monospace font
- **Tags** — clickable pills that take you to that tag's filtered view
- **Actions** — Edit button for each bookmark

The sidebar on the left provides navigation to all sections.

## Adding a Bookmark

1. Click **Add Bookmark** in the sidebar, or navigate to `/bookmarks/new`
2. Fill in the form:
   - **URL** (required) — the full web address, e.g. `https://example.com`
   - **Title** (required) — a descriptive name for the bookmark
   - **Tags** (optional) — comma-separated tag names, e.g. `clojure, tutorial, web`
3. Click **Save**

You'll be redirected back to the bookmark list where the new entry appears at the top.

## Editing a Bookmark

1. Click the **Edit** button next to any bookmark
2. Modify the URL, title, or tags as needed
3. Click **Save**

The changes are applied immediately and you return to the list.

## Browsing by Tag

### Tag Index (`/tags`)

Shows every tag in the system as cards, each displaying the tag name and how many bookmarks use it. Click any card to filter.

### Tag Detail (`/tags/<name>`)

Shows only bookmarks tagged with the given tag. The breadcrumb at the top confirms which tag you're viewing. Each bookmark's tag pills remain clickable, letting you pivot to other tags.

## About Page (`/about`)

Explains what LinkVault is, its features, the technology stack, and the philosophy behind it.

## Navigation

The left sidebar is always visible and provides quick access to:

- **Bookmarks** — full list
- **Add Bookmark** — the creation form
- **Tags** — browse by tag
- **About** — project information

## Tips

- Tags are case-sensitive. `Clojure` and `clojure` are different tags.
- Tags are created automatically when you add them to a bookmark.
- Editing a bookmark's tags replaces all tags — include existing tags you want to keep.
- The database is a single file (`linkvault.db`) that you can back up, copy, or delete to start fresh.
