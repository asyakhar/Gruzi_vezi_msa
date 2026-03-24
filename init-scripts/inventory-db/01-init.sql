-- Таблица вагонов
CREATE TABLE IF NOT EXISTS wagons (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wagon_number VARCHAR(50) UNIQUE NOT NULL,
    wagon_type VARCHAR(50) NOT NULL,
    max_weight_kg INTEGER NOT NULL,
    max_volume_m3 INTEGER NOT NULL,
    current_station VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'свободен'
    );

-- Таблица расписания
CREATE TABLE IF NOT EXISTS wagon_schedule (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wagon_id UUID NOT NULL REFERENCES wagons(id) ON DELETE CASCADE,
    order_id UUID,
    departure_station VARCHAR(255) NOT NULL,
    arrival_station VARCHAR(255) NOT NULL,
    departure_date TIMESTAMP WITH TIME ZONE,
    arrival_date TIMESTAMP WITH TIME ZONE,
                                                     status VARCHAR(50) DEFAULT 'запланирован',
    cargo_type VARCHAR(255),
    cargo_weight_kg INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                                                     );

-- Таблица расстояний
CREATE TABLE IF NOT EXISTS station_distances (
                                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_station VARCHAR(255) NOT NULL,
    to_station VARCHAR(255) NOT NULL,
    distance_km INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE(from_station, to_station)
    );

-- Индексы
CREATE INDEX IF NOT EXISTS idx_wagons_status ON wagons(status);
CREATE INDEX IF NOT EXISTS idx_wagons_current_station ON wagons(current_station);