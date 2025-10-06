import React, { useState, useEffect } from 'react';
import { BookCreature } from '../types';
import { bookCreatureApi } from '../services/api';
import BookCreatureCard from './BookCreatureCard';

interface Props { onEdit?: (creature: BookCreature) => void }

const BookCreatureList: React.FC<Props> = ({ onEdit }) => {
  const [creatures, setCreatures] = useState<BookCreature[]>([]);
  const [filter, setFilter] = useState('');
  const [sortKey, setSortKey] = useState<'name' | 'creatureType'>('name');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadCreatures();
  }, []);

  const loadCreatures = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await bookCreatureApi.getAll();
      setCreatures(data);
    } catch (err) {
      setError('Ошибка при загрузке существ');
      console.error('Error loading creatures:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить это существо?')) {
      try {
        await bookCreatureApi.delete(id);
        setCreatures(creatures.filter(creature => creature.id !== id));
      } catch (err) {
        setError('Ошибка при удалении существа');
        console.error('Error deleting creature:', err);
      }
    }
  };

  // Special actions UI
  const [attackFilter, setAttackFilter] = useState('');
  const [nameSubstring, setNameSubstring] = useState('');
  const [specialError, setSpecialError] = useState<string | null>(null);
  const [specialSuccess, setSpecialSuccess] = useState<string | null>(null);
  const [swapId1, setSwapId1] = useState('');
  const [swapId2, setSwapId2] = useState('');

  const handleDeleteByAttack = async () => {
    setSpecialError(null); setSpecialSuccess(null);
    const val = Number(attackFilter);
    if (!attackFilter || isNaN(val)) { setSpecialError('Введите число attackLevel'); return; }
    try {
      const res = await bookCreatureApi.deleteByAttackLevel(val);
      setSpecialSuccess(`Удалено: ${res.deleted}`);
      await loadCreatures();
    } catch (e) { setSpecialError('Ошибка удаления по attackLevel'); }
  };

  const handleSearchByName = async () => {
    setSpecialError(null); setSpecialSuccess(null);
    if (!nameSubstring) { setSpecialError('Введите подстроку'); return; }
    try {
      const res = await bookCreatureApi.searchByNameSubstring(nameSubstring);
      setCreatures(res);
      setSpecialSuccess(`Найдено: ${res.length}`);
    } catch (e) { setSpecialError('Ошибка поиска'); }
  };

  const handleDistinctAttack = async () => {
    setSpecialError(null); setSpecialSuccess(null);
    try {
      const levels = await bookCreatureApi.distinctAttackLevels();
      setSpecialSuccess(`Уникальные уровни атаки: ${levels.join(', ')}`);
    } catch (e) { setSpecialError('Ошибка получения уникальных уровней'); }
  };

  const handleSwapRings = async () => {
    setSpecialError(null); setSpecialSuccess(null);
    const a = Number(swapId1); const b = Number(swapId2);
    if (!swapId1 || !swapId2 || isNaN(a) || isNaN(b)) { setSpecialError('Введите корректные id двух персонажей'); return; }
    try {
      const res = await bookCreatureApi.swapRings(a, b);
      if (res && (res as any).swapped) {
        setSpecialSuccess('Кольца успешно обменены');
        await loadCreatures();
      } else {
        setSpecialError('Не удалось обменять (проверьте id)');
      }
    } catch (e) { setSpecialError('Ошибка обмена кольцами'); }
  };

  const filtered = creatures.filter(c => {
    if (!filter) return true;
    return (
      c.name === filter ||
      String(c.creatureType) === filter
    );
  });

  const sorted = [...filtered].sort((a, b) => {
    const av = String(a[sortKey]).localeCompare(String(b[sortKey]));
    return sortDir === 'asc' ? av : -av;
  });

  if (loading) {
    return <div className="loading">🔄 Загрузка существ...</div>;
  }

  if (error) {
    return (
      <div className="error">
        {error}
        <button className="btn" onClick={loadCreatures} style={{ marginLeft: '1rem' }}>
          Попробовать снова
        </button>
      </div>
    );
  }

  if (creatures.length === 0) {
    return (
      <div className="card">
        <h2>📚 Существа</h2>
        <p>Существа не найдены. Создайте первое существо!</p>
      </div>
    );
  }

  return (
    <div>
      <div className="card">
        <h2>📚 Существа ({creatures.length})</h2>
        <p>Управление магическими существами из книг</p>
        <div className="filter-bar">
          <div className="filter-group">
            <input
              className="input"
              placeholder="Фильтр: точное совпадение (например: Frodo, ELF)"
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
            />
            <button className="btn btn-secondary" onClick={() => setFilter('')}>Очистить</button>
          </div>
          <div className="filter-group">
            <select className="select" value={sortKey} onChange={(e) => setSortKey(e.target.value as any)}>
              <option value="name">Сортировать по имени</option>
              <option value="creatureType">Сортировать по типу</option>
            </select>
            <select className="select" value={sortDir} onChange={(e) => setSortDir(e.target.value as any)}>
              <option value="asc">По возрастанию</option>
              <option value="desc">По убыванию</option>
            </select>
          </div>
        </div>
        <div className="filter-bar" style={{ marginTop: '1rem' }}>
          <div className="filter-group">
            <input className="input" placeholder="Удалить по attackLevel (точное число)" value={attackFilter} onChange={(e) => setAttackFilter(e.target.value)} />
            <button className="btn btn-danger" onClick={handleDeleteByAttack}>Удалить</button>
          </div>
          <div className="filter-group">
            <input className="input" placeholder="Найти по подстроке имени" value={nameSubstring} onChange={(e) => setNameSubstring(e.target.value)} />
            <button className="btn" onClick={handleSearchByName}>Искать</button>
            <button className="btn btn-secondary" onClick={handleDistinctAttack}>Уникальные attackLevel</button>
          </div>
        </div>
        <div className="filter-bar" style={{ marginTop: '0.5rem' }}>
          <div className="filter-group">
            <input className="input" placeholder="ID персонажа A" value={swapId1} onChange={(e) => setSwapId1(e.target.value)} />
            <input className="input" placeholder="ID персонажа B" value={swapId2} onChange={(e) => setSwapId2(e.target.value)} />
            <button className="btn" onClick={handleSwapRings}>Обменять кольца</button>
          </div>
        </div>
        {specialError && <div className="error" style={{ marginTop: '0.5rem' }}>{specialError}</div>}
        {specialSuccess && <div className="success" style={{ marginTop: '0.5rem' }}>{specialSuccess}</div>}
      </div>
      
      <div className="card-grid">
        {sorted.map(creature => (
          <BookCreatureCard
            key={creature.id}
            creature={creature}
            onDelete={handleDelete}
            onEdit={onEdit}
          />
        ))}
      </div>
    </div>
  );
};

export default BookCreatureList;
