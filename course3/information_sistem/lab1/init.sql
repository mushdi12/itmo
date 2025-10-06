-- Таблица координат
CREATE TABLE coordinates (
                             id SERIAL PRIMARY KEY,
                             x INT NOT NULL CHECK (x > -380),
                             y REAL NOT NULL CHECK (y <= 665)
);

-- Таблица колец
CREATE TABLE ring (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR NOT NULL,
                      power BIGINT CHECK (power > 0)
);
INSERT INTO coordinates (x, y) VALUES (10, 20.5);
INSERT INTO ring (name, power) VALUES ('Ring of Power', 1000);
-- Таблица городов
CREATE TABLE magic_city (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR NOT NULL,
                            area BIGINT NOT NULL CHECK (area > 0),
                            population BIGINT NOT NULL CHECK (population > 0),
                            establishment_date DATE,
                            governor VARCHAR NOT NULL,
                            capital BOOLEAN,
                            population_density BIGINT CHECK (population_density > 0)
);

INSERT INTO magic_city (name, area, population, governor, capital, population_density)
VALUES ('Gondor', 500, 20000, 'HUMAN', TRUE, 40);
-- Таблица существ
CREATE TABLE book_creature (
                               id SERIAL PRIMARY KEY,
                               name VARCHAR NOT NULL,
                               coordinates_id INT NOT NULL REFERENCES coordinates(id),
                               creation_date TIMESTAMP,
                               age BIGINT NOT NULL CHECK (age > 0),
                               creature_type VARCHAR NOT NULL,
                               creature_location_id INT NOT NULL REFERENCES magic_city(id),
                               attack_level DOUBLE PRECISION NOT NULL CHECK (attack_level > 0),
                               defense_level BIGINT NOT NULL CHECK (defense_level > 0),
                               ring_id INT NOT NULL REFERENCES ring(id)
);



