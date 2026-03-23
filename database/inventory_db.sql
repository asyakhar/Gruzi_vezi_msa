-- Создание базы данных
CREATE DATABASE inventory_db;

-- Подключение к базе
\c inventory_db;

-- Таблица вагонов
CREATE TABLE wagons (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        wagon_number VARCHAR(50) UNIQUE NOT NULL,
                        wagon_type VARCHAR(50) NOT NULL,
                        max_weight_kg INTEGER NOT NULL,
                        max_volume_m3 INTEGER NOT NULL,
                        current_station VARCHAR(255) NOT NULL,
                        status VARCHAR(50) DEFAULT 'свободен'
);

-- Таблица расписания вагонов
CREATE TABLE wagon_schedule (
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

-- Таблица расстояний между станциями
CREATE TABLE station_distances (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   from_station VARCHAR(255) NOT NULL,
                                   to_station VARCHAR(255) NOT NULL,
                                   distance_km INTEGER NOT NULL,
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE(from_station, to_station)
);

-- Индексы
CREATE INDEX idx_wagons_status ON wagons(status);
CREATE INDEX idx_wagons_current_station ON wagons(current_station);
CREATE INDEX idx_wagons_type ON wagons(wagon_type);
CREATE INDEX idx_schedule_wagon ON wagon_schedule(wagon_id);
CREATE INDEX idx_schedule_dates ON wagon_schedule(departure_date, arrival_date);
CREATE INDEX idx_station_distances ON station_distances(from_station, to_station);

-- Вставка данных из монолита
INSERT INTO wagons (id, wagon_number, wagon_type, max_weight_kg, max_volume_m3, current_station, status)
SELECT id, wagon_number, wagon_type, max_weight_kg, max_volume_m3, current_station, status
FROM public.wagons;

INSERT INTO wagon_schedule (id, wagon_id, order_id, departure_station, arrival_station, departure_date, arrival_date, status, cargo_type, cargo_weight_kg, created_at)
SELECT id, wagon_id, order_id, departure_station, arrival_station, departure_date, arrival_date, status, cargo_type, cargo_weight_kg, created_at
FROM public.wagon_schedule;

INSERT INTO station_distances (id, from_station, to_station, distance_km, updated_at)
SELECT id, from_station, to_station, distance_km, updated_at
FROM public.station_distances;