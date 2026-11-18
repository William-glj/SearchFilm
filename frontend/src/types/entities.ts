// Tipos estrictos que reflejan las entidades y DTOs del backend (com.checkticket.backend)

export type Role = 'ROLE_USER' | 'ROLE_ADMIN';

export type NotificationChannel = 'EMAIL' | 'TELEGRAM';

export interface User {
  id: number;
  email: string;
  password?: string | null; // null para login con Google OAuth2
  fullName: string;
  role: Role;
  telegramChatId?: string | null;
  active: boolean;
  createdAt: string; // ISO-8601
}

export interface Cinema {
  id: number;
  externalOsmId: string;
  name: string;
  city?: string | null;
  zone?: string | null;
  latitude?: number | null;
  longitude?: number | null;
}

export interface Showtime {
  id: number;
  cinemaId: number;
  tmdbMovieId?: number | null;
  movieTitle: string;
  dayOfWeek: string; // MONDAY..SUNDAY
  sessionTime: string; // HH:mm
  price?: number | null;
  seatsAvailable: number;
  updatedAt: string;
}

export interface CinemaAlert {
  id: number;
  userId: number;
  cinemaId?: number | null;
  movieTitle: string;
  targetDay?: string | null;
  maxPrice?: number | null;
  notifyEmail: boolean;
  notifyTelegram: boolean;
  active: boolean;
  lastNotifiedHash?: string | null;
}

export interface NotificationLog {
  id: number;
  alertId: number;
  channel: NotificationChannel;
  messageSummary?: string | null;
  sentAt: string;
}

// DTOs de autenticación
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  fullName: string;
  role: Role;
}
