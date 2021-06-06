-- admin_permission_setting
DELETE
FROM admin_permission_setting
WHERE id >= 100
  AND id <= 108;

INSERT INTO admin_permission_setting(id, tenant_id, wx_application_id, permission_name, directory)
VALUES
    -- u_print
    (100, 0, 1, 'WORK_ORDER_DEL', '生产工单'),
    (101, 0, 1, 'WORK_ORDER_SCHEDULE', '生产工单'),
    (102, 0, 1, 'WORK_ORDER_PRINTABLE_FILE_DOWNLOAD', '生产工单'),
    (103, 0, 1, 'WORK_ORDER_STOP_PRINTING', '生产工单'),
    (104, 0, 1, 'WORK_ORDER_PRINT', '生产工单'),
    (105, 0, 1, 'WORK_ORDER_PRINT_PART', '生产工单'),
    (106, 0, 1, 'DEV_OP_PAUSE', '生产工单'),
    (107, 0, 1, 'DEV_OP_STOP', '操机管理'),
    (108, 0, 1, 'TAG_EDIT', '标签管理');
-- u_device
-- (200L, 0L, 2L, '', ''),
-- u_analysis
-- (300L, 0L, 3L, '', '');
