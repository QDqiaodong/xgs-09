#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

echo "🚀 正在启动手账创作与排版灵感库..."
echo ""

docker-compose up --build -d

echo ""
echo "⏳ 等待服务就绪..."
sleep 10

bash "${SCRIPT_DIR}/start.sh"
