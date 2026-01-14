import React from 'react';
import { BookCreature, BookCreatureType } from '../types';

interface BookCreatureCardProps {
  creature: BookCreature;
  onDelete: (id: number) => void;
  onEdit?: (creature: BookCreature) => void;
}

const BookCreatureCard: React.FC<BookCreatureCardProps> = ({ creature, onDelete, onEdit }) => {
  const getCreatureTypeClass = (type: BookCreatureType) => {
    return `creature-type ${type.toLowerCase()}`;
  };

  const getCreatureTypeEmoji = (type: BookCreatureType) => {
    switch (type) {
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

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'Неизвестно';
    return new Date(dateString).toLocaleDateString('ru-RU');
  };

  return (
    <div className="card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <h3 style={{ margin: 0, color: '#ffffff', textShadow: '0 2px 10px rgba(255, 255, 255, 0.2)' }}>
          {getCreatureTypeEmoji(creature.creatureType)} {creature.name}
          {typeof creature.id !== 'undefined' && (
            <span style={{ marginLeft: '0.5rem', color: '#a0a0a0', fontSize: '0.9rem' }}># {creature.id}</span>
          )}
        </h3>
        <span className={getCreatureTypeClass(creature.creatureType)}>
          {creature.creatureType}
        </span>
      </div>

      <div className="stats">
        <div className="stat-item">
          <div className="stat-value">{creature.age}</div>
          <div className="stat-label">Возраст</div>
        </div>
        <div className="stat-item">
          <div className="stat-value">{creature.attackLevel}</div>
          <div className="stat-label">Атака</div>
        </div>
        <div className="stat-item">
          <div className="stat-value">{creature.defenseLevel}</div>
          <div className="stat-label">Защита</div>
        </div>
      </div>

      <div style={{ marginTop: '1rem', fontSize: '0.9rem', color: '#d0d0d0' }}>
        <p><strong style={{ color: '#ffffff' }}>📍 Местоположение:</strong> {creature.creatureLocation.name}</p>
        <p><strong style={{ color: '#ffffff' }}>🗺️ Координаты:</strong> ({creature.coordinates.x}, {creature.coordinates.y})</p>
        <p><strong style={{ color: '#ffffff' }}>💍 Кольцо:</strong> {creature.ring.name} {creature.ring.power && `(сила: ${creature.ring.power})`}</p>
        <p><strong style={{ color: '#ffffff' }}>📅 Создано:</strong> {formatDate(creature.creationDate)}</p>
      </div>

      <div className="btn-group" style={{ marginTop: '1rem' }}>
        <button 
          className="btn" 
          onClick={() => onEdit && onEdit(creature)}
          style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}
        >
          ✏️ Редактировать
        </button>
        <button 
          className="btn btn-danger" 
          onClick={() => creature.id && onDelete(creature.id)}
          style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}
        >
          🗑️ Удалить
        </button>
      </div>
    </div>
  );
};

export default BookCreatureCard;
