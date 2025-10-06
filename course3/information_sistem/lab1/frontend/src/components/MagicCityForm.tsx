import React, { useState, useEffect } from 'react';
import { MagicCityDto, BookCreatureType } from '../types';
import { magicCityApi } from '../services/api';
import { formatDateForInput } from '../utils/date';

interface MagicCityFormProps {
  onSuccess: () => void;
  city?: MagicCityDto;
}

const MagicCityForm: React.FC<MagicCityFormProps> = ({ onSuccess, city }) => {
  const [formData, setFormData] = useState<MagicCityDto>({
    name: '',
    area: 0,
    population: 0,
    governor: BookCreatureType.HUMAN,
    capital: false,
    populationDensity: 0
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (city) {
      setFormData(city);
    }
  }, [city]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : 
              type === 'checkbox' ? (e.target as HTMLInputElement).checked : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      if (city && city.id) {
        await magicCityApi.update(city.id, formData);
        setSuccess('Город успешно обновлен!');
      } else {
        await magicCityApi.create(formData);
        setSuccess('Город успешно создан!');
      }
      
      setTimeout(() => {
        onSuccess();
      }, 1500);
    } catch (err) {
      setError('Ошибка при сохранении города');
      console.error('Error saving city:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <div className="card">
        <h2>🏰 {city ? 'Редактировать город' : 'Создать новый город'}</h2>
        
        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Название города *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              required
              placeholder="Введите название города"
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label htmlFor="area">Площадь (км²) *</label>
              <input
                type="number"
                id="area"
                name="area"
                value={formData.area}
                onChange={handleInputChange}
                required
                min="1"
                placeholder="Площадь"
              />
            </div>

            <div className="form-group">
              <label htmlFor="population">Население *</label>
              <input
                type="number"
                id="population"
                name="population"
                value={formData.population}
                onChange={handleInputChange}
                required
                min="1"
                placeholder="Население"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="populationDensity">Плотность населения *</label>
            <input
              type="number"
              id="populationDensity"
              name="populationDensity"
              value={formData.populationDensity}
              onChange={handleInputChange}
              required
              min="1"
              placeholder="Плотность населения"
            />
          </div>

          <div className="form-group">
            <label htmlFor="governor">Правитель *</label>
            <select
              id="governor"
              name="governor"
              value={formData.governor}
              onChange={handleInputChange}
              required
            >
              <option value={BookCreatureType.HOBBIT}>🧙‍♂️ Хоббит</option>
              <option value={BookCreatureType.ELF}>🧝‍♀️ Эльф</option>
              <option value={BookCreatureType.HUMAN}>👤 Человек</option>
              <option value={BookCreatureType.GOLLUM}>👹 Голлум</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="establishmentDate">Дата основания</label>
            <input
              type="date"
              id="establishmentDate"
              name="establishmentDate"
              value={formatDateForInput(formData.establishmentDate)}
              onChange={(e) => {
                setFormData(prev => ({
                  ...prev,
                  establishmentDate: e.target.value ? new Date(e.target.value).toISOString() : undefined
                }));
              }}
            />
          </div>

          <div className="form-group">
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <input
                type="checkbox"
                id="capital"
                name="capital"
                checked={formData.capital}
                onChange={handleInputChange}
              />
              👑 Столица
            </label>
          </div>

          <div className="btn-group" style={{ marginTop: '2rem' }}>
            <button type="submit" className="btn" disabled={loading}>
              {loading ? '⏳ Сохранение...' : (city ? '💾 Обновить' : '✨ Создать')}
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

export default MagicCityForm;
