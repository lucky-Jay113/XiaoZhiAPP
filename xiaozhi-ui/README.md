# 蚂蚁小智前端

基于 Vue 3、Vite 和 Element Plus 的医疗智能客服界面，配合仓库根目录的 Spring Boot 后端使用。

## 功能

- 对话发送与回复逐步显示。
- 新建、切换和删除会话，使用浏览器 localStorage 保存历史记录。
- Markdown 回复展示。

## 本地启动

先按[主项目说明](../README.md)配置并启动后端，默认地址为 `http://localhost:8080`。

前端建议使用 Node.js 20 或更新版本。在仓库根目录执行：

```bash
cd xiaozhi-ui
npm ci
npm run dev
```

在浏览器打开 Vite 输出的本地地址，默认是 `http://localhost:5173`。

开发时，前端向 `/api/xiaozhi/chat` 发送请求；`vite.config.js` 将 `/api` 代理到本机后端并移除该前缀，对应后端的 `POST /xiaozhi/chat` 接口。

## 构建

```bash
npm run build
```

构建结果位于 `dist/`。生产部署需要由 Web 服务器配置 `/api` 转发到后端并移除 `/api` 前缀；Vite 的开发代理不会包含在构建结果中。

本项目当前未配置独立的 lint、类型检查或自动化测试命令。
