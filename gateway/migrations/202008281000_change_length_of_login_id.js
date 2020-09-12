module.exports.up = async db => {
  await db.schema.raw('alter table "logins" alter column "id" type varchar(63);');
};

module.exports.down = async db => {
  await db.schema.raw('alter table "logins" alter column "id" type varchar(36);');
};

module.exports.configuration = { transaction: true };
