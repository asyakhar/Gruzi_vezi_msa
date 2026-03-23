-- Создание базы данных
CREATE DATABASE pricing_db;

-- Подключение к базе
\c pricing_db;

-- Таблица тарифов на вагоны
CREATE TABLE wagon_tariffs (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               wagon_type VARCHAR(50) NOT NULL,
                               cargo_type VARCHAR(255) NOT NULL,
                               base_rate_per_km DECIMAL(10,2) NOT NULL,
                               coefficient DECIMAL(5,2) DEFAULT 1.0,
                               min_price DECIMAL(10,2),
                               description TEXT,
                               UNIQUE(wagon_type, cargo_type)
);

-- Таблица расстояний между станциями (копия из inventory, но нужна для расчетов)
CREATE TABLE station_distances (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   from_station VARCHAR(255) NOT NULL,
                                   to_station VARCHAR(255) NOT NULL,
                                   distance_km INTEGER NOT NULL,
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE(from_station, to_station)
);

-- Индексы
CREATE INDEX idx_wagon_tariffs ON wagon_tariffs(wagon_type, cargo_type);
CREATE INDEX idx_pricing_station_distances ON station_distances(from_station, to_station);

-- Вставка данных из монолита
INSERT INTO wagon_tariffs (id, wagon_type, cargo_type, base_rate_per_km, coefficient, min_price, description)
SELECT id, wagon_type, cargo_type, base_rate_per_km, coefficient, min_price, description
FROM public.wagon_tariffs;

INSERT INTO station_distances (id, from_station, to_station, distance_km, updated_at)
SELECT id, from_station, to_station, distance_km, updated_at
FROM public.station_distances;