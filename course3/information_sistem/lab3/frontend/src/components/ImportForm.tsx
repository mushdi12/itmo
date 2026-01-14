import React, { useState } from 'react';
import { importApi } from '../services/api';

interface ImportFormProps {
  onSuccess: () => void;
}

const ImportForm: React.FC<ImportFormProps> = ({ onSuccess }) => {
  const [file, setFile] = useState<File | null>(null);
  const [userName, setUserName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setError(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!file) {
      setError('Пожалуйста, выберите файл для импорта');
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const result = await importApi.uploadFile(file, userName || undefined);
      
      if (result.success) {
        setSuccess(`Успешно импортировано ${result.objectsCount} объектов!`);
        setFile(null);
        setUserName('');
        setTimeout(() => {
          onSuccess();
        }, 2000);
      } else {
        setError(result.error || 'Ошибка при импорте файла');
      }
    } catch (err: any) {
      setError(err.response?.data?.error || err.message || 'Ошибка при загрузке файла');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="import-form">
      <h2>📥 Импорт объектов из JSON</h2>
      
      <div className="import-info">
        <p><strong>Формат файла JSON:</strong></p>
        <p>Файл должен содержать массив объектов BookCreature в формате JSON. Каждый объект должен включать все поля и вложенные объекты:</p>
        <ul>
          <li><strong>Основной объект:</strong> name, age, creatureType, attackLevel, defenseLevel, creationDate (опционально)</li>
          <li><strong>coordinates:</strong> x (Integer, &gt; -380), y (Float, &lt;= 665)</li>
          <li><strong>creatureLocation (MagicCity):</strong> name, area, population, establishmentDate (опционально), governor, capital, populationDensity</li>
          <li><strong>ring:</strong> name, power (опционально)</li>
        </ul>
        <p><strong>Типы creatureType и governor:</strong> HOBBIT, ELF, HUMAN, GOLLUM</p>
        <p><strong>Формат дат:</strong> ISO 8601 (например: "2024-01-15T10:30:00" для creationDate, "2000-03-15" для establishmentDate)</p>
        <p>Смотрите файл <code>import_example.json</code> для примера.</p>
      </div>

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="userName">Имя пользователя (необязательно):</label>
          <input
            type="text"
            id="userName"
            value={userName}
            onChange={(e) => setUserName(e.target.value)}
            placeholder="Введите ваше имя"
          />
        </div>

        <div className="form-group">
          <label htmlFor="file">Выберите JSON файл:</label>
          <input
            type="file"
            id="file"
            accept=".json"
            onChange={handleFileChange}
            required
          />
          {file && <p className="file-info">Выбран файл: {file.name}</p>}
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <button type="submit" disabled={loading || !file} className="btn btn-primary">
          {loading ? 'Импорт...' : 'Импортировать'}
        </button>
      </form>
    </div>
  );
};

export default ImportForm;

