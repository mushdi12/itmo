-- SQL скрипты для анализа результатов JMeter тестирования
-- Используйте эти запросы после выполнения тестов для проверки консистентности данных

-- 1. Проверка общего количества созданных объектов
SELECT 
    'BookCreature' as table_name,
    COUNT(*) as total_count,
    MIN(id) as min_id,
    MAX(id) as max_id
FROM book_creature
UNION ALL
SELECT 
    'MagicCity',
    COUNT(*),
    MIN(id),
    MAX(id)
FROM magic_city
UNION ALL
SELECT 
    'Ring',
    COUNT(*),
    MIN(id),
    MAX(id)
FROM ring
UNION ALL
SELECT 
    'Coordinates',
    COUNT(*),
    MIN(id),
    MAX(id)
FROM coordinates;

-- 2. Проверка истории импортов (все операции импорта)
SELECT 
    id,
    status,
    user_name,
    created_at,
    objects_count,
    CASE 
        WHEN LENGTH(error_message) > 100 THEN LEFT(error_message, 100) || '...'
        ELSE error_message
    END as error_preview
FROM import_history
ORDER BY created_at DESC
LIMIT 50;

-- 3. Статистика по статусам импортов
SELECT 
    status,
    COUNT(*) as count,
    SUM(objects_count) as total_objects_imported,
    AVG(objects_count) as avg_objects_per_import
FROM import_history
GROUP BY status;

-- 4. Проверка объектов, созданных JMeter (по именам)
SELECT 
    name,
    COUNT(*) as count
FROM book_creature
WHERE name LIKE 'JMeter_%' 
   OR name LIKE 'Import_%'
   OR name LIKE 'Test_%'
   OR name LIKE 'Updated_%'
GROUP BY name
HAVING COUNT(*) > 1
ORDER BY count DESC;

-- 5. Проверка на дубликаты по имени (если есть уникальное ограничение)
-- ВНИМАНИЕ: Это может не работать, если name не уникально
SELECT 
    name,
    COUNT(*) as duplicate_count,
    STRING_AGG(id::text, ', ') as ids
FROM book_creature
GROUP BY name
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC;

-- 6. Проверка объектов, созданных за последний час (для анализа тестов)
SELECT 
    COUNT(*) as recent_creatures,
    MIN(creation_date) as earliest,
    MAX(creation_date) as latest
FROM book_creature
WHERE creation_date >= NOW() - INTERVAL '1 hour';

-- 7. Проверка целостности связей
-- Объекты без координат, города или кольца
SELECT 
    'Creatures without coordinates' as issue,
    COUNT(*) as count
FROM book_creature bc
WHERE bc.coordinates_id IS NULL
UNION ALL
SELECT 
    'Creatures without city',
    COUNT(*)
FROM book_creature bc
WHERE bc.creature_location_id IS NULL
UNION ALL
SELECT 
    'Creatures without ring',
    COUNT(*)
FROM book_creature bc
WHERE bc.ring_id IS NULL;

-- 8. Проверка "потерянных" объектов (созданных, но потом удаленных)
-- Если вы сохраняете логи удалений или используете soft delete
SELECT 
    'This query would require additional tracking' as note;

-- 9. Статистика по типам существ
SELECT 
    creature_type,
    COUNT(*) as count,
    AVG(age) as avg_age,
    AVG(attack_level) as avg_attack,
    AVG(defense_level) as avg_defense
FROM book_creature
GROUP BY creature_type
ORDER BY count DESC;

-- 10. Проверка транзакций импорта (успешные vs неудачные)
SELECT 
    DATE_TRUNC('minute', created_at) as minute,
    status,
    COUNT(*) as import_count,
    SUM(objects_count) as total_objects
FROM import_history
WHERE created_at >= NOW() - INTERVAL '1 hour'
GROUP BY DATE_TRUNC('minute', created_at), status
ORDER BY minute DESC, status;

-- 11. Найти объекты с одинаковыми данными (кроме ID)
-- Это может указывать на lost updates или дубликаты
SELECT 
    bc1.id as id1,
    bc2.id as id2,
    bc1.name,
    bc1.age,
    bc1.attack_level
FROM book_creature bc1
JOIN book_creature bc2 ON bc1.id < bc2.id
WHERE bc1.name = bc2.name
  AND bc1.age = bc2.age
  AND ABS(bc1.attack_level - bc2.attack_level) < 0.01
LIMIT 20;

-- 12. Проверка последовательности ID (выявление пропусков, которые могут указывать на откаты)
SELECT 
    id,
    LAG(id) OVER (ORDER BY id) as prev_id,
    id - LAG(id) OVER (ORDER BY id) as gap
FROM book_creature
ORDER BY id
LIMIT 100;

