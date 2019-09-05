/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

// Create database schema for storing user accounts, logins and authentication claims/tokens
// Source https://github.com/membership/membership.db
// prettier-ignore
module.exports.up = async db => {
  // User accounts
  await db.schema.createTable('users', table => {
    // UUID v1mc reduces the negative side effect of using random primary keys
    // with respect to keyspace fragmentation on disk for the tables because it's time based
    // https://www.postgresql.org/docs/current/static/uuid-ossp.html
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.string('display_name', 100);
    table.string('image_url', 200);
    table.string('password_hash', 128);
    table.timestamps(false, true);
  });

  // Users' email addresses
  await db.schema.createTable('emails', table => {
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.uuid('user_id').notNullable().references('id').inTable('users').onDelete('CASCADE').onUpdate('CASCADE');
    table.string('email', 100).notNullable();
    table.boolean('verified').notNullable().defaultTo(false);
    table.boolean('primary').notNullable().defaultTo(false);
    table.timestamps(false, true);
    table.unique(['user_id', 'email', 'verified']);
  });

  // External logins with security tokens (e.g. Google, Facebook, Twitter)
  await db.schema.createTable('logins', table => {
    table.uuid('user_id').notNullable().references('id').inTable('users').onDelete('CASCADE').onUpdate('CASCADE');
    table.string('provider', 32).notNullable();
    table.string('id', 36).notNullable();
    table.string('username', 100);
    table.jsonb('tokens').notNullable();
    table.jsonb('profile').notNullable();
    table.timestamps(false, true);
    table.primary(['provider', 'id']);
  });
};

module.exports.down = async db => {
  await db.schema.dropTableIfExists('logins');
  await db.schema.dropTableIfExists('emails');
  await db.schema.dropTableIfExists('users');
};

module.exports.configuration = { transaction: true };
