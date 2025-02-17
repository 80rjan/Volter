const { Pool } = require('pg');

// module.exports = new Pool({
//     connectionString: 'postgresql://postgres:exBIWlMralbTVhwSiIrHRusVKDndKqRZ@nozomi.proxy.rlwy.net:38325/railway',
//     ssl: {
//         rejectUnauthorized: false // Required for some cloud providers
//     }
// });

module.exports = new Pool({
    host: 'localhost',
    user: 'postgres',
    password: 'Borjan2004',
    database: 'pawn_shop',
    port: 5432
})