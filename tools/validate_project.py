#!/usr/bin/env python3
"""Validate the checked-in Timber Chests resources and optional release JAR."""

from __future__ import annotations

import argparse
import json
import tomllib
from pathlib import Path
from zipfile import ZipFile

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src/main/resources"
VARIANTS = (
    "oak",
    "spruce",
    "birch",
    "jungle",
    "acacia",
    "dark_oak",
    "mangrove",
    "cherry",
    "bamboo",
    "crimson",
    "warped",
)


def require(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)


def validate_json() -> int:
    files = sorted(RESOURCES.rglob("*.json"))
    for path in files:
        with path.open("r", encoding="utf-8") as handle:
            json.load(handle)
    return len(files)


def validate_variant(variant: str) -> None:
    assets = RESOURCES / "assets/timber_chests"
    data = RESOURCES / "data/timber_chests"

    required = (
        assets / f"blockstates/{variant}_chest.json",
        assets / f"models/block/{variant}_chest.json",
        assets / f"models/item/{variant}_chest.json",
        assets / f"items/{variant}_chest.json",
        data / f"recipe/{variant}_chest.json",
        data / f"loot_table/blocks/{variant}_chest.json",
        data / f"advancement/recipes/decorations/{variant}_chest.json",
    )
    for path in required:
        require(path.is_file(), f"Missing resource: {path.relative_to(ROOT)}")

    recipe = json.loads((data / f"recipe/{variant}_chest.json").read_text(encoding="utf-8"))
    require(recipe["pattern"] == ["###", "# #", "###"], f"Bad recipe pattern for {variant}")
    require(recipe["key"]["#"] == f"minecraft:{variant}_planks", f"Bad plank input for {variant}")
    require(recipe["result"]["id"] == f"timber_chests:{variant}_chest", f"Bad recipe result for {variant}")

    item = json.loads((assets / f"items/{variant}_chest.json").read_text(encoding="utf-8"))
    model = item["model"]
    require(model["type"] == "minecraft:special", f"Bad item renderer for {variant}")
    require(model["base"] == f"timber_chests:item/{variant}_chest", f"Bad item base model for {variant}")
    require(model["model"]["type"] == "minecraft:chest", f"Bad special model for {variant}")
    require(model["model"]["texture"] == f"timber_chests:{variant}", f"Bad item texture for {variant}")

    for suffix in ("", "_left", "_right"):
        texture = assets / f"textures/entity/chest/{variant}{suffix}.png"
        require(texture.is_file(), f"Missing texture: {texture.relative_to(ROOT)}")
        with Image.open(texture) as image:
            require(image.size == (64, 64), f"Bad dimensions for {texture.relative_to(ROOT)}: {image.size}")
            require(image.mode == "RGBA", f"Bad color mode for {texture.relative_to(ROOT)}: {image.mode}")
            require(image.getbbox() is not None, f"Texture is fully transparent: {texture.relative_to(ROOT)}")


def validate_fallback_recipe() -> None:
    path = RESOURCES / "data/minecraft/recipe/chest.json"
    recipe = json.loads(path.read_text(encoding="utf-8"))
    require(recipe["type"] == "timber_chests:mixed_planks_chest", "Vanilla chest fallback recipe is not installed")


def validate_jar(path: Path) -> int:
    require(path.is_file(), f"JAR not found: {path}")
    with ZipFile(path) as archive:
        names = set(archive.namelist())
        required = {
            "META-INF/LICENSE",
            "META-INF/neoforge.mods.toml",
            "data/minecraft/recipe/chest.json",
            "dev/timberchests/TimberChests.class",
        }
        for resource in RESOURCES.rglob("*"):
            if resource.is_file():
                member = resource.relative_to(RESOURCES).as_posix()
                required.add(member)
                if member in names:
                    require(archive.read(member) == resource.read_bytes(), f"JAR resource differs: {member}")
        missing = sorted(required - names)
        require(not missing, "Release JAR is missing: " + ", ".join(missing))

        properties = dict(
            line.split("=", 1)
            for line in (ROOT / "gradle.properties").read_text(encoding="utf-8").splitlines()
            if line and not line.startswith("#") and "=" in line
        )
        metadata = tomllib.loads(archive.read("META-INF/neoforge.mods.toml").decode("utf-8"))
        require(metadata["mods"][0]["version"] == properties["mod_version"], "Wrong mod version in JAR")
        require(metadata["license"] == properties["mod_license"], "Wrong license in JAR")
        require(archive.read("META-INF/LICENSE") == (ROOT / "LICENSE").read_bytes(), "JAR license differs")
        dependencies = metadata["dependencies"][properties["mod_id"]]
        minecraft = next(dependency for dependency in dependencies if dependency["modId"] == "minecraft")
        require(minecraft["versionRange"] == properties["minecraft_version_range"], "Wrong Minecraft version in JAR")
        return len(names)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", type=Path, help="Optional built release JAR to inspect")
    args = parser.parse_args()

    json_count = validate_json()
    for variant in VARIANTS:
        validate_variant(variant)
    validate_fallback_recipe()
    jar_entries = validate_jar(args.jar) if args.jar else None

    print(f"Validated {len(VARIANTS)} variants, {len(VARIANTS) * 3} textures, and {json_count} JSON files.")
    if jar_entries is not None:
        print(f"Validated release JAR with {jar_entries} entries: {args.jar}")


if __name__ == "__main__":
    main()
