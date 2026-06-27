# 数据库结构说明

数据库名：`lab_equipment_db`

字符集：`utf8mb4`

## 建库语句

```sql
CREATE DATABASE lab_equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'lab_user'@'localhost' IDENTIFIED BY 'lab_pass_123';
GRANT ALL PRIVILEGES ON lab_equipment_db.* TO 'lab_user'@'localhost';
FLUSH PRIVILEGES;
```

## user_account 用户表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| username | varchar(50) | 用户名，唯一 |
| password | varchar(100) | BCrypt 加密密码 |
| real_name | varchar(50) | 真实姓名 |
| role | varchar(20) | 角色，`ADMIN` 或 `USER` |
| phone | varchar(20) | 手机号 |
| status | varchar(20) | 状态，`ENABLED` 或 `DISABLED` |
| create_time | datetime | 创建时间 |

## category 设备分类表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| category_name | varchar(80) | 分类名称，唯一 |
| description | varchar(255) | 分类描述 |
| create_time | datetime | 创建时间 |

## equipment 设备表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| equipment_name | varchar(100) | 设备名称 |
| equipment_code | varchar(80) | 设备编号，唯一 |
| category_id | bigint | 所属分类 |
| model | varchar(100) | 设备型号 |
| total_quantity | int | 总数量 |
| available_quantity | int | 可借数量 |
| status | varchar(20) | 设备状态 |
| location | varchar(120) | 存放位置 |
| remark | varchar(255) | 备注 |
| create_time | datetime | 创建时间 |

设备状态包括：`AVAILABLE` 可借用、`BORROWED` 已借出、`REPAIR` 维修中、`DISABLED` 停用。

## borrow_record 借用记录表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| user_id | bigint | 借用用户 |
| equipment_id | bigint | 借用设备 |
| borrow_quantity | int | 借用数量 |
| borrow_time | datetime | 审核通过后的借出时间 |
| expected_return_time | date | 预计归还日期 |
| actual_return_time | datetime | 实际归还时间 |
| status | varchar(20) | 借用状态 |
| remark | varchar(255) | 备注 |
| create_time | datetime | 申请创建时间 |

借用状态包括：`PENDING` 待审核、`BORROWED` 已借出、`RETURNED` 已归还、`REJECTED` 已拒绝。

## operation_log 操作日志表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| user_id | bigint | 操作用户 |
| operation_type | varchar(50) | 操作类型 |
| operation_content | varchar(255) | 操作内容 |
| operation_time | datetime | 操作时间 |
| ip_address | varchar(64) | 操作 IP |
