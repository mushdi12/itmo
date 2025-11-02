import React, { useState, useEffect } from 'react';
import { MagicCity } from '../types';
import { magicCityApi } from '../services/api';
import MagicCityCard from './MagicCityCard';

interface Props { onEdit?: (city: MagicCity) => void }

const MagicCityList: React.FC<Props> = ({ onEdit }) => {
  const [cities, setCities] = useState<MagicCity[]>([]);
  const [filter, setFilter] = useState('');
  const [sortKey, setSortKey] = useState<'name' | 'governor'>('name');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadCities();
  }, []);

  const loadCities = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await magicCityApi.getAll();
      setCities(data);
    } catch (err) {
      setError('Ошибка при загрузке городов');
      console.error('Error loading cities:', err);
    } finally {
      setLoading(false);
    }
  };

  const [cityActionMsg, setCityActionMsg] = useState<string | null>(null);
  const destroyElfCities = async () => {
    setCityActionMsg(null);
    try {
      const res = await magicCityApi.deleteElfCities();
      setCityActionMsg(`Удалено городов эльфов: ${res.deleted}`);
      await loadCities();
    } catch (e) {
      setCityActionMsg('Ошибка удаления городов эльфов');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить этот город?')) {
      try {
        await magicCityApi.delete(id);
        setCities(cities.filter(city => city.id !== id));
      } catch (err) {
        setError('Ошибка при удалении города');
        console.error('Error deleting city:', err);
      }
    }
  };

  const filtered = cities.filter(c => {
    if (!filter) return true;
    // Фильтрация по полному совпадению только по строковым колонкам
    return (
      c.name === filter ||
      String(c.governor) === filter
    );
  });

  const sorted = [...filtered].sort((a, b) => {
    const av = String(a[sortKey]).localeCompare(String(b[sortKey]));
    return sortDir === 'asc' ? av : -av;
  });

  if (loading) {
    return <div className="loading">🏰 Загрузка городов...</div>;
  }

  if (error) {
    return (
      <div className="error">
        {error}
        <button className="btn" onClick={loadCities} style={{ marginLeft: '1rem' }}>
          Попробовать снова
        </button>
      </div>
    );
  }

  if (cities.length === 0) {
    return (
      <div className="card">
        <h2>🏰 Магические города</h2>
        <p>Города не найдены. Создайте первый город!</p>
      </div>
    );
  }

  return (
    <div>
      <div className="card">
        <h2>🏰 Магические города ({cities.length})</h2>
        <p>Управление магическими городами и их жителями</p>
        <div className="filter-bar">
          <div className="filter-group">
            <input
              className="input"
              placeholder="Фильтр: точное совпадение (например: Rivendell, ELF)"
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
            />
            <button className="btn btn-secondary" onClick={() => setFilter('')}>Очистить</button>
          </div>
          <div className="filter-group">
            <select className="select" value={sortKey} onChange={(e) => setSortKey(e.target.value as any)}>
              <option value="name">Сортировать по названию</option>
              <option value="governor">Сортировать по правителю</option>
            </select>
            <select className="select" value={sortDir} onChange={(e) => setSortDir(e.target.value as any)}>
              <option value="asc">По возрастанию</option>
              <option value="desc">По убыванию</option>
            </select>
          </div>
        </div>
        <div className="filter-bar" style={{ marginTop: '1rem' }}>
          <div className="filter-group">
            <button className="btn btn-danger" onClick={destroyElfCities}>Уничтожить города эльфов</button>
          </div>
          {cityActionMsg && <div className={cityActionMsg.startsWith('Ошибка') ? 'error' : 'success'}>{cityActionMsg}</div>}
        </div>
      </div>
      
      <div className="card-grid">
        {sorted.map(city => (
          <MagicCityCard
            key={city.id}
            city={city}
            onDelete={handleDelete}
            onEdit={onEdit}
          />
        ))}
      </div>
    </div>
  );
};

export default MagicCityList;
