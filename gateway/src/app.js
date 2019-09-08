/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import i18nextMiddleware, {
  LanguageDetector,
} from 'i18next-express-middleware';

import PrettyError from 'pretty-error';
import accountRoutes from './routes/account';
import proxy from 'http-proxy-middleware';
import bodyParser from 'body-parser';
import compression from 'compression';
import cookieParser from 'cookie-parser';
import cors from 'cors';
import email from './email';
import express from 'express';
import flash from 'express-flash';
import i18next from 'i18next';
import i18nextBackend from 'i18next-node-fs-backend';
import passport from './passport';
import db from './db';
import path from 'path';
import session from 'cookie-session';

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

const app = express();

app.set('trust proxy', 'loopback');

app.use(
  cors({
    origin(origin, cb) {
      const whitelist = process.env.CORS_ORIGIN
        ? process.env.CORS_ORIGIN.split(',')
        : [];
      cb(null, whitelist.includes(origin));
    },
    credentials: true,
  }),
);

app.use(compression());
app.use(cookieParser());

const onProxyReq = (proxyReq, req, res) => {
  if (req.user) {
    proxyReq.setHeader('x-initiator', req.user.id);
  } else if (req.path.startsWith('/api/private')) {
    res.status(401).send({ message: 'Unauthorized' });
  }
};

app.use(
  session({
    name: 'sid',
    keys: [process.env.SESSION_SECRET],
    maxAge: 24 * 60 * 60 * 1000,
  }),
);

const apiProxy = proxy({
  target: 'http://server:8080',
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

app.use('/api', apiProxy);

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.use(accountRoutes);

// The following routes are intended to be used in development mode only
if (process.env.NODE_ENV !== 'production') {
  // A route for testing email templates
  app.get('/:email(email|emails)/:template', (req, res) => {
    const message = email.render(req.params.template, { t: req.t, v: 123 });
    res.send(message.html);
  });
}

app.get('/user', async (req, res) => {
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
    });
  } else {
    res.status(401).send({
      message: 'Unauthorized',
    });
  }
});

const pe = new PrettyError();
pe.skipNodeFiles();
pe.skipPackage('express');

app.use((err, req, res, next) => {
  process.stderr.write(pe.render(err));
  next();
});

export default app;
