import React, { useState, useEffect } from 'react';
import { cacheStatisticsApi } from '../services/api';

const CacheStatisticsControl: React.FC = () => {
  const [enabled, setEnabled] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    loadStatus();
  }, []);

  const loadStatus = async () => {
    try {
      const response = await cacheStatisticsApi.getLoggingStatus();
      setEnabled(response.enabled);
    } catch (err: any) {
      console.error('Ошибка при загрузке статуса:', err);
    }
  };

  const handleEnable = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const response = await cacheStatisticsApi.enableLogging();
      setEnabled(true);
      setMessage(response.message);
      setTimeout(() => setMessage(null), 3000);
    } catch (err: any) {
      setMessage('Ошибка при включении логирования');
    } finally {
      setLoading(false);
    }
  };

  const handleDisable = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const response = await cacheStatisticsApi.disableLogging();
      setEnabled(false);
      setMessage(response.message);
      setTimeout(() => setMessage(null), 3000);
    } catch (err: any) {
      setMessage('Ошибка при отключении логирования');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="cache-statistics-control">
      <h3>📊 Управление логированием статистики L2 Cache</h3>
      
      <div className="cache-control-panel">
        <div className="cache-status">
          <span className="status-label">Статус:</span>
          <span className={`status-indicator ${enabled ? 'enabled' : 'disabled'}`}>
            {enabled ? '✅ Включено' : '❌ Выключено'}
          </span>
        </div>

        <div className="cache-buttons">
          <button
            className="btn"
            onClick={handleEnable}
            disabled={loading || enabled}
          >
            Включить логирование
          </button>
          <button
            className="btn btn-secondary"
            onClick={handleDisable}
            disabled={loading || !enabled}
          >
            Выключить логирование
          </button>
        </div>

        {message && (
          <div className={`message ${enabled ? 'success-message' : 'info-message'}`}>
            {message}
          </div>
        )}

        <div className="cache-info">
          <p>
            Логирование статистики кэша показывает информацию о hits (попаданиях), 
            misses (промахах) и hit rate (проценте попаданий) для L2 JPA Cache.
          </p>
          <p>
            Статистика отображается в логах сервера при выполнении методов, 
            помеченных аннотацией <code>@CacheStatisticsLogging</code>.
          </p>
        </div>
      </div>
    </div>
  );
};

export default CacheStatisticsControl;

