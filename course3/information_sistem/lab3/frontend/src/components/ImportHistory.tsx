import React, { useState, useEffect } from 'react';
import { importApi } from '../services/api';
import { ImportHistory as ImportHistoryType } from '../types';

const ImportHistory: React.FC = () => {
  const [history, setHistory] = useState<ImportHistoryType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [downloading, setDownloading] = useState<number | null>(null);

  useEffect(() => {
    loadHistory();
  }, []);

  const loadHistory = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await importApi.getHistory();
      setHistory(data);
    } catch (err: any) {
      setError(err.response?.data?.error || err.message || 'Ошибка при загрузке истории');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleString('ru-RU');
    } catch {
      return dateString;
    }
  };

  const getStatusBadge = (status: string) => {
    if (status === 'SUCCESS') {
      return <span className="status-badge success">Успешно</span>;
    }
    return <span className="status-badge failed">Ошибка</span>;
  };

  const handleDownloadFile = async (historyId: number) => {
    if (!historyId) return;
    
    setDownloading(historyId);
    try {
      await importApi.downloadFile(historyId);
    } catch (err: any) {
      alert(err.response?.data?.error || err.message || 'Ошибка при скачивании файла');
    } finally {
      setDownloading(null);
    }
  };

  if (loading) {
    return <div className="loading">Загрузка истории импорта...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  return (
    <div className="import-history">
      <h2>📋 История импорта</h2>
      
      {history.length === 0 ? (
        <p>История импорта пуста</p>
      ) : (
        <table className="history-table">
          <thead>
            <tr>
              <th>ID операции</th>
              <th>Статус</th>
              <th>Пользователь</th>
              <th>Дата и время</th>
              <th>Количество объектов</th>
              <th>Файл</th>
              <th>Сообщение об ошибке</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{getStatusBadge(item.status)}</td>
                <td>{item.userName || 'Анонимный'}</td>
                <td>{formatDate(item.createdAt)}</td>
                <td>
                  {item.status === 'SUCCESS' && item.objectsCount !== undefined
                    ? item.objectsCount
                    : '-'}
                </td>
                <td>
                  {item.fileObjectName ? (
                    <button
                      className="download-btn"
                      onClick={() => handleDownloadFile(item.id!)}
                      disabled={downloading === item.id}
                      title="Скачать файл"
                    >
                      {downloading === item.id ? '⏳' : '📥'} Скачать
                    </button>
                  ) : (
                    <span style={{ color: '#888' }}>-</span>
                  )}
                </td>
                <td className="error-cell">
                  {item.status === 'FAILED' && item.errorMessage
                    ? <span title={item.errorMessage}>{item.errorMessage.substring(0, 100)}...</span>
                    : '-'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ImportHistory;

