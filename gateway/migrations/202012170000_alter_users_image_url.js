exports.up = async (db) => {
  await db.schema.alterTable('users', table => {
    table.text('image_url').alter();
  });
};

module.exports.down = async db => {
  await db.schema.alterTable('users', table => {
    table.string('image_url', 200).alter();
  });
};
