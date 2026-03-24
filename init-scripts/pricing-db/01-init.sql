-- Таблица тарифов
CREATE TABLE IF NOT EXISTS wagon_tariffs (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wagon_type VARCHAR(50) NOT NULL,
    cargo_type VARCHAR(255) NOT NULL,
    base_rate_per_km DECIMAL(10,2) NOT NULL,
    coefficient DECIMAL(5,2) DEFAULT 1.0,
    min_price DECIMAL(10,2),
    description TEXT,
    UNIQUE(wagon_type, cargo_type)
    );

-- Таблица расстояний (для расчетов)
CREATE TABLE IF NOT EXISTS station_distances (
                                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_station VARCHAR(255) NOT NULL,
    to_station VARCHAR(255) NOT NULL,
    distance_km INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE(from_station, to_station)
    );

-- Индексы
CREATE INDEX IF NOT EXISTS idx_wagon_tariffs_composite ON wagon_tariffs(wagon_type, cargo_type);