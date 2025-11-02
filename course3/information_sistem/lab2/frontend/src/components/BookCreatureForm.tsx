import React, { useState, useEffect } from 'react';
import { BookCreatureDto, BookCreatureType, MagicCity, Coordinates, Ring } from '../types';
import { bookCreatureApi, magicCityApi, ringsApi, coordsApi } from '../services/api';

interface BookCreatureFormProps {
  onSuccess: () => void;
  creature?: BookCreatureDto;
}

const BookCreatureForm: React.FC<BookCreatureFormProps> = ({ onSuccess, creature }) => {
  const [formData, setFormData] = useState<BookCreatureDto>({
    name: '',
    age: 0,
    creatureType: BookCreatureType.HUMAN,
    attackLevel: 0,
    defenseLevel: 0,
    coordinates: { x: 0, y: 0 },
    creatureLocation: { 
      name: '', 
      area: 0, 
      population: 0, 
      governor: BookCreatureType.HUMAN, 
      capital: false, 
      populationDensity: 0 
    },
    ring: { name: '', power: 0 }
  });

  const [cities, setCities] = useState<MagicCity[]>([]);
  const [rings, setRings] = useState<Ring[]>([]);
  const [coords, setCoords] = useState<Coordinates[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (creature) {
      setFormData(creature);
    }
    loadCities();
    loadRingsAndCoords();
  }, [creature]);

  const loadCities = async () => {
    try {
      const data = await magicCityApi.getAll();
      setCities(data);
    } catch (err) {
      console.error('Error loading cities:', err);
    }
  };

  const loadRingsAndCoords = async () => {
    try {
      const [ringsRes, coordsRes] = await Promise.all([
        ringsApi.getAll(),
        coordsApi.getAll()
      ]);
      setRings(ringsRes);
      setCoords(coordsRes);
    } catch (err) {
      console.error('Error loading rings/coords:', err);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    
    if (name.startsWith('coordinates.')) {
      const coordField = name.split('.')[1] as keyof Coordinates;
      setFormData(prev => ({
        ...prev,
        coordinates: {
          ...prev.coordinates,
          [coordField]: type === 'number' ? Number(value) : value
        }
      }));
    } else if (name.startsWith('creatureLocation.')) {
      const cityField = name.split('.')[1] as keyof MagicCity;
      setFormData(prev => ({
        ...prev,
        creatureLocation: {
          ...prev.creatureLocation,
          [cityField]: type === 'number' ? Number(value) : 
                      type === 'checkbox' ? (e.target as HTMLInputElement).checked : value
        }
      }));
    } else if (name.startsWith('ring.')) {
      const ringField = name.split('.')[1] as keyof Ring;
      setFormData(prev => ({
        ...prev,
        ring: {
          ...prev.ring,
          [ringField]: type === 'number' ? Number(value) : value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: type === 'number' ? Number(value) : value
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      if (creature && creature.id) {
        await bookCreatureApi.update(creature.id, formData);
        setSuccess('Существо успешно обновлено!');
      } else {
        await bookCreatureApi.create(formData);
        setSuccess('Существо успешно создано!');
      }
      
      setTimeout(() => {
        onSuccess();
      }, 1500);
    } catch (err) {
      setError('Ошибка при сохранении существа');
      console.error('Error saving creature:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <div className="card">
        <h2>🧙‍♂️ {creature ? 'Редактировать существо' : 'Создать новое существо'}</h2>
        
        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Имя существа *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              required
              placeholder="Введите имя существа"
            />
          </div>

          <div className="form-group">
            <label htmlFor="creatureType">Тип существа *</label>
            <select
              id="creatureType"
              name="creatureType"
              value={formData.creatureType}
              onChange={handleInputChange}
              required
            >
              <option value={BookCreatureType.HOBBIT}>🧙‍♂️ Хоббит</option>
              <option value={BookCreatureType.ELF}>🧝‍♀️ Эльф</option>
              <option value={BookCreatureType.HUMAN}>👤 Человек</option>
              <option value={BookCreatureType.GOLLUM}>👹 Голлум</option>
            </select>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label htmlFor="age">Возраст *</label>
              <input
                type="number"
                id="age"
                name="age"
                value={formData.age}
                onChange={handleInputChange}
                required
                min="1"
                placeholder="Возраст"
              />
            </div>

            <div className="form-group">
              <label htmlFor="attackLevel">Уровень атаки *</label>
              <input
                type="number"
                id="attackLevel"
                name="attackLevel"
                value={formData.attackLevel}
                onChange={handleInputChange}
                required
                min="0.1"
                step="0.1"
                placeholder="Уровень атаки"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="defenseLevel">Уровень защиты *</label>
            <input
              type="number"
              id="defenseLevel"
              name="defenseLevel"
              value={formData.defenseLevel}
              onChange={handleInputChange}
              required
              min="1"
              placeholder="Уровень защиты"
            />
          </div>

          <h3 style={{ marginTop: '2rem', marginBottom: '1rem', color: '#4a5568' }}>📍 Координаты</h3>
          <div className="form-group">
            <label htmlFor="coordinatesExisting">Выбрать существующие координаты</label>
            <select
              id="coordinatesExisting"
              onChange={(e) => {
                const selected = coords.find(c => c.id === Number(e.target.value));
                if (selected) {
                  setFormData(prev => ({ ...prev, coordinates: selected }));
                }
              }}
              value={formData.coordinates.id ?? ''}
            >
              <option value="">Не выбрано</option>
              {coords.map(c => (
                <option key={c.id} value={c.id}>
                  ID {c.id}: ({c.x}, {c.y})
                </option>
              ))}
            </select>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label htmlFor="coordinates.x">X координата *</label>
              <input
                type="number"
                id="coordinates.x"
                name="coordinates.x"
                value={formData.coordinates.x}
                onChange={handleInputChange}
                required
                min="-379"
                placeholder="X координата"
              />
            </div>
            <div className="form-group">
              <label htmlFor="coordinates.y">Y координата *</label>
              <input
                type="number"
                id="coordinates.y"
                name="coordinates.y"
                value={formData.coordinates.y}
                onChange={handleInputChange}
                required
                max="665"
                step="0.1"
                placeholder="Y координата"
              />
            </div>
          </div>

          <h3 style={{ marginTop: '2rem', marginBottom: '1rem', color: '#4a5568' }}>🏰 Местоположение</h3>
          <div className="form-group">
            <label htmlFor="creatureLocation">Город *</label>
            <select
              id="creatureLocation"
              name="creatureLocation"
              value={formData.creatureLocation.id || ''}
              onChange={(e) => {
                const selectedCity = cities.find(city => city.id === Number(e.target.value));
                if (selectedCity) {
                  setFormData(prev => ({
                    ...prev,
                    creatureLocation: selectedCity
                  }));
                }
              }}
              required
            >
              <option value="">Выберите город</option>
              {cities.map(city => (
                <option key={city.id} value={city.id}>
                  {city.name} {city.capital ? '(столица)' : ''}
                </option>
              ))}
            </select>
          </div>

          <h3 style={{ marginTop: '2rem', marginBottom: '1rem', color: '#4a5568' }}>💍 Кольцо</h3>
          <div className="form-group">
            <label htmlFor="ringExisting">Выбрать существующее кольцо</label>
            <select
              id="ringExisting"
              onChange={(e) => {
                const selected = rings.find(r => r.id === Number(e.target.value));
                if (selected) {
                  setFormData(prev => ({ ...prev, ring: selected }));
                }
              }}
              value={formData.ring.id ?? ''}
            >
              <option value="">Не выбрано</option>
              {rings.map(r => (
                <option key={r.id} value={r.id}>
                  ID {r.id}: {r.name} {r.power ? `(сила ${r.power})` : ''}
                </option>
              ))}
            </select>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label htmlFor="ring.name">Название кольца *</label>
              <input
                type="text"
                id="ring.name"
                name="ring.name"
                value={formData.ring.name}
                onChange={handleInputChange}
                required
                placeholder="Название кольца"
              />
            </div>
            <div className="form-group">
              <label htmlFor="ring.power">Сила кольца</label>
              <input
                type="number"
                id="ring.power"
                name="ring.power"
                value={formData.ring.power || ''}
                onChange={handleInputChange}
                min="1"
                placeholder="Сила"
              />
            </div>
          </div>

          <div className="btn-group" style={{ marginTop: '2rem' }}>
            <button type="submit" className="btn" disabled={loading}>
              {loading ? '⏳ Сохранение...' : (creature ? '💾 Обновить' : '✨ Создать')}
            </button>
            <button type="button" className="btn btn-secondary" onClick={onSuccess}>
              ❌ Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BookCreatureForm;
