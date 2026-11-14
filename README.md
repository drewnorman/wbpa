# WBPA

A browser port of a 2013 Java/LWJGL game made for my high-school psychology teacher. The original game art and recovered Java reference are preserved in this repository; the playable version is written in TypeScript using PixiJS and Planck.js.

## Local development

The Nix flake provides Node 24, Corepack, `just`, ffmpeg, and Java for archive work. With direnv installed:

```sh
direnv allow
just bootstrap
just dev
```

Open [site.wbpa.localhost](http://site.wbpa.localhost). The `just dev` command builds and runs the Vite container through the existing local Traefik service on the `local-proxy` Podman network. Use `just dev-down` to stop it, or `just dev-logs` to follow its logs. `just dev-direct` runs Vite at `http://127.0.0.1:5173` without Traefik.

Run `just check` before committing. `just bootstrap` installs dependencies from `yarn.lock` and enables the local `commit-msg` hook, which enforces conventional commit messages. Use `just format` to apply Prettier formatting, `just build` for a production bundle, and `just preview` to inspect that bundle locally.

`just build-art` regenerates the corrected credits and shadowed end screens from the archived PNGs. The in-game score and kicks labels and the digits use the original art.

## Controls

Use **Enter** to select a menu item. During play, press **Enter** to pull back the leg, then press **Space** at the right moment to kick. Space cannot kick until the leg is pulled back. Menus and kick controls are also tappable on touch screens.
