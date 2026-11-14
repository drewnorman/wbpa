#!/usr/bin/env python3
"""Build corrected menu screens without changing the recovered originals."""

from pathlib import Path
import os
import shutil
import subprocess


SCREENS = Path(__file__).resolve().parents[1] / "src/assets/screens"
WIDTH = 400
HEIGHT = 300


def read(magick: str, name: str) -> bytes:
    return subprocess.check_output(
        [magick, str(SCREENS / f"{name}.png"), "-depth", "8", "rgba:-"]
    )


def write(magick: str, name: str, pixels: bytes) -> None:
    subprocess.run(
        [
            magick,
            "-size",
            f"{WIDTH}x{HEIGHT}",
            "-depth",
            "8",
            "rgba:-",
            str(SCREENS / f"{name}.png"),
        ],
        input=pixels,
        check=True,
    )


def corrected_credits(magick: str) -> None:
    source = read(magick, "credits-screen")
    result = bytearray(source)
    # Copy the lowercase "a" already in "Jonathon" over its second "o".
    for y in range(158, 177):
        source_index = (y * WIDTH + 199) * 4
        target_index = (y * WIDTH + 231) * 4
        result[target_index : target_index + 44] = source[
            source_index : source_index + 44
        ]
    write(magick, "credits-screen-corrected", result)


def shadowed_end_screen(magick: str, name: str) -> None:
    source = read(magick, name)
    result = bytearray(source)

    def yellow(index: int) -> bool:
        return (
            source[index] > 140
            and source[index + 1] > 125
            and source[index + 2] < 110
            and source[index + 3] > 0
        )

    for y in range(HEIGHT - 1):
        for x in range(WIDTH - 1):
            index = (y * WIDTH + x) * 4
            if not yellow(index):
                continue
            shadow_index = ((y + 1) * WIDTH + x + 1) * 4
            if yellow(shadow_index):
                continue
            coverage = min(source[index], source[index + 1]) / 255 * 0.88
            if coverage < 0.1:
                continue
            for channel, color in enumerate((74, 55, 0)):
                result[shadow_index + channel] = round(
                    source[shadow_index + channel] * (1 - coverage)
                    + color * coverage
                )
            result[shadow_index + 3] = max(
                result[shadow_index + 3], source[index + 3]
            )
    write(magick, f"{name}-shadowed", result)


def main() -> None:
    magick = os.environ.get("MAGICK_BIN") or shutil.which("magick")
    if not magick:
        raise SystemExit("ImageMagick 7 (magick) is required")
    corrected_credits(magick)
    shadowed_end_screen(magick, "end-screen")
    shadowed_end_screen(magick, "end-screen-new-high")


if __name__ == "__main__":
    main()
