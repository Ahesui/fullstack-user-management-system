# 现代全栈用户管理系统 (Vue + Spring Cloud)

<div align="center">
  <img src="https://img.shields.io/badge/Vue.js-3.x-4FC08D?style=for-the-badge&logo=vue.js" alt="Vue 3">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?style=for-the-badge&logo=spring" alt="Spring Boot 3.3.1">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2023.0.2-6DB33F?style=for-the-badge&logo=spring" alt="Spring Cloud 2023.0.2">
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Redis-7.x-DC382D?style=for-the-badge&logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/RabbitMQ-3.12-FF6600?style=for-the-badge&logo=rabbitmq" alt="RabbitMQ">
</div>

这是一个从零开始、逐步演进构建的现代化全栈项目。它记录了一个简单的用户管理系统，如何从一个**单体应用**，通过**前后端分离**，最终演进为**微服务架构**的完整过程。这个项目是学习和实践现代Web开发技术的绝佳范例。

## 📸 项目预览 (Project Preview)

*（请在这里替换为您自己的项目截图）*

**用户列表页 (管理员视图)**
![用户列表截图](https://raw.githubusercontent.com/your-username/your-repo-name/main/docs/images/user-list-view.png)

**登录页面**
![登录页面截图](https://raw.githubusercontent.com/your-username/your-repo-name/main/docs/images/login-view.png)


## ✨ 项目特色 (Features)

- **现代技术栈**: 采用业界主流且前沿的技术组合。
- **前后端分离**: 清晰的架构，前端(Vue)与后端(Spring)完全解耦，通过RESTful API交互。
- **微服务架构**: 使用Spring Cloud Alibaba构建，包含API网关、服务注册与发现等核心组件。
- **完整CRUD功能**: 实现了用户管理的增、删、改、查全套操作。
- **高级功能**:
    - **分页 & 条件查询**: 高效处理大量数据。
    - **缓存集成**: 使用Redis提升系统性能。
    - **消息队列**: 使用RabbitMQ实现服务间的异步通信和解耦。
    - **完善的安全机制**: 使用Spring Security实现统一认证(JWT)和基于角色的授权。
- **自动化部署**: 前端部署于Vercel，后端服务部署于Render，实现CI/CD。

## 🛠️ 技术栈 (Technology Stack)

| 领域           | 技术                                                         |
| -------------- | ------------------------------------------------------------ |
| **前端**       | `Vue 3`, `Vue Router`, `Pinia`, `Axios`, `pnpm`              |
| **后端核心**   | `Spring Boot 3.3.1`, `Spring MVC`                            |
| **微服务**     | `Spring Cloud 2023.0.2`, `Spring Cloud Alibaba 2023.0.1.0`   |
|    - **API网关** | `Spring Cloud Gateway`                                       |
|    - **服务治理** | `Nacos 2.2.3` (服务注册与发现)                           |
| **安全**       | `Spring Security 6` (JWT认证, WebFlux & MVC)               |
| **数据持久化** | `MyBatis Framework`, `MyBatis PageHelper`, `PostgreSQL`      |
| **数据缓存**   | `Redis`, `Spring Cache`                                      |
| **消息队列**   | `RabbitMQ`, `Spring AMQP`                                    |
| **构建工具**   | `Maven` (后端), `Vue CLI` (前端)                           |

## 🏗️ 项目架构 (Architecture)

本项目采用微服务架构，主要包含以下几个核心服务：

*（请在这里替换为您自己绘制的架构图）*
![架构图](https://raw.githubusercontent.com/your-username/your-repo-name/main/docs/images/architecture.png)

- **user-management-frontend**: 基于Vue的单页面应用(SPA)，是用户交互的入口。
- **gateway-service**: Spring Cloud Gateway实现的API网关，是所有后端服务的统一入口，负责路由转发、统一认证(JWT验证)、CORS处理等。
- **user-service**: 核心业务微服务，负责所有用户相关的业务逻辑和数据持久化。
- **Nacos**: 服务注册与发现中心，所有微服务在此注册，网关通过它来动态发现业务服务。
- **数据库/缓存/消息队列**: 提供数据存储、缓存和异步消息处理的基础设施。

## 🚀 本地运行指南 (Local Setup Guide)

### 1. 环境要求 (Prerequisites)

请确保你的本地环境已安装以下软件：
- `JDK 17` 或更高版本
- `Maven 3.6+`
- `Node.js 18+` (LTS版本) 和 `pnpm`
- `Docker` 和 `Docker Compose` (强烈推荐，用于快速启动基础设施)
- `Git`

### 2. 克隆项目 (Clone)

```bash
git clone https://github.com/[YOUR_USERNAME]/[YOUR_REPO_NAME].git
cd [YOUR_REPO_NAME]
```

### 3. 启动基础设施 (Start Infrastructure)

推荐使用Docker Compose一键启动所有后端依赖服务。
*（你可以在项目根目录创建一个 `docker-compose.yml` 文件来管理它们）*

```bash
# 这是一个示例，你需要一个docker-compose.yml文件
docker-compose up -d
```
如果手动安装，请确保PostgreSQL, Redis, RabbitMQ, Nacos服务已在本地启动，并检查端口是否正确。

### 4. 启动后端微服务 (Start Backend Services)

*   **使用IDE**:
    1.  用IntelliJ IDEA或VS Code打开项目根目录。
    2.  等待Maven加载完所有依赖。
    3.  依次运行 `user-service` 和 `gateway-service` 模块的 `*Application.java`主启动类。
*   **使用命令行**:
    ```bash
    # 在 user-management-ms 目录下
    cd user-management-ms
    
    # 启动用户服务 (在第一个终端)
    ./mvnw spring-boot:run -pl user-service
    
    # 启动网关服务 (在第二个终端)
    ./mvnw spring-boot:run -pl gateway-service
    ```

### 5. 启动前端应用 (Start Frontend App)

```bash
# 在 user-management-frontend 目录下
cd user-management-frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm serve
```

### 6. 访问 (Access)

- **前端应用**: `http://localhost:10481` (或pnpm提示的端口)
- **API网关**: `http://localhost:9999`
- **Nacos控制台**: `http://localhost:18848/nacos` (用户名/密码: `nacos/nacos`)
- **RabbitMQ控制台**: `http://localhost:15672` (用户名/密码: `guest/guest`)

**默认登录账号**:
- 用户名: `admin`
- 密码: `123456`

## ☁️ 部署 (Deployment)

本项目已成功实现自动化部署：
- **前端 (`user-management-frontend`)**: 部署在 **Vercel**。任何对`main`分支下该目录的提交都会自动触发新的部署。
- **后端 (`*-service`)**: 部署在 **Render**。每个微服务都作为独立的Docker服务运行，并与Render提供的数据库和缓存服务相连。

## 🤝 参与贡献 (Contributing)

欢迎提交Pull Request或提出Issues！

## 📄 开源协议 (License)

本项目采用 [MIT License](LICENSE)。