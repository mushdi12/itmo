package com.example.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.logging.Logger;

/**
 * Реализация двухфазного коммита для распределенных транзакций
 * между БД и MinIO файловым хранилищем
 */
@ApplicationScoped
public class DistributedTransactionManager {

    private static final Logger logger = Logger.getLogger(DistributedTransactionManager.class.getName());

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Inject
    private MinIOService minIOService;

    /**
     * Выполняет распределенную транзакцию с двухфазным коммитом
     * 
     * @param dbOperation операция для выполнения в БД
     * @param fileOperation операция для выполнения в MinIO
     * @return результат транзакции
     */
    public <T> T executeDistributedTransaction(
            DatabaseOperation<T> dbOperation,
            FileOperation fileOperation) throws Exception {
        
        Phase phase = Phase.INITIAL;
        String fileObjectName = null;
        T dbResult = null;

        try {
            // ФАЗА 1: PREPARE (подготовка)
            phase = Phase.PREPARE;
            logger.info("Phase 1: PREPARE - Starting distributed transaction");

            // 1.1 Подготовка операции с файлом (сохранение в MinIO)
            if (fileOperation != null && minIOService.isAvailable()) {
                try {
                    fileObjectName = fileOperation.prepare();
                    logger.info("Phase 1: File prepared in MinIO: " + fileObjectName);
                } catch (Exception e) {
                    logger.severe("Phase 1: Failed to prepare file operation: " + e.getMessage());
                    throw new TransactionException("File preparation failed", e);
                }
            }

            // 1.2 Подготовка операции с БД (начало транзакции)
            try {
                dbResult = dbOperation.prepare();
                logger.info("Phase 1: Database operation prepared");
            } catch (Exception e) {
                logger.severe("Phase 1: Failed to prepare database operation: " + e.getMessage());
                // Откатываем файловую операцию
                if (fileObjectName != null) {
                    try {
                        minIOService.deleteFile(fileObjectName);
                    } catch (Exception rollbackEx) {
                        logger.warning("Failed to rollback file operation: " + rollbackEx.getMessage());
                    }
                }
                throw new TransactionException("Database preparation failed", e);
            }

            // ФАЗА 2: COMMIT (фиксация)
            phase = Phase.COMMIT;
            logger.info("Phase 2: COMMIT - Committing distributed transaction");

            // 2.1 Коммит БД (транзакция управляется через @Transactional в вызывающем методе)
            try {
                dbOperation.commit();
                // Не вызываем em.flush() здесь, так как транзакция управляется извне
                logger.info("Phase 2: Database operation committed");
            } catch (Exception e) {
                logger.severe("Phase 2: Failed to commit database operation: " + e.getMessage());
                // Откатываем файловую операцию
                if (fileObjectName != null) {
                    try {
                        minIOService.deleteFile(fileObjectName);
                    } catch (Exception rollbackEx) {
                        logger.warning("Failed to rollback file operation: " + rollbackEx.getMessage());
                    }
                }
                throw new TransactionException("Database commit failed", e);
            }

            // 2.2 Файловая операция уже зафиксирована (MinIO не поддерживает транзакции)
            // В случае ошибки БД файл будет удален выше
            logger.info("Phase 2: File operation committed: " + fileObjectName);

            return dbResult;

        } catch (TransactionException e) {
            logger.severe("Distributed transaction failed in phase: " + phase + ", error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error in distributed transaction: " + e.getMessage());
            // Откатываем файловую операцию при любой неожиданной ошибке
            if (fileObjectName != null) {
                try {
                    minIOService.deleteFile(fileObjectName);
                } catch (Exception rollbackEx) {
                    logger.warning("Failed to rollback file operation: " + rollbackEx.getMessage());
                }
            }
            throw new TransactionException("Unexpected error in distributed transaction", e);
        }
    }

    /**
     * Интерфейс для операций с БД
     */
    @FunctionalInterface
    public interface DatabaseOperation<T> {
        T prepare() throws Exception;
        default void commit() throws Exception {
            // По умолчанию коммит выполняется автоматически через @Transactional
        }
    }

    /**
     * Интерфейс для операций с файлами
     */
    @FunctionalInterface
    public interface FileOperation {
        String prepare() throws Exception;
    }

    /**
     * Фазы двухфазного коммита
     */
    private enum Phase {
        INITIAL, PREPARE, COMMIT
    }

    /**
     * Исключение для ошибок распределенных транзакций
     */
    public static class TransactionException extends Exception {
        public TransactionException(String message) {
            super(message);
        }

        public TransactionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

