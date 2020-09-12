module.exports.up = async db => {
  await db.schema.createTable('lti_consumers', table => {
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.string('client_secret', 255);
    table.string('client_id', 127);
    table.string('base_url', 127);
    table.unique(['client_id']);
    table.timestamps(true, true);
  });
};

module.exports.down = async db => {
  await db.schema.dropTableIfExists('lti_consumers');
};

module.exports.configuration = { transaction: true };
