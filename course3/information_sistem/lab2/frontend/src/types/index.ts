export enum BookCreatureType {
  HOBBIT = 'HOBBIT',
  ELF = 'ELF',
  HUMAN = 'HUMAN',
  GOLLUM = 'GOLLUM'
}

export interface Coordinates {
  id?: number;
  x: number;
  y: number;
}

export interface Ring {
  id?: number;
  name: string;
  power?: number;
}

export interface MagicCity {
  id?: number;
  name: string;
  area: number;
  population: number;
  establishmentDate?: string;
  governor: BookCreatureType;
  capital: boolean;
  populationDensity: number;
}

export interface BookCreature {
  id?: number;
  name: string;
  coordinates: Coordinates;
  creationDate?: string;
  age: number;
  creatureType: BookCreatureType;
  creatureLocation: MagicCity;
  attackLevel: number;
  defenseLevel: number;
  ring: Ring;
}

export interface BookCreatureDto {
  id?: number;
  name: string;
  coordinates: Coordinates;
  creationDate?: string;
  age: number;
  creatureType: BookCreatureType;
  creatureLocation: MagicCity;
  attackLevel: number;
  defenseLevel: number;
  ring: Ring;
}

export interface MagicCityDto {
  id?: number;
  name: string;
  area: number;
  population: number;
  establishmentDate?: string;
  governor: BookCreatureType;
  capital: boolean;
  populationDensity: number;
}

export interface ImportHistory {
  id?: number;
  status: 'SUCCESS' | 'FAILED';
  userName?: string;
  createdAt: string;
  objectsCount?: number;
  errorMessage?: string;
}

export interface ImportResult {
  success: boolean;
  objectsCount?: number;
  historyId?: number;
  error?: string;
}