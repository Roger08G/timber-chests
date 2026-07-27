#!/usr/bin/env python3
"""Generate Timber Chests entity textures from Minecraft's chest UV templates.

The vanilla source images provide only the UV silhouette, baked shading, and
latch pixels. Every wood-facing pixel is remapped to a dedicated palette and
receives a deterministic grain treatment. The output is reproducible and the
source Minecraft resources JAR is never copied into the repository.
"""

from __future__ import annotations

import argparse
import io
import math
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "src/main/resources/assets/timber_chests/textures/entity/chest"
PREVIEW = ROOT / "docs/texture-atlas-preview.png"

TEMPLATES = {
    "": "assets/minecraft/textures/entity/chest/normal.png",
    "_left": "assets/minecraft/textures/entity/chest/normal_left.png",
    "_right": "assets/minecraft/textures/entity/chest/normal_right.png",
}

# Five shades, darkest to lightest. These palettes follow the visual direction
# in docs/timber-chests-concept.png without attempting to rasterize that image.
PALETTES: dict[str, tuple[str, ...]] = {
    "oak": ("38240f", "624019", "8d5c20", "b8792c", "d99c43"),
    "spruce": ("171210", "2a1b14", "47291a", "633b23", "80532f"),
    "birch": ("55452f", "9e8b67", "c8b887", "e5d6aa", "f3e6c3"),
    "jungle": ("3b1912", "6c2e1e", "91462b", "b45f3b", "d17b51"),
    "acacia": ("4a2419", "85371f", "b74f26", "dd6a2e", "ef8a43"),
    "dark_oak": ("0e0b09", "1d1410", "312019", "493024", "654633"),
    "mangrove": ("310b11", "5c1019", "861824", "ad2633", "cf3d49"),
    "cherry": ("6e4143", "a86669", "cf8f91", "eab1b1", "f6ced0"),
    "bamboo": ("59460e", "867019", "ad9425", "d1b83a", "ead45b"),
    "crimson": ("2c0c20", "52102f", "7a1745", "a52661", "c83b78"),
    "warped": ("062f31", "074b4e", "0b7070", "149493", "27b6ac"),
}


def rgb(hex_color: str) -> tuple[int, int, int]:
    return tuple(int(hex_color[index : index + 2], 16) for index in (0, 2, 4))


def blend(color: tuple[int, int, int], target: tuple[int, int, int], amount: float) -> tuple[int, int, int]:
    return tuple(round(channel + (other - channel) * amount) for channel, other in zip(color, target))


def is_wood_pixel(pixel: tuple[int, int, int, int]) -> bool:
    red, green, blue, alpha = pixel
    if alpha == 0 or max(red, green, blue) == 0:
        return False
    saturation = (max(red, green, blue) - min(red, green, blue)) / max(red, green, blue)
    # Vanilla's latch and neutral metal edging stay unchanged.
    return saturation > 0.22 and red > green and green > blue


def palette_color(source: tuple[int, int, int, int], palette: tuple[tuple[int, int, int], ...]) -> tuple[int, int, int]:
    red, green, blue, _ = source
    luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
    normalized = max(0.0, min(1.0, (luminance - 34.0) / 155.0))
    position = normalized * (len(palette) - 1)
    low = int(position)
    high = min(low + 1, len(palette) - 1)
    return blend(palette[low], palette[high], position - low)


def add_grain(variant: str, x: int, y: int, color: tuple[int, int, int], palette: tuple[tuple[int, int, int], ...]) -> tuple[int, int, int]:
    noise = (x * 17 + y * 31 + x * y * 3) % 19

    if variant == "birch" and ((x * 7 + y * 11) % 41 in (0, 1)):
        return blend(color, palette[0], 0.72)
    if variant == "bamboo":
        if x % 7 in (0, 1):
            color = blend(color, palette[0], 0.24)
        if y % 8 == 0:
            color = blend(color, palette[1], 0.36)
        return color
    if variant in ("crimson", "warped"):
        wave = math.sin((x + y * 1.7) * 0.72)
        if wave > 0.78 and noise < 10:
            return blend(color, palette[-1], 0.25)
        if wave < -0.78 and noise < 10:
            return blend(color, palette[0], 0.22)
        return color
    if variant == "mangrove" and (x + 2 * y) % 11 == 0:
        return blend(color, palette[0], 0.24)
    if variant == "cherry" and (2 * x + y) % 17 == 0:
        return blend(color, palette[-1], 0.18)
    if variant == "acacia" and x % 9 == 0:
        return blend(color, palette[1], 0.18)
    if variant == "spruce" and (x + y) % 8 == 0:
        return blend(color, (8, 10, 11), 0.28)
    if variant == "dark_oak" and (x * 3 + y) % 13 == 0:
        return blend(color, palette[0], 0.32)
    if variant == "jungle" and (x + 3 * y) % 14 == 0:
        return blend(color, palette[-1], 0.15)
    if variant == "oak" and noise == 0:
        return blend(color, palette[0], 0.18)
    return color


def render_texture(template: Image.Image, variant: str) -> Image.Image:
    result = template.convert("RGBA").copy()
    palette = tuple(rgb(color) for color in PALETTES[variant])
    pixels = result.load()

    for y in range(result.height):
        for x in range(result.width):
            source = pixels[x, y]
            if not is_wood_pixel(source):
                continue
            luminance = 0.2126 * source[0] + 0.7152 * source[1] + 0.0722 * source[2]
            if variant == "acacia" and luminance < 88:
                gray = round(38 + max(0.0, luminance - 30.0) * 0.62)
                styled = (gray, gray + 3, gray + 5)
            elif variant == "spruce" and luminance < 82:
                shade = round(10 + max(0.0, luminance - 30.0) * 0.22)
                styled = (shade, shade + 2, shade + 3)
            else:
                styled = palette_color(source, palette)
            styled = add_grain(variant, x, y, styled, palette)
            pixels[x, y] = (*styled, source[3])

    return result


def build_preview(singles: dict[str, Image.Image]) -> None:
    scale = 6
    cell_width = 64 * scale + 24
    cell_height = 64 * scale + 44
    columns = 4
    rows = math.ceil(len(singles) / columns)
    preview = Image.new("RGB", (columns * cell_width, rows * cell_height), (27, 30, 34))
    draw = ImageDraw.Draw(preview)

    for index, (variant, image) in enumerate(singles.items()):
        column = index % columns
        row = index // columns
        x = column * cell_width + 12
        y = row * cell_height + 12
        enlarged = image.resize((64 * scale, 64 * scale), Image.Resampling.NEAREST)
        preview.paste(enlarged, (x, y), enlarged)
        draw.text((x, y + 64 * scale + 8), variant.replace("_", " ").title(), fill=(235, 235, 235))

    PREVIEW.parent.mkdir(parents=True, exist_ok=True)
    preview.save(PREVIEW, optimize=True)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--minecraft-resources-jar",
        required=True,
        type=Path,
        help="Minecraft 1.21 client resources JAR containing the vanilla chest textures",
    )
    args = parser.parse_args()

    OUTPUT.mkdir(parents=True, exist_ok=True)
    singles: dict[str, Image.Image] = {}

    with ZipFile(args.minecraft_resources_jar) as archive:
        templates = {
            suffix: Image.open(io.BytesIO(archive.read(resource))).convert("RGBA")
            for suffix, resource in TEMPLATES.items()
        }

    for variant in PALETTES:
        for suffix, template in templates.items():
            rendered = render_texture(template, variant)
            rendered.save(OUTPUT / f"{variant}{suffix}.png", optimize=True)
            if not suffix:
                singles[variant] = rendered

    build_preview(singles)
    print(f"Generated {len(PALETTES) * len(TEMPLATES)} textures in {OUTPUT}")
    print(f"Preview: {PREVIEW}")


if __name__ == "__main__":
    main()
