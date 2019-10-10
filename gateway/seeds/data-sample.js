/**
 * Copyright © 2016-present Kriasoft.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

/* eslint-disable no-restricted-syntax, no-await-in-loop */

const faker = require('faker');

module.exports.seed = async db => {
  // Create 10 random website users (as an example)
  const users = Array.from({ length: 10 }).map(() => ({
    display_name: faker.name.findName(),
    image_url: faker.internet.avatar(),
  }));

  await Promise.all(
    users.map(user =>
      db
        .table('users')
        .insert(user)
        .returning('id')
        .then(rows =>
          db
            .table('users')
            .where('id', '=', rows[0])
            .first()
            .then(u =>
              db
                .table('emails')
                .insert({
                  user_id: u.id,
                  email: faker.internet.email().toLowerCase(),
                })
                .then(() => u),
            ),
        )
        .then(row => Object.assign(user, row)),
    ),
  );
};
