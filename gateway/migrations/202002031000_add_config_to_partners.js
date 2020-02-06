module.exports.up = async db => {
  await db.schema.table('partners', table => {
    table.jsonb('config').notNullable().defaultTo('{}');
  });
};

module.exports.down = async db => {
  await db.schema.table('partners', table => {
    table.dropColumn('config');
  });
};

module.exports.configuration = { transaction: true };
