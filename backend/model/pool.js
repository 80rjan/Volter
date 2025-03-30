require('dotenv').config({ path: "../.env" });
// require('dotenv').config();

const { Pool } = require('pg');

console.log(process.env.DB_USER);
console.log(process.env.DB_PASSWORD);
console.log(typeof process.env.DB_PASSWORD);

const pool = new Pool({
    user: process.env.DB_USER,
    host: process.env.DB_HOST,
    database: process.env.DB_NAME,
    password: process.env.DB_PASSWORD,
    port: process.env.DB_PORT,
    ssl: {
        rejectUnauthorized: false
    }
});

pool.connect()
    .then(() => console.log('Connected to the database successfully'))
    .catch(err => {
        console.error('Database connection error:', err.message);
        process.exit(1);  // Optionally stop the server if the connection fails
    });

module.exports = pool;

// module.exports = new Pool({
//     connectionString: 'postgresql://postgres:exBIWlMralbTVhwSiIrHRusVKDndKqRZ@nozomi.proxy.rlwy.net:38325/railway',
//     ssl: {
//         rejectUnauthorized: false
//     }
// });

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