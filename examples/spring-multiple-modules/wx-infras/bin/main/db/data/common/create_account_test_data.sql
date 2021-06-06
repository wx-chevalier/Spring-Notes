-- u_user
DELETE
FROM u_user
WHERE id IN (10, 11, 12, 50, 51, 52, 100, 101, 102);

INSERT INTO u_user(id, tenant_id, username, phone_number, nick_name, email, creator_id)
VALUES -- 优联
       (10, 0, 'ua-admin', '42424242420', 'ua-admin', 'ua-admin@unionfab.com', 0),
       (11, 0, 'ua-test1', '42424242421', 'ua-test1', 'ua-test1@unionfab.com', 0),
       (12, 0, 'ua-test2', '42424242422', 'ua-test2', 'ua-test2@unionfab.com', 0),
       -- 联泰
       (50, 0, 'utk-admin', '42424242423', 'utk-admin', 'utk-admin@unionfab.com', 0),
       (51, 0, 'utk-test1', '42424242424', 'utk-test1', 'utk-test1@unionfab.com', 0),
       (52, 0, 'utk-test2', '42424242425', 'utk-test2', 'utk-test2@unionfab.com', 0),
       -- 峻宸
       (100, 0, 'jc-admin', '42424242426', 'jc-admin', 'jc-admin@unionfab.com', 0),
       (101, 0, 'jc-test1', '42424242427', 'jc-test1', 'jc-test1@unionfab.com', 0),
       (102, 0, 'jc-test2', '42424242428', 'jc-test2', 'jc-test2@unionfab.com', 0);

-- u_user_role_relation
DELETE
FROM u_user_role_relation
WHERE (tenant_id = 1 AND user_id = 10 AND role_id = 10)
   OR (tenant_id = 2 AND user_id = 50 AND role_id = 10)
   OR (tenant_id = 3 AND user_id = 100 AND role_id = 10);

INSERT INTO u_user_role_relation(tenant_id, user_id, role_id, creator_id)
VALUES -- tenant admins
       (1, 10, 10, 1),
       (2, 50, 10, 1),
       (3, 100, 10, 1);

-- u_company
DELETE
FROM u_company
WHERE id IN (1, 2, 3);

INSERT INTO u_company(id, name, area_code)
VALUES (1, '优联三维打印科技发展有限公司', ''),
       (2, '上海联泰科技股份有限公司', ''),
       (3, '上海峻宸三维科技有限公司', '');

-- u_tenant
DELETE
FROM u_tenant
WHERE id IN (1, 2, 3);

INSERT INTO u_tenant(id, name, company_id)
VALUES (1, '优联上海工厂', 1),
       (2, '联泰总部打印机', 2),
       (3, '峻宸上海工厂', 3);

