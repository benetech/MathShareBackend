/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import { Router } from 'express';
import URL from 'url';
import passport from 'passport';
import validator from 'validator';

export const defaultRedirect = process.env.CORS_ORIGIN.split(',')[0];

const router = new Router();

// External login providers. Also see src/passport.js.
const loginProviders = [
  {
    provider: 'google',
    options: { scope: 'profile email', accessType: 'offline' },
  },
  {
    provider: 'azuread-openidconnect',
    options: {},
    hasPostCallback: true,
  },
];

// '/about' => ''
// http://localhost:3000/some/page => http://localhost:3000
function getOrigin(url: string) {
  if (!url || url.startsWith('/')) return '';
  return (x => `${String(x.protocol)}//${String(x.host)}`)(URL.parse(url));
}

// '/about' => `true` (all relative URL paths are allowed)
// 'http://localhost:3000/about' => `true` (but only if its origin is whitelisted)
function isValidReturnURL(url: string) {
  if (url.startsWith('/')) return true;
  const whitelist = process.env.CORS_ORIGIN
    ? process.env.CORS_ORIGIN.split(',')
    : [];
  return (
    validator.isURL(url, {
      require_tld: false,
      require_protocol: true,
      protocols: ['http', 'https'],
    }) && whitelist.includes(getOrigin(url))
  );
}

// Generates a URL for redirecting a user to upon successfull authentication.
// It is intended to support cross-domain authentication in development mode.
// For example, a user goes to http://localhost:3000/login (frontend) to sign in,
// then he's being redirected to http://localhost:8080/login/facebook (backend),
// Passport.js redirects the user to Facebook, which redirects the user back to
// http://localhost:8080/login/facebook/return and finally, user is being redirected
// to http://localhost:3000/?sessionID=xxx where front-end middleware can save that
// session ID into cookie (res.cookie.sid = req.query.sessionID).
function getSuccessRedirect(req) {
  const url = req.query.return || req.body.return || '/';
  if (!isValidReturnURL(url)) return '/';
  if (!getOrigin(url)) return url;
  return url;
}

// Registers route handlers for the external login providers
loginProviders.forEach(({ provider, options, hasPostCallback }) => {
  router.get(
    `/login/${provider}`,
    (req, res, next) => {
      req.session.returnTo = getSuccessRedirect(req);
      req.session.loginStarted = true;
      next();
    },
    passport.authenticate(provider, { failureFlash: true, ...options }),
  );

  const callbackProcessor = (req, res, next) => {
    return passport.authenticate(
      provider,
      {
        successReturnToOrRedirect: true,
        failureFlash: true,
        failureRedirect: req.session.returnTo || defaultRedirect,
      },
      (err, user, info) => {
        console.log('err', err);
        console.log('user', user);
        console.log('info', info);
        console.log('res1', res._headers);
        if (err || !user) {
          return res.redirect(req.session.returnTo || defaultRedirect);
        }
        req.logIn(user, function (err) {
          console.log('res2', res._headers);
          const token = Buffer.from(JSON.stringify({
            "passport": {
              "user": {
                id: user.id,
                displayName: user.displayName,
                imageUrl: user.imageUrl,
              }
            }
          })).toString('base64')
          const url = `${defaultRedirect}?sid=${token}`
          return res.redirect(req.session.returnTo || url);
        });
      },
    )(req, res, next);
  };

  if (hasPostCallback) {
    router.post(`/login/${provider}/return`, callbackProcessor);
  } else {
    router.get(`/login/${provider}/return`, callbackProcessor);
  }
});

// Remove the `user` object from the session. Example:
//   fetch('/login/clear', { method: 'POST', credentials: 'include' })
//     .then(() => window.location = '/')
router.get('/logout', (req, res) => {
  req.logout();
  res.status(200).send({ message: 'Logged Out' });
});

// Allows to fetch the last login error(s) (which is usefull for single-page apps)
router.post('/login/error', (req, res) => {
  res.send({ errors: req.flash('error') });
});

export default router;
