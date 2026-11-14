set shell := ["bash", "-euo", "pipefail", "-c"]

default:
  @just --list

bootstrap:
  @corepack yarn install --immutable
  @just install-hooks

dev:
  @corepack yarn dev

build:
  @corepack yarn build

preview:
  @corepack yarn preview

format:
  @corepack yarn format

check:
  @corepack yarn format:check
  @corepack yarn lint
  @corepack yarn typecheck
  @corepack yarn test
  @corepack yarn build

install-hooks:
  @if [ "$(git config --local --get core.hooksPath || true)" != ".githooks" ]; then git config core.hooksPath .githooks; fi
  @echo "Installed commit-msg hook from .githooks"

commitlint from="HEAD~1" to="HEAD":
  @corepack yarn commitlint --from "{{from}}" --to "{{to}}"
