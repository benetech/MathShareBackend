module.exports.up = async db => {
  await db.schema.table('user_configs', table => {
    table.jsonb('analytics').notNullable().defaultTo('{}');
  });
};

module.exports.down = async db => {
  await db.schema.table('user_configs', table => {
    table.dropColumn('analytics');
  });
};

module.exports.configuration = { transaction: true };
