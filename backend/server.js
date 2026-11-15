require('dotenv').config();

const express = require('express');
const cors = require('cors');
const parentReportRoute = require('./routes/parentReportRoute');

const app = express();
const PORT = Number(process.env.PORT) || 3000;
const IS_PRODUCTION = process.env.NODE_ENV === 'production';

// CORS configuration supporting either "*" or comma-separated origin allowlist
const allowedOriginsRaw = (process.env.ALLOWED_ORIGINS || '*').trim();
const corsOptions =
  allowedOriginsRaw === '*'
    ? { origin: '*' }
    : {
        origin: allowedOriginsRaw
          .split(',')
          .map((o) => o.trim())
          .filter(Boolean),
      };

app.use(cors(corsOptions));
app.use(express.json({ limit: '100kb' }));

// Lightweight request logging
app.use((req, res, next) => {
  const startedAt = Date.now();
  res.on('finish', () => {
    const durationMs = Date.now() - startedAt;
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.originalUrl} ${res.statusCode} (${durationMs}ms)`);
  });
  next();
});

// Required Render.com health-check endpoint
app.get('/', (req, res) => {
  res.send('Phonics App Backend is Live!');
});

// Structured health endpoint for uptime monitors
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'ok',
    service: process.env.APP_NAME || 'PhonicsPath',
    uptimeSeconds: Math.floor(process.uptime()),
    timestamp: new Date().toISOString(),
  });
});

// Parent weekly report routes
app.use('/api/reports', parentReportRoute);

// 404 Not Found handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: `Route not found: ${req.method} ${req.originalUrl}`,
  });
});

// Global Error Handling Middleware
app.use((err, req, res, next) => {
  if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
    return res.status(400).json({
      success: false,
      error: 'Invalid JSON payload in request body.',
    });
  }

  console.error('Unhandled server error:', err);
  const statusCode = err.statusCode || err.status || 500;
  return res.status(statusCode).json({
    success: false,
    error: IS_PRODUCTION ? 'Internal server error.' : err.message || 'Internal server error.',
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`PhonicsPath backend listening on http://0.0.0.0:${PORT}`);
});
