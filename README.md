# WBPA

A browser port of a 2013 Java/LWJGL game made for my high-school psychology teacher. The original game art and recovered Java reference are preserved in this repository; the playable Classic mode is written in TypeScript using PixiJS and Planck.js.

The entry page asks for a shared password before showing a mode launcher. Classic is available now; Revamp is reserved for a future version.

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

## Entry screen and modes

The shared password is checked in the browser against a SHA-256 hash in `src/access.ts`. The plaintext password is not in this repository. A successful entry sets a session cookie, which is shared across tabs and normally lasts until the browser closes; browser session restore can retain it. The launcher has a **Lock this browser** control to clear it.

This is an invitation screen, not a security boundary. The repository is public and Firebase Hosting serves the built JavaScript, art, and audio directly. Anyone can download those files or bypass the browser check. Do not use the cookie to authorize a future API or protect player data.

To rotate the shared password, run `node scripts/new-access-password.mjs`, save the printed password outside the repository, replace the hash in `src/access.ts`, and deploy the new build. Share the password separately with invited players. The current plaintext password should never be committed.

`src/main.ts` owns the entry screen and launcher. It loads `src/classic.ts` only for `/classic`; the existing game implementation remains under `src/game/`. Add Revamp as a separate module when its gameplay is defined. Browser high scores remain local to each player.

## Firebase Hosting

The Vite build outputs static files to `dist/`, and `firebase.json` routes direct visits to `/classic` back to the app entry point. The game is deployed from the public [wbpa repository](https://github.com/drewnorman/wbpa) to the dedicated `wbpa-adre-me` Firebase project. Its Firebase URL is [wbpa-adre-me.web.app](https://wbpa-adre-me.web.app); `wbpa.adre.me` is the custom domain. The game does not need Cloud Functions or Cloud Run for this release. If Revamp later needs an API, Firebase Hosting can rewrite `/api/**` to either service; that API will need its own authentication and validation.

The project, billing link, and matching Hosting site are provisioned. Firebase's default `web.app` and `firebaseapp.com` URLs show the same entry screen. The custom domain uses an unproxied Cloudflare CNAME from `wbpa.adre.me` to `wbpa-adre-me.web.app`; Firebase provisions its certificate after DNS verification.

The GitHub workflows use Workload Identity Federation, following the personal site's deployment pattern. Configure the repository variable `FIREBASE_PROJECT_ID` and the secrets `WIF_PROVIDER`, `GCP_SA_EMAIL`, and `RELEASE_PLEASE_TOKEN`. Give the deploy service account Firebase Hosting deployment permission for the game project. Pull requests from branches in this repository run checks and create seven-day preview channels; fork pull requests run checks without deployment. Release candidate tags (`v*-rc*`) deploy to the `staging` channel, and final `v*` tags deploy to production. Release Please opens a release pull request from Conventional Commits on `main` and creates the final tag when that pull request is merged. Preview URLs are public and use the same invitation screen.

For a manual preview or deploy after Firebase is configured:

```sh
corepack yarn build
corepack yarn firebase:preview --project YOUR_FIREBASE_PROJECT_ID
corepack yarn firebase:deploy --project YOUR_FIREBASE_PROJECT_ID
```

## Controls

Use **Enter** to select a menu item. During play, press **Enter** to pull back the leg, then press **Space** at the right moment to kick. Space cannot kick until the leg is pulled back. Menus and kick controls are also tappable on touch screens.
