// require('dotenv').config({ path: "../.env" });
// require('dotenv').config();
// const path = require('path');
// require('dotenv').config({ path: path.join(__dirname, 'resources/.env') });

const { Pool } = require('pg');

// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM

console.log(process.env.VOLTER_CENTAR_DB_USER);
console.log(process.env.VOLTER_CENTAR_DB_HOST);
console.log(process.env.VOLTER_CENTAR_DB_NAME);
console.log(process.env.VOLTER_CENTAR_DB_PASS);
console.log(process.env.VOLTER_CENTAR_DB_PORT);

// const pool = new Pool({
//     user: process.env.VOLTER_CENTAR_DB_USER,
//     host: process.env.VOLTER_CENTAR_DB_HOST,
//     database: process.env.VOLTER_CENTAR_DB_NAME,
//     password: process.env.VOLTER_CENTAR_DB_PASS,
//     port: process.env.VOLTER_CENTAR_DB_PORT,
//     ssl: {
//         rejectUnauthorized: false
//     }
// });
// pool.connect()
//     .then(() => console.log('Connected to the database successfully'))
//     .catch(err => {
//         console.error('Database connection error:', err.message);
//         process.exit(1);  // Optionally stop the server if the connection fails
//     });
//
// module.exports = pool;



// const pool = new Pool({
//     host: 'nozomi.proxy.rlwy.net',  // Railway host URL
//     port: 38325,                    // Railway PostgreSQL port (or default 5432)
//     user: 'postgres',                // Your PostgreSQL user
//     password: 'exBIWlMralbTVhwSiIrHRusVKDndKqRZ',  // Your password
//     database: 'railway',             // Your database name (on Railway)
//     ssl: {
//         rejectUnauthorized: false    // Required for cloud database connections
//     }
// });

module.exports = new Pool({
    connectionString: 'postgresql://postgres:exBIWlMralbTVhwSiIrHRusVKDndKqRZ@nozomi.proxy.rlwy.net:38325/railway',
    ssl: {
        rejectUnauthorized: false
    }
});

// module.exports = new Pool({
//     host: 'localhost',
//     user: 'postgres',
//     password: 'Borjan2004',
//     database: 'pawn_shop',
//     port: 5432
// })