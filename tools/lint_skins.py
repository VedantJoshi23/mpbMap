#!/usr/bin/env python3
"""Skin lint (STD-SKINS rules 1-4; ADR-0003). Fails on:
- any layer drawing the `boundary` source-layer (Law 12: no boundary lines at any level);
- a vector source without attribution, or a style that disables attribution (R11);
- a missing or malformed meta.json, or a missing LICENSES.md.
"""
import json
import pathlib
import sys

REQUIRED_META = {"codename", "version", "is3D", "fallback2D", "fontSet", "iconSet", "accents", "attribution"}


def lint(skin: pathlib.Path) -> list[str]:
    errors = []
    style = json.loads((skin / "style.json").read_text())
    for layer in style.get("layers", []):
        if layer.get("source-layer") == "boundary":
            errors.append(f"{skin.name}: layer '{layer.get('id')}' draws the boundary layer (Law 12)")
    for name, src in style.get("sources", {}).items():
        if src.get("type") == "vector" and not src.get("attribution"):
            errors.append(f"{skin.name}: source '{name}' has no attribution (R11)")
    if style.get("metadata", {}).get("kairos:hideAttribution"):
        errors.append(f"{skin.name}: attribution must not be hidden (R11)")
    meta_path = skin / "meta.json"
    if not meta_path.exists():
        errors.append(f"{skin.name}: meta.json missing")
    else:
        missing = REQUIRED_META - set(json.loads(meta_path.read_text()))
        if missing:
            errors.append(f"{skin.name}: meta.json missing keys {sorted(missing)}")
        meta = json.loads(meta_path.read_text())
        if meta.get("is3D") and not meta.get("fallback2D"):
            errors.append(f"{skin.name}: 3D skin needs a fallback2D (C1)")
    if not (skin / "LICENSES.md").exists():
        errors.append(f"{skin.name}: LICENSES.md missing")
    return errors


def main() -> int:
    root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "skins")
    skins = [p for p in sorted(root.iterdir()) if p.is_dir()]
    errors = [e for s in skins for e in lint(s)]
    for e in errors:
        print("ERROR:", e)
    print(f"{len(skins)} skin(s) checked, {len(errors)} error(s)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
