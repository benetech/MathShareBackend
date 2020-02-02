module.exports.up = async db => {
  await db.schema.createTable('partners', table => {
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.string('name', 127);
    table.string('code', 31);
    table.unique(['code']);
    table.timestamps(true, true);
  });
  await db.schema.createTable('partner_api_keys', table => {
    table.uuid('id').notNullable().defaultTo(db.raw('uuid_generate_v1mc()')).primary();
    table.uuid('partner_id').notNullable().references('id').inTable('partners').onDelete('CASCADE').onUpdate('CASCADE');
    table.timestamp('expired_at').defaultTo(null);
    table.timestamps(true, true);
  });
};

module.exports.down = async db => {
  await db.schema.dropTableIfExists('partner_api_keys');
  await db.schema.dropTableIfExists('partners');
};

module.exports.configuration = { transaction: true };
