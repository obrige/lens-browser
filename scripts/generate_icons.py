#!/usr/bin/env python3
"""Generate launcher icons for all densities using PIL."""
import os, sys, subprocess
from pathlib import Path

def ensure_pillow():
    try:
        from PIL import Image, ImageDraw
        return Image, ImageDraw
    except ImportError:
        print("Installing Pillow...")
        subprocess.check_call([
            sys.executable, "-m", "pip", "install",
            "--break-system-packages", "--quiet", "Pillow"
        ])
        import site
        site.addsitedir(site.getusersitepackages())
        from PIL import Image, ImageDraw
        return Image, ImageDraw

Image, ImageDraw = ensure_pillow()

RES_DIR = Path("app/src/main/res")
DENSITIES = {
    "mdpi": 48, "hdpi": 72, "xhdpi": 96,
    "xxhdpi": 144, "xxxhdpi": 192,
}

def create_foreground(size):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size / 2, size / 2
    r = size * 0.32
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(255, 255, 255, 255))
    inner_r = r * 0.45
    draw.ellipse([cx - inner_r, cy - inner_r, cx + inner_r, cy + inner_r],
                 fill=(30, 30, 46, 255))
    return img

def create_background(size):
    return Image.new("RGBA", (size, size), (30, 30, 46, 255))

def main():
    print("Generating launcher icons...")
    for density, size in DENSITIES.items():
        dpi_dir = RES_DIR / f"mipmap-{density}"
        dpi_dir.mkdir(parents=True, exist_ok=True)
        fg = create_foreground(size)
        bg = create_background(size)
        icon = Image.alpha_composite(bg, fg)
        icon.save(dpi_dir / "ic_launcher.png", "PNG")
        icon.save(dpi_dir / "ic_launcher_round.png", "PNG")
        print(f"  {density}: {size}x{size} ✓")
    print("Done! Generated icons for all densities.")

if __name__ == "__main__":
    main()
