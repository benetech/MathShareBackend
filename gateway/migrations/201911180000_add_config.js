module.exports.up = async db => {

  // Users' configs
  await db.schema.createTable('user_configs', table => {
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.uuid('user_id').notNullable().references('id').inTable('users').onDelete('CASCADE').onUpdate('CASCADE');
    table.jsonb('ui').notNullable().defaultTo('{}');
    table.timestamps(false, true);
    table.unique(['user_id']);
  });
};

module.exports.down = async db => {
  await db.schema.dropTableIfExists('user_configs');
};

module.exports.configuration = { transaction: true };
