const { Pool } = require('pg');

module.exports = new Pool({
    host: 'localhost',
    user: 'postgres',
    password: 'Borjan2004',
    database: 'pawn_shop',
    port: 5432
})