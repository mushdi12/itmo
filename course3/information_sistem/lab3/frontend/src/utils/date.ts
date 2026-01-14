export function normalizeDate(value: unknown): string | undefined {
  if (value === null || value === undefined) return undefined;

  // If backend may return as object {year, month, day} or as epoch or as string
  // Try common shapes safely and always return ISO string
  try {
    // Case: already ISO string or parseable by Date
    if (typeof value === 'string') {
      const trimmed = value.trim();
      if (!trimmed) return undefined;
      const d = new Date(trimmed);
      if (!isNaN(d.getTime())) return d.toISOString();
    }

    // Case: number (epoch millis or seconds)
    if (typeof value === 'number') {
      const millis = value > 1e12 ? value : value * 1000;
      const d = new Date(millis);
      if (!isNaN(d.getTime())) return d.toISOString();
    }

    // Case: JPA LocalDate serialized as object
    if (typeof value === 'object') {
      const v: any = value;
      // Mongo/JSON-B like wrapper { "$date": ... }
      if (v.$date) {
        const nested = normalizeDate(v.$date);
        if (nested) return nested;
      }

      // java.util.Date serialized as { time: <millis> }
      if (typeof v.time === 'number') {
        const d = new Date(v.time);
        if (!isNaN(d.getTime())) return d.toISOString();
      }

      if (
        typeof v.year === 'number' &&
        (typeof v.month === 'number' || typeof v.monthValue === 'number' || typeof v.month === 'string') &&
        (typeof v.day === 'number' || typeof v.dayOfMonth === 'number')
      ) {
        // month can be 1-based number, or string name
        let monthIndex: number | undefined;
        if (typeof v.month === 'number') monthIndex = v.month - 1;
        else if (typeof v.monthValue === 'number') monthIndex = v.monthValue - 1;
        else if (typeof v.month === 'string') {
          const names = ['JANUARY','FEBRUARY','MARCH','APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER'];
          const idx = names.indexOf(String(v.month).toUpperCase());
          if (idx >= 0) monthIndex = idx;
        }
        const day = typeof v.day === 'number' ? v.day : v.dayOfMonth;
        const d = new Date(Date.UTC(v.year, monthIndex ?? 0, day));
        if (!isNaN(d.getTime())) return d.toISOString();
      }
      // Try Date constructor on JSON-serialized date-like object (fallback)
      const d2 = new Date(String((value as any).toString?.() ?? ''));
      if (!isNaN(d2.getTime())) return d2.toISOString();
    }
  } catch (_) {
    // ignore and fall through
  }

  return undefined;
}

export function formatDateForInput(iso?: string): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return '';
  return d.toISOString().split('T')[0];
}

export function formatDateForUi(iso?: string, locale: string = 'ru-RU'): string {
  if (!iso) return 'Неизвестно';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return 'Неизвестно';
  return d.toLocaleDateString(locale);
}


