import { Router } from 'express';
import axios from 'axios';
import FormData from 'form-data';
import db from '../db';

const router = new Router();

router.post('/oauth2/lti/access', async (req, res) => {
  try {
    if (!req.user) {
      return res
        .status(401)
        .send({ error: 'Not authorized to generate access token' });
    }

    const oauthClientId =
      req.query.client_id || req.body.clientId || req.session.oauthClientId;
    if (!oauthClientId) {
      res
        .status(400)
        .send({ error: 'client_id or clientId not found in the request' });
      return;
    }
    const ltiConsumer = await db
      .table('lti_consumers')
      .where({ client_id: oauthClientId })
      .first();
    console.log('ltiConsumer', ltiConsumer);
    if (!ltiConsumer) {
      res.status(404).send({ error: 'LTI consumer not found' });
      return;
    }

    const userTokens = await db
      .table('logins')
      .innerJoin('users', 'users.id', 'logins.user_id')
      .where({
        'logins.provider': `lti:${oauthClientId}`,
        'users.id': req.user.id,
      })
      .orderBy('logins.id', 'desc')
      .first('logins.tokens');

    console.log('userTokens', userTokens);

    if (!userTokens || !userTokens.tokens) {
      res.status(403).send({ error: "Service hasn't been authorized" });
      return;
    }

    const data = new FormData();
    data.append('grant_type', 'refresh_token');
    data.append('client_id', oauthClientId);
    data.append('client_secret', ltiConsumer.client_secret);
    data.append('refresh_token', userTokens.tokens.refreshToken);
    const response = await axios({
      method: 'post',
      url: `${ltiConsumer.base_url}/login/oauth2/token`,
      headers: data.getHeaders(),
      data,
    });
    console.log('response', response);
    res.status(response.status).send(response.data);
  } catch (error) {
    console.log('error', error);
    res.status(500).send({ error: 'Server Error' });
  }
});

router.get('/oauth2/lti/consumers/available', async (req, res) => {
  const consumers = await db
    .table('lti_consumers')
    .select('client_id', 'base_url');
  res.send(consumers);
});

router.get('/oauth2/lti/consumers/authorized', async (req, res) => {
  if (!req.user) {
    return res
      .status(401)
      .send({ error: 'Not authorized to generate access token' });
  }
  const providers = await db
    .table('logins')
    .innerJoin('users', 'users.id', 'logins.user_id')
    .where({ 'users.id': req.user.id })
    .where('logins.provider', 'like', 'lti:%')
    .orderBy('logins.id', 'desc')
    .pluck('logins.provider')
    .map(provider => provider.split('lti:')[1]);
  const ltiConsumers = await db
    .table('lti_consumers')
    .whereIn('client_id', providers)
    .select('client_id', 'base_url');
  res.send(ltiConsumers);
});

export default router;
