/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import { Strategy as GoogleStrategy } from 'passport-google-oauth20';
import { OIDCStrategy } from 'passport-azure-ad';
import db from './db';
// import jwt from 'jsonwebtoken'
import passport from 'passport';

passport.serializeUser((user, done) => {
  console.log('user', user);
  done(null, {
    id: user.id,
    displayName: user.displayName,
    imageUrl: user.imageUrl,
    emails: user.emails
  });
});

passport.deserializeUser((user, done) => {
  done(null, user);
});

// Creates or updates the external login credentials
// and returns the currently authenticated user.
async function login(req, provider, profile, tokens) {
  let user;

  if (req.user) {
    user = await db
      .table('users')
      .where({ id: req.user.id })
      .first();
  }

  if (!user) {
    user = await db
      .table('logins')
      .innerJoin('users', 'users.id', 'logins.user_id')
      .where({ 'logins.provider': provider, 'logins.id': profile.id })
      .first('users.*');
    if (
      !user &&
      profile.emails &&
      profile.emails.length &&
      profile.emails[0].verified === true
    ) {
      user = await db
        .table('users')
        .innerJoin('emails', 'emails.user_id', 'users.id')
        .where({
          'emails.email': profile.emails[0].value,
          'emails.verified': true,
        })
        .first('users.*');
    }
  }

  if (!user) {
    [user] = await db
      .table('users')
      .insert({
        display_name: profile.displayName,
        image_url:
          profile.photos && profile.photos.length
            ? profile.photos[0].value
            : null,
      })
      .returning('*');

    if (profile.emails && profile.emails.length) {
      await db.table('emails').insert(
        profile.emails.map(x => ({
          user_id: user && user.id,
          email: x.value,
          verified: x.verified || false,
        })),
      );
    }
  }

  const loginKeys = { user_id: user.id, provider, id: profile.id };
  const { count } = await db
    .table('logins')
    .where(loginKeys)
    .count('id')
    .first();

  if (count === '0') {
    await db.table('logins').insert({
      ...loginKeys,
      username: profile.username,
      tokens: JSON.stringify(tokens),
      profile: JSON.stringify(profile._json),
    });
  } else {
    await db
      .table('logins')
      .where(loginKeys)
      .update({
        username: profile.username,
        tokens: JSON.stringify(tokens),
        profile: JSON.stringify(profile._json),
        updated_at: db.raw('CURRENT_TIMESTAMP'),
      });
  }

  return {
    id: user.id,
    displayName: user.display_name,
    imageUrl: user.image_url,
    emails: profile.emails,
  };
}

// https://github.com/jaredhanson/passport-google-oauth2
passport.use(
  new GoogleStrategy(
    {
      clientID: process.env.GOOGLE_ID,
      clientSecret: process.env.GOOGLE_SECRET,
      callbackURL: process.env.GATEWAY_BASE_URL + 'login/google/return',
      passReqToCallback: true,
    },
    async (req, accessToken, refreshToken, profile, done) => {
      try {
        profile.emails = profile.emails.map(email => ({
          ...email,
          verified: true,
        }));
        const user = await login(req, 'google', profile, {
          accessToken,
          refreshToken,
        });
        done(null, user);
      } catch (err) {
        done(err);
      }
    },
  ),
);

// // https://github.com/jaredhanson/passport-facebook
// // https://developers.facebook.com/docs/facebook-login/permissions/
// passport.use(
//   new FacebookStrategy(
//     {
//       clientID: process.env.FACEBOOK_ID,
//       clientSecret: process.env.FACEBOOK_SECRET,
//       profileFields: [
//         'id',
//         'cover',
//         'name',
//         'age_range',
//         'link',
//         'gender',
//         'locale',
//         'picture',
//         'timezone',
//         'updated_time',
//         'verified',
//         'email',
//       ],
//       callbackURL: '/login/facebook/return',
//       passReqToCallback: true,
//     },
//     async (req, accessToken, refreshToken, profile, done) => {
//       try {
//         if (profile.emails.length)
//           profile.emails[0].verified = !!profile._json.verified;
//         profile.displayName =
//           profile.displayName ||
//           `${profile.name.givenName} ${profile.name.familyName}`;
//         const user = await login(req, 'facebook', profile, {
//           accessToken,
//           refreshToken,
//         });
//         done(null, user);
//       } catch (err) {
//         done(err);
//       }
//     },
//   ),
// );

// // https://github.com/jaredhanson/passport-twitter
// passport.use(
//   new TwitterStrategy(
//     {
//       consumerKey: process.env.TWITTER_KEY,
//       consumerSecret: process.env.TWITTER_SECRET,
//       callbackURL: '/login/twitter/return',
//       includeEmail: true,
//       includeStatus: false,
//       passReqToCallback: true,
//     },
//     async (req, token, tokenSecret, profile, done) => {
//       try {
//         if (profile.emails && profile.emails.length)
//           profile.emails[0].verified = true;
//         const user = await login(req, 'twitter', profile, {
//           token,
//           tokenSecret,
//         });
//         done(null, user);
//       } catch (err) {
//         done(err);
//       }
//     },
//   ),
// );

passport.use(
  new OIDCStrategy(
    {
      identityMetadata: 'https://login.microsoftonline.com/common/v2.0/.well-known/openid-configuration',
      clientID: process.env.AZURE_ID,
      clientSecret: process.env.AZURE_SECRET,
      responseType: 'code id_token',
      responseMode: 'form_post',
      redirectUrl: `${process.env.GATEWAY_BASE_URL}login/azuread-openidconnect/return/`,
      allowHttpForRedirectUrl: true,
      validateIssuer: false,
      isB2C: false,
      issuer: null,
      passReqToCallback: true,
      scope: ['profile', 'offline_access'],
      loggingLevel: 'info',
      nonceLifetime: null,
      nonceMaxAmount: 5,
      useCookieInsteadOfSession: false,
      clockSkew: null,
    },
    async (req, iss, sub, profile, accessToken, refreshToken, done) => {
      try {
        const azProfile = {
          ...profile,
          id: profile._json.oid,
          emails: [{
            value: profile._json.preferred_username,
            verified: true,
          }]
        }
        const user = await login(req, 'azuread-openidconnect', azProfile, {
          accessToken,
          refreshToken,
        });
        done(null, user);
      } catch (err) {
        done(err);
      }
    },
  ),
);

export default passport;
