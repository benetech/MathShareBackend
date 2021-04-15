import { Router } from 'express';
import axios from 'axios';
import db from '../db';
import {
  getPayload,
  getRequestHeaders,
  getUrlFromConfig,
} from '../helpers/partner';

export const defaultRedirect = process.env.CORS_ORIGIN.split(',')[0];

const router = new Router();

const addApiKeyForPartner = async (partnerId, expiresAfter = null) => {
  const [partnerApiKey] = await db
    .table('partner_api_keys')
    .insert({
      partner_id: partnerId,
    })
    .returning('*');
  let expiresAt = 'CURRENT_TIMESTAMP';
  if (expiresAfter) {
    expiresAt += ` + INTERVAL '${Math.round(Number(expiresAfter))} days'`;
  }
  await db
    .table('partner_api_keys')
    .update({
      expired_at: db.raw(expiresAt),
    })
    .whereRaw(
      `id != '${partnerApiKey.id}' and partner_id = '${partnerId}' and expired_at is null`,
    );
  return partnerApiKey;
};

router.post('/partner', async (req, res) => {
  if (req.headers['x-auth-token'] !== process.env.SESSION_SECRET) {
    res.status(401).send({ message: 'Unauthorized' });
  } else {
    try {
      const [partner] = await db
        .table('partners')
        .insert({
          name: req.body.name,
          code: req.body.code,
          config: req.body.config || {},
        })
        .returning('*');
      partner.apiKey = await addApiKeyForPartner(partner.id);
      res.status(200).send(partner);
    } catch (error) {
      console.log('error', error);
      if (error.constraint === 'partners_code_unique') {
        res.status(400).send({
          message: 'code already exists',
        });
      } else {
        res.status(500).end();
      }
    }
  }
});

router.patch('/partner', async (req, res) => {
  if (req.headers['x-auth-token'] !== process.env.SESSION_SECRET) {
    res.status(401).send({ message: 'Unauthorized' });
  } else {
    try {
      const { partnerId, name, config } = req.body;
      if (!partnerId) {
        res.status(500).send({
          message: 'partnerId is required field',
        });
      } else {
        const [partner] = await db
          .table('partners')
          .where({
            id: partnerId,
          })
          .update({
            name,
            config: config || {},
          })
          .returning('*');
        res.status(200).send(partner);
      }
    } catch (error) {
      console.log('error', error);
      res.status(500).end();
    }
  }
});

router.post('/partner/submit', async (req, res) => {
  let payload = null;
  let authHeaders = null;
  try {
    const { id, editCode, shareCode } = req.body;
    if (id) {
      const solution = await db
        .table('problem_set_revision_solution')
        .where({
          id,
        })
        .select('source', 'edit_code', 'metadata')
        .first();
      if (!solution) {
        res.status(400).send({
          message: 'Solution not found',
        });
      } else {
        const { metadata, source } = solution;
        let metadataFinal = {};
        try {
          metadataFinal = JSON.parse(metadata);
        } catch (error4) {
          console.log('error4', error4);
        }
        const partner = await db
          .table('partners')
          .where({
            code: source,
          })
          .pluck('config')
          .first();
        if (partner) {
          const { config } = partner;
          if (!config || !config.submit) {
            res.status(400).send({
              message: 'Partner submission not configured',
            });
          } else {
            const path = `http://localhost:8081/problemSet/convert/${editCode}`;
            let encodedEditCode = null;
            try {
              const editCodeRes = await axios.get(path);
              encodedEditCode = editCodeRes.data;
            } catch (error3) {
              console.log('error3', error3);
            }
            if (
              !encodedEditCode ||
              !solution.edit_code ||
              Math.round(Number(solution.edit_code) / 1000) !==
                Math.round(Number(encodedEditCode) / 1000)
            ) {
              res.status(400).send({
                message: 'Not authorised to submit this set',
              });
            } else {
              const mathsharePayload = {
                editCode,
                shareCode,
                metadataFinal,
              };
              payload = getPayload(config, mathsharePayload);
              authHeaders = await getRequestHeaders(config, metadataFinal);
              const url = getUrlFromConfig(config.submit, metadataFinal);
              const submitResponse = await axios.post(url, payload, {
                headers: Object.assign(
                  config.submit.staticHeaders || {},
                  authHeaders || {},
                ),
              });
              if (submitResponse.status === 200) {
                res.status(200).send(payload);
              } else {
                res.status(submitResponse.status).send(submitResponse.data);
              }
            }
          }
        } else {
          res.status(400).send({
            message: 'No partner found for Solution Set source',
          });
        }
      }
    } else {
      res.status(400).send({
        message: 'Please pass a valid id',
      });
    }
  } catch (error) {
    console.log('[partner submit] error', error);
    if (error.constraint === 'partner_api_keys_partner_id_foreign') {
      res.status(400).send({
        message: 'partner not found',
      });
    } else {
      res.status(500).send({
        error,
        payload,
        authHeaders,
      });
    }
  }
});

router.post('/partner/key', async (req, res) => {
  if (req.headers['x-auth-token'] !== process.env.SESSION_SECRET) {
    res.status(401).send({ message: 'Unauthorized' });
  } else {
    try {
      const { partnerId, expiresAfter } = req.body;
      if (partnerId) {
        res
          .status(200)
          .send(await addApiKeyForPartner(partnerId, expiresAfter));
      } else {
        res.status(400).send({
          message: 'Please pass a valid partnerId',
        });
      }
    } catch (error) {
      console.log('error', error);
      if (error.constraint === 'partner_api_keys_partner_id_foreign') {
        res.status(400).send({
          message: 'partner not found',
        });
      } else {
        res.status(500).end();
      }
    }
  }
});

router.post('/partner/submitOptions', async (req, res) => {
  try {
    const { partnerCode } = req.body;
    if (partnerCode) {
      const partner = await db
        .table('partners')
        .where({
          code: partnerCode,
        })
        .select('config', 'name')
        .first();
      if (partner) {
        res.status(200).send({
          canSubmit:
            partner.config &&
            !!partner.config.submit &&
            !!partner.config.submit.url,
          name: partner.name || '',
        });
      } else {
        res.status(400).send({
          message: 'Partner not found',
        });
      }
    } else {
      res.status(400).send({
        message: 'Please pass a valid partnerCode',
      });
    }
  } catch (error) {
    console.log('error', error);
    res.status(500).end();
  }
});

export default router;
