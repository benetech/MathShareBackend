module.exports.up = async db => {
  await db.schema.table('user_configs', table => {
    table.jsonb('tts').notNullable().defaultTo('{}');
  });
};

module.exports.down = async db => {
  await db.schema.table('user_configs', table => {
    table.dropColumn('tts');
  });
};

module.exports.configuration = { transaction: true };
