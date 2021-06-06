-- u_tenant
DELETE FROM u_tenant WHERE id=0;
INSERT INTO u_tenant(id, name, company_id) VALUES (0, '_', 0);

-- u_company
DELETE FROM u_company WHERE id=0;
INSERT INTO u_company(id, name, area_code) VALUES (0, '_', '_');

-- u_user
INSERT INTO u_user(id, tenant_id, username, phone_number, nick_name, email, creator_id)
VALUES (0, 0, '_', '_', '_', '_', 0),
      (1, 0, 'admin', '00000000000', 'admin', 'admin@unionfab.com', 0);

-- u_user_role_relation
DELETE
FROM u_user_role_relation
WHERE (tenant_id = 0 AND user_id = 1 AND role_id = 1)

INSERT INTO u_user_role_relation(tenant_id, user_id, role_id, creator_id)
VALUES (0, 1, 1, 0); -- admin

-- u_role
DELETE
FROM u_role
WHERE id = 1
   OR (id >= 10 AND id <= 14);

INSERT INTO u_role(id, tenant_id, name, nickname, description, creator_id, icon_file_id)
VALUES (1, 0, 'ADMIN', '系统管理员', '', 0, NULL),
       (10, 0, 'TENANT_ADMIN', '管理员', '', 0, NULL),
       (11, 0, 'PRINTER_OPERATOR', '操机员', '', 0, NULL),
       (12, 0, 'DATA_PROCESSOR', '数据处理员', '', 0, NULL),
       (13, 0, 'PART_PICKER', '取件人员', '', 0, NULL),
       (14, 0, 'SCHEDULER', '排产人员', '', 0, NULL);

-- u_permission
DELETE
FROM u_permission
WHERE id >= 1
  AND id <= 9;

INSERT INTO u_permission(id, tenant_id, name, nickname, description, creator_id, icon_file_id)
VALUES (1, 0, 'WORK_ORDER_DEL', '删除工单', '', 0, NULL),
       (2, 0, 'WORK_ORDER_SCHEDULE', '工单排产', '', 0, NULL),
       (3, 0, 'WORK_ORDER_PRINTABLE_FILE_DOWNLOAD', '下载图纸', '', 0, NULL),
       (4, 0, 'WORK_ORDER_STOP_PRINTING', '终止打印', '', 0, NULL),
       (5, 0, 'WORK_ORDER_PRINT', '暂停/继续打印', '', 0, NULL),
       (6, 0, 'WORK_ORDER_PRINT_PART', '零件取消打印', '', 0, NULL),
       (7, 0, 'DEV_OP_PAUSE', '暂停打印', '', 0, NULL),
       (8, 0, 'DEV_OP_STOP', '停止设备打印', '', 0, NULL),
       (9, 0, 'TAG_EDIT', '添加/删除标签', '', 0, NULL);
