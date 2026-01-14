package com.example.lab1.services;

import com.example.lab1.dto.BookCreatureType;
import com.example.lab1.entities.*;
import com.example.lab1.repositories.*;
import com.example.lab1.interceptors.CacheStatisticsLogging;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class ImportService {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Inject
    private ImportHistoryRepository importHistoryRepository;

    @Inject
    private DistributedTransactionManager transactionManager;

    @Inject
    private MinIOService minIOService;

    private final Validator validator;

    public ImportService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(rollbackOn = Exception.class)
    public ImportResult importFromCSV(InputStream inputStream, String userName) {
        ImportHistory history = new ImportHistory();
        history.setUserName(userName);
        history.setStatus("FAILED");
        history.setCreatedAt(LocalDateTime.now());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Файл пустой");
            }

            List<BookCreature> creatures = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    BookCreature creature = parseLine(line);
                    validateCreature(creature, errors, lineNumber);
                    creatures.add(creature);
                } catch (Exception e) {
                    errors.add("Строка " + lineNumber + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                history.setErrorMessage(String.join("; ", errors));

                saveHistory(history);
                throw new IllegalArgumentException("Ошибки валидации: " + String.join("; ", errors));
            }


            em.getTransaction().begin();
            try {
                for (BookCreature creature : creatures) {

                    Coordinates coord = creature.getCoordinates();
                    if (coord.getId() == null) {
                        em.persist(coord);
                    }

                    MagicCity city = creature.getCreatureLocation();
                    if (city.getId() == null) {
                        em.persist(city);
                    }

                    Ring ring = creature.getRing();
                    if (ring.getId() == null) {
                        em.persist(ring);
                    }

                    if (creature.getId() == null) {
                        em.persist(creature);
                    }
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                history.setStatus("FAILED");
                history.setErrorMessage("Ошибка при сохранении: " + e.getMessage());
                saveHistory(history);
                throw e;
            }

            history.setStatus("SUCCESS");
            history.setObjectsCount(creatures.size());
            saveHistory(history);

            return new ImportResult(true, creatures.size(), history.getId(), null);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            history.setErrorMessage(e.getMessage());
            saveHistory(history);
            throw new RuntimeException("Ошибка импорта: " + e.getMessage(), e);
        }
    }

    @Transactional
    private ImportHistory saveHistory(ImportHistory history) {
        return importHistoryRepository.create(history);
    }

    @Transactional(rollbackOn = Exception.class)
    @CacheStatisticsLogging
    public ImportResult importFromJSON(List<com.example.lab1.dto.BookCreatureDto> creatureDtos, String userName) {
        return importFromJSON(creatureDtos, userName, null, null);
    }

    @Transactional(rollbackOn = Exception.class)
    @CacheStatisticsLogging
    public ImportResult importFromJSON(List<com.example.lab1.dto.BookCreatureDto> creatureDtos, String userName, InputStream fileInputStream, String fileName) {
        ImportHistory history = new ImportHistory();
        history.setUserName(userName != null && !userName.trim().isEmpty() ? userName : "anonymous");
        history.setStatus("FAILED");
        history.setCreatedAt(LocalDateTime.now());

        try {
            if (creatureDtos == null || creatureDtos.isEmpty()) {
                throw new IllegalArgumentException("Список объектов пуст");
            }

            List<BookCreature> creatures = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            // Валидируем и преобразуем все DTO в Entity
            for (int i = 0; i < creatureDtos.size(); i++) {
                int index = i + 1;
                try {
                    com.example.lab1.dto.BookCreatureDto dto = creatureDtos.get(i);
                    BookCreature creature = com.example.lab1.mappers.BookCreatureMapper.toEntity(dto);
                    
                    // Устанавливаем дату создания, если не указана
                    if (creature.getCreationDate() == null) {
                        creature.setCreationDate(LocalDateTime.now());
                    }
                    
                    validateCreature(creature, errors, index);
                    creatures.add(creature);
                } catch (Exception e) {
                    errors.add("Объект " + index + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                history.setErrorMessage(String.join("; ", errors));
                saveHistory(history);
                throw new IllegalArgumentException("Ошибки валидации: " + String.join("; ", errors));
            }

            // Сохраняем содержимое файла в память для загрузки в MinIO
            byte[] fileContent = null;
            if (fileInputStream != null) {
                fileContent = fileInputStream.readAllBytes();
            }

            final byte[] finalFileContent = fileContent;
            final String finalFileName = fileName != null ? fileName : "import_" + System.currentTimeMillis() + ".json";
            final List<BookCreature> finalCreatures = creatures;
            final ImportHistory finalHistory = history;

            // Переменная для хранения имени файла из MinIO
            final String[] fileObjectNameHolder = new String[1];

            // Если файл не предоставлен или MinIO недоступен, используем простую транзакцию
            if (finalFileContent == null || !minIOService.isAvailable()) {
                // Простое сохранение в БД без файла
                for (BookCreature creature : finalCreatures) {
                    // Сохраняем вложенные объекты
                    Coordinates coord = creature.getCoordinates();
                    if (coord.getId() == null) {
                        em.persist(coord);
                    }

                    MagicCity city = creature.getCreatureLocation();
                    if (city.getId() == null) {
                        em.persist(city);
                    }

                    Ring ring = creature.getRing();
                    if (ring.getId() == null) {
                        em.persist(ring);
                    }

                    // Сохраняем основной объект
                    if (creature.getId() == null) {
                        em.persist(creature);
                    }
                }

                // Сохраняем историю импорта
                finalHistory.setStatus("SUCCESS");
                finalHistory.setObjectsCount(finalCreatures.size());
                ImportHistory savedHistory = saveHistory(finalHistory);
                return new ImportResult(true, finalCreatures.size(), savedHistory.getId(), null);
            }

            // Используем распределенную транзакцию только если файл предоставлен и MinIO доступен
            ImportHistory savedHistory = transactionManager.executeDistributedTransaction(
                // Операция с БД
                new DistributedTransactionManager.DatabaseOperation<ImportHistory>() {
                    @Override
                    public ImportHistory prepare() throws Exception {
                        // Сохраняем все объекты в БД
                        for (BookCreature creature : finalCreatures) {
                            // Сохраняем вложенные объекты
                            Coordinates coord = creature.getCoordinates();
                            if (coord.getId() == null) {
                                em.persist(coord);
                            }

                            MagicCity city = creature.getCreatureLocation();
                            if (city.getId() == null) {
                                em.persist(city);
                            }

                            Ring ring = creature.getRing();
                            if (ring.getId() == null) {
                                em.persist(ring);
                            }

                            // Сохраняем основной объект
                            if (creature.getId() == null) {
                                em.persist(creature);
                            }
                        }

                        // Сохраняем историю импорта с именем файла
                        finalHistory.setStatus("SUCCESS");
                        finalHistory.setObjectsCount(finalCreatures.size());
                        if (fileObjectNameHolder[0] != null) {
                            finalHistory.setFileObjectName(fileObjectNameHolder[0]);
                        }
                        return saveHistory(finalHistory);
                    }
                },
                // Операция с файлом
                new DistributedTransactionManager.FileOperation() {
                    @Override
                    public String prepare() throws Exception {
                        ByteArrayInputStream bais = new ByteArrayInputStream(finalFileContent);
                        String objectName = minIOService.uploadFile(bais, finalFileName, "application/json", finalFileContent.length);
                        fileObjectNameHolder[0] = objectName;
                        return objectName;
                    }
                }
            );

            return new ImportResult(true, finalCreatures.size(), savedHistory.getId(), null);

        } catch (IllegalArgumentException e) {
            // Уже обработано выше
            throw e;
        } catch (DistributedTransactionManager.TransactionException e) {
            history.setErrorMessage("Ошибка распределенной транзакции: " + e.getMessage());
            saveHistory(history);
            throw new RuntimeException("Ошибка импорта: " + e.getMessage(), e);
        } catch (Exception e) {
            history.setErrorMessage(e.getMessage());
            saveHistory(history);
            throw new RuntimeException("Ошибка импорта: " + e.getMessage(), e);
        }
    }

    private BookCreature parseLine(String line) throws ParseException {
        String[] fields = parseCSVLine(line);
        
        if (fields.length < 17) {
            throw new IllegalArgumentException("Недостаточно полей в строке (ожидается 17, получено " + fields.length + ")");
        }

        // Основной объект BookCreature
        String name = fields[0].trim();
        long age = Long.parseLong(fields[1].trim());
        BookCreatureType creatureType = BookCreatureType.valueOf(fields[2].trim().toUpperCase());
        Double attackLevel = Double.parseDouble(fields[3].trim());
        long defenseLevel = Long.parseLong(fields[4].trim());
        String creationDateStr = fields[5].trim();
        LocalDateTime creationDate = creationDateStr.isEmpty() ? LocalDateTime.now() 
                : LocalDateTime.parse(creationDateStr, DATE_TIME_FORMATTER);

        // Coordinates
        Integer coordX = Integer.parseInt(fields[6].trim());
        Float coordY = Float.parseFloat(fields[7].trim());

        // MagicCity
        String cityName = fields[8].trim();
        Long cityArea = Long.parseLong(fields[9].trim());
        long cityPopulation = Long.parseLong(fields[10].trim());
        String establishmentDateStr = fields[11].trim();
        Date establishmentDate = establishmentDateStr.isEmpty() ? null 
                : DATE_FORMAT.parse(establishmentDateStr);
        BookCreatureType governor = BookCreatureType.valueOf(fields[12].trim().toUpperCase());
        boolean capital = Boolean.parseBoolean(fields[13].trim());
        Long populationDensity = Long.parseLong(fields[14].trim());

        // Ring
        String ringName = fields[15].trim();
        String ringPowerStr = fields[16].trim();
        Long ringPower = ringPowerStr.isEmpty() ? null : Long.parseLong(ringPowerStr);

        // Создаем объекты
        Coordinates coordinates = new Coordinates(coordX, coordY);
        
        MagicCity city = new MagicCity();
        city.setName(cityName);
        city.setArea(cityArea);
        city.setPopulation(cityPopulation);
        city.setEstablishmentDate(establishmentDate);
        city.setGovernor(governor);
        city.setCapital(capital);
        city.setPopulationDensity(populationDensity);

        Ring ring = new Ring(ringName, ringPower);

        BookCreature creature = new BookCreature();
        creature.setName(name);
        creature.setCoordinates(coordinates);
        creature.setCreationDate(creationDate);
        creature.setAge(age);
        creature.setCreatureType(creatureType);
        creature.setCreatureLocation(city);
        creature.setAttackLevel(attackLevel);
        creature.setDefenseLevel(defenseLevel);
        creature.setRing(ring);

        return creature;
    }

    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    private void validateCreature(BookCreature creature, List<String> errors, int lineNumber) {
        Set<ConstraintViolation<BookCreature>> violations = validator.validate(creature);
        for (ConstraintViolation<BookCreature> violation : violations) {
            errors.add("Строка " + lineNumber + ": " + violation.getPropertyPath() + " - " + violation.getMessage());
        }

        Set<ConstraintViolation<Coordinates>> coordViolations = validator.validate(creature.getCoordinates());
        for (ConstraintViolation<Coordinates> violation : coordViolations) {
            errors.add("Строка " + lineNumber + ": coordinates." + violation.getPropertyPath() + " - " + violation.getMessage());
        }

        Set<ConstraintViolation<MagicCity>> cityViolations = validator.validate(creature.getCreatureLocation());
        for (ConstraintViolation<MagicCity> violation : cityViolations) {
            errors.add("Строка " + lineNumber + ": city." + violation.getPropertyPath() + " - " + violation.getMessage());
        }

        Set<ConstraintViolation<Ring>> ringViolations = validator.validate(creature.getRing());
        for (ConstraintViolation<Ring> violation : ringViolations) {
            errors.add("Строка " + lineNumber + ": ring." + violation.getPropertyPath() + " - " + violation.getMessage());
        }
    }

    public static class ImportResult {
        private boolean success;
        private int objectsCount;
        private Long historyId;
        private String errorMessage;

        public ImportResult(boolean success, int objectsCount, Long historyId, String errorMessage) {
            this.success = success;
            this.objectsCount = objectsCount;
            this.historyId = historyId;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getObjectsCount() {
            return objectsCount;
        }

        public Long getHistoryId() {
            return historyId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

