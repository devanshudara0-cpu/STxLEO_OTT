#!/bin/bash
export GIT_CONFIG_NOSYSTEM=1
cd "$(dirname "$0")"

echo "=================================================="
echo "   ST×LEO OTT v1.0 - Push to GitHub Script       "
echo "   Developer: Devanshu (@Dara_OG)                 "
echo "=================================================="
echo ""

REPO_NAME="STxLEO_OTT"
GITHUB_USER="devanshudara0-cpu"

read -p "Enter your GitHub Repo URL (default: https://github.com/$GITHUB_USER/$REPO_NAME.git): " input_url
TARGET_URL="${input_url:-https://github.com/$GITHUB_USER/$REPO_NAME.git}"

echo "Configuring remote origin -> $TARGET_URL"
git remote remove origin 2>/dev/null || true
git remote add origin "$TARGET_URL"

echo "Pushing code to main branch..."
git push -u origin main

echo ""
echo "=================================================="
echo " If push succeeded, GitHub Actions has started    "
echo " building your clean APK automatically!           "
echo " Check: $TARGET_URL/actions                       "
echo "=================================================="
