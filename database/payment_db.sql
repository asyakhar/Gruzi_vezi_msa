
-- Подключение к базе
\c payment_db;

-- Таблица платежей
CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          order_id UUID NOT NULL,
                          payment_id VARCHAR(255) UNIQUE,
                          amount DECIMAL(10,2) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          payment_method VARCHAR(100),
                          company_name VARCHAR(255),
                          inn VARCHAR(12),
                          kpp VARCHAR(9),
                          bik VARCHAR(9),
                          account_number VARCHAR(20),
                          correspondent_account VARCHAR(20),
                          bank_name VARCHAR(255),
                          payment_purpose TEXT,
                          payment_document VARCHAR(50),
                          payment_date TIMESTAMP WITH TIME ZONE,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          paid_at TIMESTAMP WITH TIME ZONE,
                          error_message TEXT,
                          metadata TEXT
);

-- Таблица банковских счетов
CREATE TABLE company_accounts (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  inn VARCHAR(12) NOT NULL,
                                  company_name VARCHAR(255) NOT NULL,
                                  account_number VARCHAR(20) UNIQUE NOT NULL,
                                  balance DECIMAL(15,2) DEFAULT 0.00,
                                  bik VARCHAR(9) NOT NULL,
                                  bank_name VARCHAR(255) NOT NULL,
                                  is_main BOOLEAN DEFAULT false,
                                  is_rzd_account BOOLEAN DEFAULT false,
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  UNIQUE(inn, account_number)
);

-- Индексы
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_inn ON payments(inn);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_id ON payments(payment_id);
CREATE INDEX idx_company_accounts_inn ON company_accounts(inn);
CREATE INDEX idx_company_accounts_rzd ON company_accounts(is_rzd_account) WHERE is_rzd_account = true;

-- Вставка данных из монолита
INSERT INTO payments (id, order_id, payment_id, amount, status, payment_method, company_name, inn, kpp, bik, account_number, correspondent_account, bank_name, payment_purpose, payment_document, payment_date, created_at, paid_at, error_message, metadata)
SELECT id, order_id, payment_id, amount, status, payment_method, company_name, inn, kpp, bik, account_number, correspondent_account, bank_name, payment_purpose, payment_document, payment_date, created_at, paid_at, error_message, metadata
FROM public.payments;

INSERT INTO company_accounts (id, inn, company_name, account_number, balance, bik, bank_name, is_main, is_rzd_account, created_at, updated_at)
SELECT id, inn, company_name, account_number, balance, bik, bank_name, is_main, is_rzd_account, created_at, updated_at
FROM public.company_accounts;