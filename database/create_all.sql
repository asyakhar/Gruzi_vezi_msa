-- Создание всех БД для микросервисов
-- Проверка существования и создание баз
SELECT 'CREATE DATABASE user_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_db')\gexec
SELECT 'CREATE DATABASE order_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_db')\gexec
SELECT 'CREATE DATABASE inventory_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inventory_db')\gexec
SELECT 'CREATE DATABASE pricing_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pricing_db')\gexec
SELECT 'CREATE DATABASE payment_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_db')\gexec
SELECT 'CREATE DATABASE document_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'document_db')\gexec

-- Вывод информации о созданных базах
    \echo '========================================='
    \echo 'Базы данных созданы:'
    \echo '  - user_db      (порт: 5432)'
    \echo '  - order_db     (порт: 5433)'
    \echo '  - inventory_db (порт: 5434)'
    \echo '  - pricing_db   (порт: 5435)'
    \echo '  - payment_db   (порт: 5436)'
    \echo '  - document_db  (порт: 5437)'
    \echo '========================================='
    \echo 'Для инициализации каждой базы выполните:'
    \echo '  psql -U postgres -d user_db -f user_db.sql'
    \echo '  psql -U postgres -d order_db -f order_db.sql'
    \echo '  psql -U postgres -d inventory_db -f inventory_db.sql'
    \echo '  psql -U postgres -d pricing_db -f pricing_db.sql'
    \echo '  psql -U postgres -d payment_db -f payment_db.sql'
    \echo '  psql -U postgres -d document_db -f document_db.sql'
    \echo '========================================='