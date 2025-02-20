const { Pool } = require('pg');

module.exports = new Pool({
    connectionString: 'postgresql://postgres:exBIWlMralbTVhwSiIrHRusVKDndKqRZ@nozomi.proxy.rlwy.net:38325/railway',
    ssl: {
        rejectUnauthorized: false
    }
});
//
// module.exports = new Pool({
//     host: 'nozomi.proxy.rlwy.net',  // Railway host URL
//     port: 38325,                    // Railway PostgreSQL port (or default 5432)
//     user: 'postgres',                // Your PostgreSQL user
//     password: 'exBIWlMralbTVhwSiIrHRusVKDndKqRZ',  // Your password
//     database: 'railway',             // Your database name (on Railway)
//     ssl: {
//         rejectUnauthorized: false    // Required for cloud database connections
//     }
// });

// module.exports = new Pool({
//     host: 'localhost',
//     user: 'postgres',
//     password: 'Borjan2004',
//     database: 'pawn_shop',
//     port: 5432
// })