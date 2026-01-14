import React from 'react';
import { MagicCity, BookCreatureType } from '../types';

interface MagicCityCardProps {
  city: MagicCity;
  onDelete: (id: number) => void;
  onEdit?: (city: MagicCity) => void;
}

const MagicCityCard: React.FC<MagicCityCardProps> = ({ city, onDelete, onEdit }) => {
  const getGovernorEmoji = (governor: BookCreatureType) => {
    switch (governor) {
      case BookCreatureType.HOBBIT:
        return '🧙‍♂️';
      case BookCreatureType.ELF:
        return '🧝‍♀️';
      case BookCreatureType.HUMAN:
        return '👤';
      case BookCreatureType.GOLLUM:
        return '👹';
      default:
        return '❓';
    }
  };

    const formatDateForUi = (dateString?: string) => {
        if (!dateString) return 'Неизвестно';


        // Ищем YYYY-MM-DD в строке
        const match = dateString.match(/(\d{1,4})-(\d{2})-(\d{2})/);
        if (!match) return 'Неизвестно';

        const [, year, month, day] = match;

        const months = [
            'января', 'февраля', 'марта', 'апреля', 'мая', 'июня',
            'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'
        ];

        return `${day} ${months[Number(month) - 1]} ${year}`;
    };

    const formatDate = (dateString?: string) => formatDateForUi(dateString);





    const formatNumber = (num: number) => {
    return new Intl.NumberFormat('ru-RU').format(num);
  };

    console.log('city.establishmentDate:', city.establishmentDate);

  return (
    <div className="card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <h3 style={{ margin: 0, color: '#ffffff', textShadow: '0 2px 10px rgba(255, 255, 255, 0.2)' }}>
          🏰 {city.name}
          {typeof city.id !== 'undefined' && (
            <span style={{ marginLeft: '0.5rem', color: '#a0a0a0', fontSize: '0.9rem' }}># {city.id}</span>
          )}
          {city.capital && <span style={{ marginLeft: '0.5rem', fontSize: '0.8rem' }}>👑</span>}
        </h3>
        {city.capital && (
          <span style={{ 
            background: 'rgba(214, 158, 46, 0.2)', 
            color: '#ffd700', 
            padding: '0.25rem 0.75rem', 
            borderRadius: '12px', 
            fontSize: '0.875rem', 
            fontWeight: '600',
            border: '1px solid rgba(214, 158, 46, 0.4)',
            boxShadow: '0 0 10px rgba(214, 158, 46, 0.2)'
          }}>
            СТОЛИЦА
          </span>
        )}
      </div>

      <div className="stats">
        <div className="stat-item">
          <div className="stat-value">{formatNumber(city.population)}</div>
          <div className="stat-label">Население</div>
        </div>
        <div className="stat-item">
          <div className="stat-value">{formatNumber(city.area)}</div>
          <div className="stat-label">Площадь (км²)</div>
        </div>
        <div className="stat-item">
          <div className="stat-value">{formatNumber(city.populationDensity)}</div>
          <div className="stat-label">Плотность</div>
        </div>
      </div>

      <div style={{ marginTop: '1rem', fontSize: '0.9rem', color: '#d0d0d0' }}>
        <p>
          <strong style={{ color: '#ffffff' }}>{getGovernorEmoji(city.governor)} Правитель:</strong> {city.governor}
        </p>

        <p><strong style={{ color: '#ffffff' }}>📅 Основан:</strong> {formatDate(city.establishmentDate)}</p>
      </div>

      <div className="btn-group" style={{ marginTop: '1rem' }}>
        <button 
          className="btn" 
          onClick={() => onEdit && onEdit(city)}
          style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}
        >
          ✏️ Редактировать
        </button>
        <button 
          className="btn btn-danger" 
          onClick={() => city.id && onDelete(city.id)}
          style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}
        >
          🗑️ Удалить
        </button>
      </div>
    </div>
  );
};

export default MagicCityCard;
