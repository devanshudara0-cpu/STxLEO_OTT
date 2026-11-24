global._telemetryState = global._telemetryState || {
  activeSessions: {},
  totalWatchSeconds: 0,
  totalSessions: 0,
  uniqueDevices: new Set(),
  movieWatchCounts: {},
  recentEvents: []
};

async function executeKv(cmd, ...args) {
  const url = process.env.KV_REST_API_URL;
  const token = process.env.KV_REST_API_TOKEN;
  if (!url || !token) return null;
  try {
    const res = await fetch(`${url}`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify([cmd, ...args])
    });
    const data = await res.json();
    return data.result;
  } catch (err) {
    console.error('KV Error:', err);
    return null;
  }
}

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') return res.status(200).end();

  const pin = req.query.pin || req.headers['authorization'];
  const ADMIN_PIN = process.env.ADMIN_PIN || 'leo2026';

  if (pin !== ADMIN_PIN && pin !== `Bearer ${ADMIN_PIN}`) {
    return res.status(401).json({ error: 'Unauthorized: Invalid Admin PIN' });
  }

  const now = Date.now();
  const state = global._telemetryState;

  // Filter sessions active within the last 2 minutes (120 seconds)
  const activeCutoff = now - 120 * 1000;
  const liveSessions = Object.entries(state.activeSessions)
    .filter(([_, s]) => s.lastPing >= activeCutoff)
    .map(([id, s]) => ({
      id: id.substring(0, 10) + '...',
      title: s.title,
      country: s.country,
      city: s.city,
      version: s.version,
      watchMinutes: Math.round(s.watchSec / 60),
      lastActiveSec: Math.round((now - s.lastPing) / 1000)
    }));

  let totalWatchSec = state.totalWatchSeconds;
  let totalDevices = state.uniqueDevices.size;
  let totalSessions = state.totalSessions;

  // If Vercel KV exists, merge KV data
  if (process.env.KV_REST_API_URL) {
    const kvSec = await executeKv('GET', 'stats:total_watch_seconds');
    if (kvSec) totalWatchSec = Math.max(totalWatchSec, parseInt(kvSec, 10));

    const kvDevices = await executeKv('SCARD', 'stats:devices');
    if (kvDevices) totalDevices = Math.max(totalDevices, parseInt(kvDevices, 10));

    const kvSessions = await executeKv('GET', 'stats:total_sessions');
    if (kvSessions) totalSessions = Math.max(totalSessions, parseInt(kvSessions, 10));
  }

  // Calculate top watched content
  const topMovies = Object.entries(state.movieWatchCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5)
    .map(([title, sec]) => ({
      title,
      hours: +(sec / 3600).toFixed(1)
    }));

  return res.status(200).json({
    liveUsersCount: liveSessions.length,
    liveSessions,
    totalWatchHours: +(totalWatchSec / 3600).toFixed(2),
    totalDevicesCount: Math.max(totalDevices, liveSessions.length),
    totalAppOpens: totalSessions,
    topMovies,
    recentEvents: state.recentEvents.slice(0, 15),
    serverTime: now,
    hasPersistentKv: !!process.env.KV_REST_API_URL
  });
}
