
-- Подключение к базе
\c user_db;

-- Таблица пользователей
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       company_name VARCHAR(255) NOT NULL,
                       inn VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       role VARCHAR(50) DEFAULT 'USER'
);

-- Индексы
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_inn ON users(inn);

-- Вставка данных из монолита
INSERT INTO users (id, email, password_hash, company_name, inn, created_at, role) VALUES
                                                                                      ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'logistics@vector.ru', 'hashed_pwd_1', 'ООО Вектор', '7701234567', '2026-02-26 19:44:19.238275+03', 'USER'),
                                                                                      ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'supply@romashka.ru', 'hashed_pwd_2', 'ЗАО Ромашка', '7709876543', '2026-02-26 19:44:19.238275+03', 'USER'),
                                                                                      ('6705048b-12f0-4caa-8a44-2a8040a5fc58', 'test100@logistic.ru', '$2a$10$TSCnBvxReIylC.bkOMh05.IV6HYyCl1K22UEFxkepIVtzFr.Crjku', 'ООО Тест', '7712345678', '2026-02-27 11:27:42.961501+03', 'USER'),
                                                                                      ('0f22dba8-eb37-460f-acd2-f1fe8ec324e3', 'som@mail.ru', '$2a$10$CQEIsz429se9IaUWB2vJfeOc4R/YJcjp.yeBJytqsOKRK85fsdehO', 'ООО СОМ', '1234567890', '2026-02-27 18:26:57.811742+03', 'USER'),
                                                                                      ('7ccda00f-6f5b-49cf-9d92-36a4d81a3f7a', 'abas@mail.ru', '$2a$10$CngcSQivcSU4GGz9i4UeOeb0DsbFnG5lh25Bxo.bWTbr2acmrNnN.', 'ООО Абас', '1234512345', '2026-03-01 17:47:30.955325+03', 'USER');