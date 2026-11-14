# Widener's Baby Punting Adventure

A browser port of a 2013 Java/LWJGL game made for my high-school psychology teacher. The original game art and recovered Java reference are preserved in this repository; the playable version is written in TypeScript using PixiJS and Planck.js.

## Local development

The Nix flake provides Node 24, Corepack, `just`, ffmpeg, and Java for archive work. With direnv installed:

```sh
direnv allow
just bootstrap
just dev
```

Open the URL shown by Vite (normally `http://127.0.0.1:5173`). Run `just check` before committing. `just bootstrap` installs dependencies from `yarn.lock` and enables the local `commit-msg` hook, which enforces conventional commit messages. Use `just format` to apply Prettier formatting, `just build` for a production bundle, and `just preview` to inspect that bundle locally.

## Controls

Use **Enter** to select or start a kick, then **Space** to swing. Menus and kick controls are also tappable on touch screens.
