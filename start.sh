#!/bin/bash

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"

if [ -f "${ENV_FILE}" ]; then
    export $(grep -v '^#' "${ENV_FILE}" | xargs)
fi

FRONTEND_PORT="${FRONTEND_PORT:-18037}"
BACKEND_PORT="${BACKEND_PORT:-19037}"
MYSQL_PORT="${MYSQL_PORT:-13337}"
REDIS_PORT="${REDIS_PORT:-16337}"

echo ""
echo -e "${CYAN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                                                            ║${NC}"
echo -e "${CYAN}║         🎉  手账创作与排版灵感库  启动成功！  🎉         ║${NC}"
echo -e "${CYAN}║                                                            ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${GREEN}📋 服务端口一览：${NC}"
echo -e "  ├─ 前端访问地址:  ${YELLOW}http://localhost:${FRONTEND_PORT}${NC}"
echo -e "  ├─ 前端访问地址:  ${YELLOW}http://127.0.0.1:${FRONTEND_PORT}${NC}"
echo -e "  ├─ 后端API地址:   ${YELLOW}http://127.0.0.1:${BACKEND_PORT}/api${NC}"
echo -e "  ├─ MySQL端口:     ${YELLOW}127.0.0.1:${MYSQL_PORT}${NC}"
echo -e "  └─ Redis端口:     ${YELLOW}127.0.0.1:${REDIS_PORT}${NC}"
echo ""

echo -e "${GREEN}🔍 正在验证服务可用性...${NC}"
sleep 2

echo ""
echo -e "${CYAN}🌐 前端页面验证 (127.0.0.1):${NC}"
curl -sS "http://127.0.0.1:${FRONTEND_PORT}" 2>/dev/null | head -5 | sed 's/^/  /' || echo -e "  ${YELLOW}请稍后手动访问验证${NC}"

echo ""
echo -e "${CYAN}🌐 前端页面验证 (localhost):${NC}"
curl -sS "http://localhost:${FRONTEND_PORT}" 2>/dev/null | head -5 | sed 's/^/  /' || echo -e "  ${YELLOW}请稍后手动访问验证${NC}"

echo ""
echo -e "${CYAN}🔌 后端API验证:${NC}"
curl -sS "http://127.0.0.1:${BACKEND_PORT}/api/works/latest?pageNum=1&pageSize=1" 2>/dev/null | head -3 | sed 's/^/  /' || echo -e "  ${YELLOW}后端服务启动中，请稍候...${NC}"

echo ""
echo -e "${GREEN}💡 常用命令：${NC}"
echo -e "  查看日志:  docker-compose logs -f"
echo -e "  停止服务:  docker-compose down"
echo -e "  重启服务:  docker-compose restart"
echo -e "  重新构建:  docker-compose up --build -d"
echo ""

echo -e "${GREEN}🚀 打开浏览器访问:  ${YELLOW}http://localhost:${FRONTEND_PORT}${NC}"
echo ""
