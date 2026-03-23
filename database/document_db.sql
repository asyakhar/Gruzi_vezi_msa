-- Создание базы данных
CREATE DATABASE document_db;

-- Подключение к базе
\c document_db;

-- Таблица документов
CREATE TABLE documents (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           order_id UUID,
                           payment_id UUID,
                           document_type VARCHAR(50) NOT NULL,
                           file_path VARCHAR(500),
                           file_content BYTEA,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           metadata TEXT
);

-- Индексы
CREATE INDEX idx_documents_order_id ON documents(order_id);
CREATE INDEX idx_documents_payment_id ON documents(payment_id);
CREATE INDEX idx_documents_type ON documents(document_type);

-- Начальные данные (пусто, документы генерируются динамически)