CREATE DATABASE `securitydb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

SELECT u.username, r.role_name, p.name AS 'PERMISSION NAME'
FROM users u
INNER JOIN user_roles ur ON ur.user_id = u.id
INNER JOIN roles r ON r.id = ur.role_id
INNER JOIN role_permissions rp ON rp.role_id = r.id
INNER JOIN permissions p ON p.id = rp.permission_id