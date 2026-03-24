
-- Подключение к базе
\c order_db;

-- Таблица заказов (без внешних ключей на другие сервисы)
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL,
                        user_email VARCHAR(255) NOT NULL,
                        company_name VARCHAR(255),
                        user_inn VARCHAR(20),
                        departure_station VARCHAR(255) NOT NULL,
                        destination_station VARCHAR(255) NOT NULL,
                        requested_wagon_type VARCHAR(50) NOT NULL,
                        wagon_id UUID,
                        wagon_number VARCHAR(50),
                        status VARCHAR(50) DEFAULT 'черновик',
                        total_price DECIMAL(10,2),
                        carbon_footprint_kg DECIMAL(10,2),
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица грузов
CREATE TABLE cargo (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       order_id UUID UNIQUE NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                       cargo_type VARCHAR(255) NOT NULL,
                       weight_kg INTEGER NOT NULL,
                       volume_m3 INTEGER NOT NULL,
                       packaging_type VARCHAR(100) NOT NULL
);

-- Таблица дополнительных услуг
CREATE TABLE order_services (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                                service_name VARCHAR(50) NOT NULL,
                                price DECIMAL(10,2) NOT NULL
);

-- Индексы
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_user_email ON orders(user_email);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_wagon_id ON orders(wagon_id);
CREATE INDEX idx_cargo_order_id ON cargo(order_id);


-- Вставка грузов
INSERT INTO cargo (id, order_id, cargo_type, weight_kg, volume_m3, packaging_type)
SELECT id, order_id, cargo_type, weight_kg, volume_m3, packaging_type
FROM public.cargo;

-- Вставка услуг
INSERT INTO order_services (id, order_id, service_name, price)
SELECT id, order_id, service_name::text, price
FROM public.order_services;