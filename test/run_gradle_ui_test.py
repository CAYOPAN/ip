"""Run Baymax from Gradle's installed application in an isolated directory."""

from __future__ import annotations

import os
from pathlib import Path
import subprocess
import sys
import tempfile


def main() -> int:
    """Build the Gradle application distribution, then run it with clean storage."""

    repo_root = Path(__file__).resolve().parents[1]
    wrapper_name = "gradlew.bat" if os.name == "nt" else "gradlew"
    script_name = "baymax.bat" if os.name == "nt" else "baymax"

    gradle_wrapper = repo_root / wrapper_name
    install_result = subprocess.run(
        [str(gradle_wrapper), "--quiet", "installShadowDist"],
        cwd=repo_root,
        stdin=subprocess.DEVNULL,
        check=False,
    )
    if install_result.returncode != 0:
        return install_result.returncode

    app_script = repo_root / "build" / "install" / "baymax-shadow" / "bin" / script_name
    console_input = sys.stdin.read()
    with tempfile.TemporaryDirectory(prefix="baymax-ui-test-") as work_dir:
        app_result = subprocess.run(
            [str(app_script)],
            cwd=work_dir,
            input=console_input,
            text=True,
            check=False,
        )
        return app_result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
