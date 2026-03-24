-- Таблица документов
CREATE TABLE IF NOT EXISTS documents (
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
CREATE INDEX IF NOT EXISTS idx_documents_order_id ON documents(order_id);
CREATE INDEX IF NOT EXISTS idx_documents_payment_id ON documents(payment_id);