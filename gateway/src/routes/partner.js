import { Router } from 'express';
import db from '../db';

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
        expiresAt += ` + INTERVAL '${Math.round(Number(expiresAfter))} days'`
    }
    await db.table('partner_api_keys')
        .update({
            expired_at: db.raw(expiresAt)
        }).whereRaw(`id != '${partnerApiKey.id}' and partner_id = '${partnerId}' and expired_at is null`)
    return partnerApiKey;
}

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

router.post('/partner/key', async (req, res) => {
    if (req.headers['x-auth-token'] !== process.env.SESSION_SECRET) {
        res.status(401).send({ message: 'Unauthorized' });
    } else {
        try {
            const { partnerId, expiresAfter } = req.body;
            if (partnerId) {
                res.status(200).send(await addApiKeyForPartner(partnerId, expiresAfter));
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

export default router;
