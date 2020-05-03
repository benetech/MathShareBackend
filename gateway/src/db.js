/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import knex from 'knex';
import util from 'util';

export const dbConfig = {
  client: 'pg',
  connection: process.env.DATABASE_URL,
  migrations: {
    tableName: 'migrations',
  },
  debug: process.env.DATABASE_DEBUG === 'true',
};

const db = knex(dbConfig);

export const insertOrUpdate = (tableName, tuple, uniqueIdKey) => {
  return db.transaction(trx => {
    let query = trx.raw(util.format(`%s ON CONFLICT (${uniqueIdKey}) DO UPDATE SET %s`,
      trx(tableName).insert(tuple).toString().toString(),
      trx(tableName).update(tuple).whereRaw(`${tableName}.${uniqueIdKey} = '${tuple[uniqueIdKey]}'`).toString().replace(/^update\s.*\sset\s/i, '')
    ))
      .transacting(trx);
    return query.then(trx.commit).catch(trx.rollback);
  });
};

export default db;
