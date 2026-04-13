#!/usr/bin/env bash
set -euo pipefail

HOOK_DIR="$(git rev-parse --git-dir)/hooks"
HOOK_FILE="$HOOK_DIR/pre-commit"

cat >"$HOOK_FILE" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail
./gradlew spotlessCheck --no-daemon
EOF

chmod +x "$HOOK_FILE"
echo "Installed pre-commit hook at $HOOK_FILE"
