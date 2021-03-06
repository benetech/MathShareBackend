/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import accountRoutes, { defaultRedirect } from './routes/account';
import db, { dbConfig } from './db';
import i18nextMiddleware, {
  LanguageDetector,
} from 'i18next-express-middleware';

import PrettyError from 'pretty-error';
import bodyParser from 'body-parser';
import compression from 'compression';
import configRoutes from './routes/config';
import cookieParser from 'cookie-parser';
import cors from 'cors';
import email from './email';
import express from 'express';
import expressWinston from 'express-winston';
import flash from 'express-flash';
import i18next from 'i18next';
import i18nextBackend from 'i18next-node-fs-backend';
import partnerRoutes from './routes/partner';
import passport from './passport';
import path from 'path';
import proxy from 'http-proxy-middleware';
import session from 'cookie-session';
import winston from 'winston';

console.log('process.env', process.env);

const { GATEWAY_BASE_URL } = process.env;
let cookieDomain = undefined;
if (GATEWAY_BASE_URL !== 'http://localhost:8080/') {
  cookieDomain = `.${GATEWAY_BASE_URL.split('://')[1]
    .split('.')
    .slice(1)
    .join('.')
    .split('/')[0]}`;
}
console.log('cookieDomain', cookieDomain);

expressWinston.responseWhitelist.push('_headers');

i18next
  .use(LanguageDetector)
  .use(i18nextBackend)
  .init({
    preload: ['en', 'de'],
    ns: ['common', 'email'],
    fallbackNS: 'common',
    detection: {
      lookupCookie: 'lng',
    },
    backend: {
      loadPath: path.resolve(__dirname, '../locales/{{lng}}/{{ns}}.json'),
      addPath: path.resolve(
        __dirname,
        '../locales/{{lng}}/{{ns}}.missing.json',
      ),
    },
  });

db.migrate.latest([dbConfig]);

const app = express();

app.use(
  cors({
    origin(origin, cb) {
      const whitelist = process.env.CORS_ORIGIN
        ? process.env.CORS_ORIGIN.split(',')
        : [];
      cb(null, whitelist.includes(origin));
    },
    credentials: true,
    exposedHeaders: ['x-load-more']
  }),
);

app.use(compression());
app.use(cookieParser());

const onProxyReq = (proxyReq, req, res) => {
  if (req.headers['x-auth-token'] === process.env.SESSION_SECRET) {
    proxyReq.setHeader('x-role', 'admin');
  }
  if (req.partner) {
    proxyReq.setHeader('x-partner-code', req.partner.code);
  }
  if (req.user) {
    proxyReq.setHeader('x-initiator', req.user.id);
    if (req.user.emails && req.user.emails.length > 0) {
      proxyReq.setHeader('x-initiator-email', req.user.emails[0].value);
    }
  } else if (req.path.startsWith('/api/private')) {
    res.status(401).send({ message: 'Unauthorized' });
  }
};

app.use(
  session({
    name: 'sid',
    keys: [process.env.SESSION_SECRET],
    maxAge: 24 * 60 * 60 * 1000,
    httpOnly: false,
    domain: cookieDomain,
  }),
);

const apiProxy = proxy({
  target: 'http://localhost:8081',
  changeOrigin: true,
  logLevel: 'debug',
  pathRewrite: {
    '^/api/': '/',
  },
  onProxyReq,
});

app.use(i18nextMiddleware.handle(i18next));
app.use(passport.initialize());
app.use(passport.session());
app.use(flash());

app.use(expressWinston.logger({
  transports: [
    new winston.transports.Console()
  ],
  format: winston.format.combine(
    winston.format.colorize(),
    winston.format.json()
  )
}));

const partnerValidator = async (req, res, next) => {
  const partnerToken = req.headers['x-partner-token']
  if (partnerToken) {
    let partner = null;
    try {
      partner = await db
            .table('partner_api_keys')
            .innerJoin('partners', 'partners.id', 'partner_api_keys.partner_id')
            .where({
                'partner_api_keys.id': partnerToken,
            })
            .whereRaw('("expired_at" is NULL or "expired_at" > CURRENT_TIMESTAMP)')
            .first('partners.*');
    } catch (error) {
      console.log('error', error);
    }
    if (partner) {
      req.partner = partner;
    } else {
      res.status(401).send({
        'message': 'Invalid x-partner-token in header',
      });
    }
  }
  next();
}

app.use('/api', partnerValidator, apiProxy);

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.use(accountRoutes);
app.use(configRoutes);
app.use(partnerRoutes);

// The following routes are intended to be used in development mode only
if (process.env.NODE_ENV !== 'production') {
  // A route for testing email templates
  app.get('/:email(email|emails)/:template', (req, res) => {
    const message = email.render(req.params.template, { t: req.t, v: 123 });
    res.send(message.html);
  });
}

app.get('/user', async (req, res) => {
  const { loginStarted } = req.session;
  req.session.loginStarted = false;
  if (req.user) {
    res.send({
      ...req.user,
      emails: await db
        .table('users')
        .innerJoin('emails', 'emails.user_id', 'users.id')
        .where({
          'emails.verified': true,
          'users.id': req.user.id,
        })
        .pluck('emails.email'),
      isLogin: loginStarted,
    });
  } else {
    res.status(401).send({
      message: 'Unauthorized',
      isLogin: loginStarted,
    });
  }
});

const pe = new PrettyError();
pe.skipNodeFiles();
pe.skipPackage('express');

app.get('*', (_req, res) => {
  res.redirect(defaultRedirect);
});

app.use(expressWinston.errorLogger({
  transports: [
    new winston.transports.Console()
  ],
  format: winston.format.combine(
    winston.format.colorize(),
    winston.format.json()
  )
}));

export default app;
