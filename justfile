set shell := ["bash", "-euo", "pipefail", "-c"]

compose := "podman-compose -f local/compose.yml"

default:
  @just --list

bootstrap:
  @corepack yarn install --immutable
  @just install-hooks

dev:
  @{{ compose }} up --build

dev-direct:
  @corepack yarn dev

dev-down:
  @{{ compose }} down

dev-logs:
  @{{ compose }} logs -f site

build:
  @corepack yarn build

build-art:
  @python scripts/build-screen-art.py

preview:
  @corepack yarn preview

format:
  @corepack yarn format

check:
  @corepack yarn check

install-hooks:
  @if [ "$(git config --local --get core.hooksPath || true)" != ".githooks" ]; then git config core.hooksPath .githooks; fi
  @echo "Installed commit-msg hook from .githooks"

commitlint from="HEAD~1" to="HEAD":
  @corepack yarn commitlint --from "{{from}}" --to "{{to}}"
