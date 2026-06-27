# 高校实验室设备库存与借用管理系统

本项目是一个课程设计级 Spring Boot 单体管理系统，用于实验室设备信息维护、库存管理、借用申请、审核归还、操作日志记录和设备信息导出。

## 技术栈

- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- Bootstrap
- MySQL
- Maven

## 默认账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin520` | `admin1314` |
| 普通用户 | `user520` | `user1314` |

## 数据库初始化

```sql
CREATE DATABASE lab_equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'lab_user'@'localhost' IDENTIFIED BY 'lab_pass_123';
GRANT ALL PRIVILEGES ON lab_equipment_db.* TO 'lab_user'@'localhost';
FLUSH PRIVILEGES;
```

## 运行方式

### 简单启动方式

项目已经内置 Maven 和 MySQL，不需要提前安装。推荐把项目放在英文路径下，例如：

```text
D:\university-content\web\lab-management-system
```

进入项目文件夹后，直接双击：

```text
start-system.bat
```

脚本会自动完成：

- 初始化本地 MySQL 数据目录；
- 启动本地 MySQL；
- 创建数据库和用户；
- 启动 Spring Boot 系统；
- 显示登录地址和默认账号。

启动成功后浏览器访问：

```text
http://localhost:8080/login
```

默认账号：

- 管理员：`admin520 / admin1314`
- 学生用户：`user520 / user1314`

运行过程中不要关闭启动窗口。需要停止系统时，在启动窗口按 `Ctrl + C`。

### 手动启动方式

如果需要手动启动，可以按下面步骤执行。

1. 在命令提示符 cmd 中进入项目目录：

```cmd
cd /d "D:\university-content\web\lab-management-system"
```

2. 第一次运行时初始化项目内 MySQL 数据目录：

```cmd
".tools\mysql-9.7.0-winx64\bin\mysqld.exe" --defaults-file="%CD%\.tools\mysql-lab.ini" --initialize-insecure --console
```

如果 `.tools\mysql-data` 已经存在，这一步不用重复执行。

3. 启动项目内 MySQL：

```cmd
start "" ".tools\mysql-9.7.0-winx64\bin\mysqld.exe" --defaults-file="%CD%\.tools\mysql-lab.ini"
```

4. 创建项目数据库和用户：

```cmd
".tools\mysql-9.7.0-winx64\bin\mysql.exe" -u root -h 127.0.0.1 -P 3306 -e "CREATE DATABASE IF NOT EXISTS lab_equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER IF NOT EXISTS 'lab_user'@'localhost' IDENTIFIED BY 'lab_pass_123'; GRANT ALL PRIVILEGES ON lab_equipment_db.* TO 'lab_user'@'localhost'; FLUSH PRIVILEGES;"
```

5. 使用项目内 Maven 启动系统：

```cmd
".tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

如果已经打包过，也可以直接运行：

```cmd
java -jar target\lab-management-0.0.1-SNAPSHOT.jar
```

浏览器访问 `http://localhost:8080/login`。

## 主要功能

- 登录、退出、Session 状态保持、角色权限控制
- 用户管理、设备分类管理、设备信息管理
- 普通用户设备查询和借用申请
- 管理员借用审核、拒绝和归还处理
- 操作日志查询
- 设备信息 Excel 导出

## 课程设计材料

- 数据库结构说明：`database-structure.md`
- 功能说明文档：`function-description.md`
- 系统运行截图：`photo/`
