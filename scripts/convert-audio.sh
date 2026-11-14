#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source_root="$repo_root/legacy/audio-source"
target_root="$repo_root/src/assets"

for source in "$source_root"/{music,sound}/*.wav; do
  relative="${source#"$source_root/"}"
  target="$target_root/${relative%.wav}.mp3"
  mkdir -p "$(dirname "$target")"
  ffmpeg -hide_banner -loglevel error -y -i "$source" \
    -map_metadata -1 -codec:a libmp3lame -q:a 5 "$target"
done
