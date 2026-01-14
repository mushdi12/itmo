import axios from 'axios';
import { BookCreature, BookCreatureDto, MagicCity, MagicCityDto, Coordinates, Ring, ImportHistory, ImportResult } from '../types';


const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// BookCreature API
export const bookCreatureApi = {
  getAll: (): Promise<BookCreature[]> => 
    api.get('/creatures').then(res => res.data),
  
  getById: (id: number): Promise<BookCreature> => 
    api.get(`/creatures/${id}`).then(res => res.data),
  
  create: (data: BookCreatureDto): Promise<BookCreature> => 
    api.post('/creatures', data).then(res => res.data),
  
  update: (id: number, data: BookCreatureDto): Promise<BookCreature> => 
    api.put(`/creatures/${id}`, data).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/creatures/${id}`).then(() => {}),

  // special
  deleteByAttackLevel: (value: number): Promise<{deleted: number}> =>
    api.delete(`/creatures/attack-level/${value}`).then(res => res.data),
  searchByNameSubstring: (substring: string): Promise<BookCreature[]> =>
    api.get(`/creatures/search`, { params: { substring } }).then(res => res.data),
  distinctAttackLevels: (): Promise<number[]> =>
    api.get(`/creatures/attack-levels/distinct`).then(res => res.data),
  swapRings: (firstId: number, secondId: number): Promise<{swapped: boolean}> =>
    api.post(`/creatures/swap-rings`, { firstId, secondId }).then(res => res.data),
};

const normalizeDate = (value?: string | null | Date | any): string | undefined => {
    if (!value) return undefined;

    // Преобразуем в строку, если это не строка
    let dateStr: string;
    if (typeof value === 'string') {
        dateStr = value;
    } else if (value instanceof Date) {
        // Если это объект Date, форматируем в YYYY-MM-DD
        const year = value.getFullYear();
        const month = String(value.getMonth() + 1).padStart(2, '0');
        const day = String(value.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    } else if (typeof value === 'object' && value !== null) {
        // Если это объект (например, из JSON), пытаемся преобразовать в строку
        dateStr = String(value);
    } else {
        dateStr = String(value);
    }

    // Ищем YYYY-MM-DD в строке
    const match = dateStr.match(/(\d{1,4})-(\d{2})-(\d{2})/);
    if (!match) return undefined;

    return match[0]; // вернёт "0011-11-09"
};

// MagicCity API
export const magicCityApi = {
  getAll: async (): Promise<MagicCity[]> => {
    const res = await api.get('/cities');
    const cities: MagicCity[] = res.data;
    return cities.map(c => ({
      ...c,
      establishmentDate: normalizeDate((c as any).establishmentDate),
    }));
  },
  
  getById: async (id: number): Promise<MagicCity> => {
    const res = await api.get(`/cities/${id}`);
    const c: MagicCity = res.data;
    return {
      ...c,
      establishmentDate: normalizeDate((c as any).establishmentDate),
    };
  },
  
  create: async (data: MagicCityDto): Promise<MagicCity> => {
    const res = await api.post('/cities', data);
    const c: MagicCity = res.data;
    return {
      ...c,
      establishmentDate: normalizeDate((c as any).establishmentDate),
    };
  },
  
  update: async (id: number, data: MagicCityDto): Promise<MagicCity> => {
    const res = await api.put(`/cities/${id}`, data);
    const c: MagicCity = res.data;
    return {
      ...c,
      establishmentDate: normalizeDate((c as any).establishmentDate),
    };
  },
  
  delete: (id: number): Promise<void> => 
    api.delete(`/cities/${id}`).then(() => {}),

  // special
  deleteElfCities: (): Promise<{deleted: number}> =>
    api.delete('/cities/elves').then(res => res.data),
};

// Rings API (for linking existing)
export const ringsApi = {
  getAll: (): Promise<Ring[]> => api.get('/rings').then(res => res.data),
};

// Coordinates API (for linking existing)
export const coordsApi = {
  getAll: (): Promise<Coordinates[]> => api.get('/coords').then(res => res.data),
};

// Import API
export const importApi = {
  uploadFile: async (file: File, userName?: string): Promise<ImportResult> => {
    // Читаем файл как JSON
    const fileContent = await file.text();
    let creatures: BookCreatureDto[];
    
    try {
      creatures = JSON.parse(fileContent);
      // Если это не массив, обертываем в массив
      if (!Array.isArray(creatures)) {
        creatures = [creatures];
      }
    } catch (e) {
      throw new Error('Ошибка парсинга JSON файла: ' + (e as Error).message);
    }
    
    // Читаем файл как base64 для отправки на сервер
    const fileArrayBuffer = await file.arrayBuffer();
    const bytes = new Uint8Array(fileArrayBuffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    const fileBase64 = btoa(binary);
    
    const requestBody = {
      userName: userName || 'anonymous',
      creatures: creatures,
      fileContent: fileBase64,  // Отправляем файл как base64
      fileName: file.name
    };
    
    const res = await api.post('/import/upload', requestBody);
    
    return res.data;
  },

  getHistory: (): Promise<ImportHistory[]> => 
    api.get('/import/history').then(res => res.data),

  getHistoryById: (id: number): Promise<ImportHistory> => 
    api.get(`/import/history/${id}`).then(res => res.data),

  downloadFile: async (historyId: number): Promise<void> => {
    const response = await api.get(`/import/file/${historyId}`, {
      responseType: 'blob',
    });
    
    // Создаем ссылку для скачивания
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    
    // Получаем имя файла из заголовка или используем дефолтное
    const contentDisposition = response.headers['content-disposition'];
    let fileName = `import_${historyId}.json`;
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
      if (fileNameMatch) {
        fileName = fileNameMatch[1];
      }
    }
    
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },
};

// Cache Statistics API
export const cacheStatisticsApi = {
  getLoggingStatus: (): Promise<{ enabled: boolean; message: string }> =>
    api.get('/cache-statistics/logging/status').then(res => res.data),

  enableLogging: (): Promise<{ success: boolean; message: string }> =>
    api.post('/cache-statistics/logging/enable').then(res => res.data),

  disableLogging: (): Promise<{ success: boolean; message: string }> =>
    api.post('/cache-statistics/logging/disable').then(res => res.data),
};

export default api;
