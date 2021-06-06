-- admin_wx_application
DELETE
FROM admin_wx_application
WHERE id >= 1
  AND id <= 3;

INSERT INTO admin_wx_application(id, tenant_id, name)
VALUES (1, 0, 'u_print'),
       (2, 0, 'u_device'),
       (3, 0, 'u_analysis');
