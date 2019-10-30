/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* @flow */

import knex from 'knex';

export const dbConfig = {
  client: 'pg',
  connection: process.env.DATABASE_URL,
  migrations: {
    tableName: 'migrations',
  },
  debug: process.env.DATABASE_DEBUG === 'true',
};

const db = knex(dbConfig);

export default db;
