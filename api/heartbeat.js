// In-memory fallback for immediate zero-config operation
global._telemetryState = global._telemetryState || {
  activeSessions: {}, // deviceId -> { lastPing, title, city, country, watchSec }
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
  // Enable CORS for dashboard and app requests
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    let body = req.body;
    if (typeof body === 'string') {
      try { body = JSON.parse(body); } catch (_) { body = {}; }
    }
    body = body || {};

    const deviceId = (body.deviceId || 'anon_' + Math.random().toString(36).substring(2, 10)).substring(0, 64);
    const event = body.event || 'watching'; // 'open' | 'watching'
    const title = (body.title || 'Browsing Catalog').substring(0, 100);
    const seconds = Math.min(120, Math.max(0, parseInt(body.seconds || (event === 'watching' ? 30 : 0), 10)));
    const version = (body.version || '2.4').substring(0, 10);
    const country = req.headers['x-vercel-ip-country'] || 'IN';
    const city = req.headers['x-vercel-ip-city'] || 'Local';
    const now = Date.now();

    // 1. Update in-memory state
    const state = global._telemetryState;
    state.activeSessions[deviceId] = {
      lastPing: now,
      title,
      country,
      city,
      version,
      watchSec: (state.activeSessions[deviceId]?.watchSec || 0) + seconds
    };
    state.totalWatchSeconds += seconds;
    state.uniqueDevices.add(deviceId);
    if (event === 'open') state.totalSessions += 1;
    if (title && title !== 'Browsing Catalog') {
      state.movieWatchCounts[title] = (state.movieWatchCounts[title] || 0) + (seconds || 30);
    }
    state.recentEvents.unshift({
      time: now,
      event,
      title,
      city,
      country,
      seconds
    });
    if (state.recentEvents.length > 50) state.recentEvents.pop();

    // 2. If Vercel KV is configured, update Redis
    if (process.env.KV_REST_API_URL) {
      await executeKv('SET', `active:${deviceId}`, JSON.stringify({ title, country, city, lastPing: now }), 'EX', 120);
      if (seconds > 0) await executeKv('INCRBY', 'stats:total_watch_seconds', seconds);
      await executeKv('SADD', 'stats:devices', deviceId);
      if (event === 'open') await executeKv('INCR', 'stats:total_sessions');
      if (title && title !== 'Browsing Catalog') {
        await executeKv('ZINCRBY', 'stats:top_movies', seconds || 30, title);
      }
    }

    return res.status(200).json({ ok: true, serverTime: now });
  } catch (error) {
    console.error('Heartbeat handler error:', error);
    return res.status(500).json({ error: error.message });
  }
}
