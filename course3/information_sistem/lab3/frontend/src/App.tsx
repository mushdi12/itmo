import  { useState } from 'react';
import './App.css';
import BookCreatureList from './components/BookCreatureList';
import MagicCityList from './components/MagicCityList';
import BookCreatureForm from './components/BookCreatureForm';
import MagicCityForm from './components/MagicCityForm';
import ImportForm from './components/ImportForm';
import ImportHistory from './components/ImportHistory';
import CacheStatisticsControl from './components/CacheStatisticsControl';
import { BookCreatureDto, MagicCityDto } from './types';

type TabType = 'creatures' | 'cities' | 'create-creature' | 'create-city' | 'import' | 'import-history' | 'cache-control';

function App() {
  const [activeTab, setActiveTab] = useState<TabType>('creatures');
  const [editingCreature, setEditingCreature] = useState<BookCreatureDto | undefined>(undefined);
  const [editingCity, setEditingCity] = useState<MagicCityDto | undefined>(undefined);

  const handleEditCreature = (creature: BookCreatureDto) => {
    setEditingCreature(creature);
    setActiveTab('create-creature');
  };

  const handleEditCity = (city: MagicCityDto) => {
    setEditingCity(city);
    setActiveTab('create-city');
  };

  const handleCreatureSaved = () => {
    setEditingCreature(undefined);
    setActiveTab('creatures');
  };

  const handleCitySaved = () => {
    setEditingCity(undefined);
    setActiveTab('cities');
  };

  const handleImportSuccess = () => {
    setActiveTab('import-history');
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'creatures':
        return <BookCreatureList onEdit={handleEditCreature} />;
      case 'cities':
        return <MagicCityList onEdit={handleEditCity} />;
      case 'create-creature':
        return <BookCreatureForm onSuccess={handleCreatureSaved} creature={editingCreature} />;
      case 'create-city':
        return <MagicCityForm onSuccess={handleCitySaved} city={editingCity} />;
      case 'import':
        return <ImportForm onSuccess={handleImportSuccess} />;
      case 'import-history':
        return (
          <div>
            <ImportHistory />
            <CacheStatisticsControl />
          </div>
        );
      case 'cache-control':
        return <CacheStatisticsControl />;
      default:
        return <BookCreatureList onEdit={handleEditCreature} />;
    }
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>🪄 Магический мир книжных существ</h1>
        <nav className="nav-tabs">
          <button 
            className={activeTab === 'creatures' ? 'active' : ''}
            onClick={() => setActiveTab('creatures')}
          >
            Существа
          </button>
          <button 
            className={activeTab === 'cities' ? 'active' : ''}
            onClick={() => setActiveTab('cities')}
          >
            Города
          </button>
          <button 
            className={activeTab === 'create-creature' ? 'active' : ''}
            onClick={() => setActiveTab('create-creature')}
          >
            Создать существо
          </button>
          <button 
            className={activeTab === 'create-city' ? 'active' : ''}
            onClick={() => setActiveTab('create-city')}
          >
            Создать город
          </button>
          <button 
            className={activeTab === 'import' ? 'active' : ''}
            onClick={() => setActiveTab('import')}
          >
            Импорт
          </button>
          <button 
            className={activeTab === 'import-history' ? 'active' : ''}
            onClick={() => setActiveTab('import-history')}
          >
            История импорта
          </button>
        </nav>
      </header>
      <main className="app-main">
        {renderContent()}
      </main>
    </div>
  );
}

export default App;
