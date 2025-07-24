const path = require('path');
require('dotenv').config({ path: path.resolve(__dirname, '../.env') });

const { Pool } = require('pg');

// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM
// SET UP ENVIRONMENT VARIABLES IN THE SYSTEM

console.log(process.env.VITE_DB_USER);
console.log(process.env.VITE_DB_HOST);
console.log(process.env.VITE_DB_NAME);
console.log(process.env.VITE_DB_PASSWORD);
console.log(process.env.VITE_DB_PORT);

const pool = new Pool({
    user: process.env.VITE_DB_USER,
    host: process.env.VITE_DB_HOST,
    database: process.env.VITE_DB_NAME,
    password: process.env.VITE_DB_PASSWORD,
    port: process.env.VITE_DB_PORT,
    ssl: {
        rejectUnauthorized: false
    }
});
pool.connect()
    .then(() => {
        console.log('Connected to the database successfully')
    })
    .catch(err => {
        console.error('Database connection error:', err.message);
        process.exit(1);  // Optionally stop the server if the connection fails
    });

module.exports = pool;


// module.exports = new Pool({
//     host: 'localhost',
//     user: 'postgres',
//     password: 'Borjan2004',
//     database: 'pawn_shop',
//     port: 5432
// })