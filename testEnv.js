require('dotenv').config();  // Load environment variables

console.log('DB_USER:', process.env.DB_USER);  // Log the DB_USER from .env
console.log('DB_PASSWORD:', process.env.DB_PASSWORD);  // Log the DB_PASSWORD from .env
console.log('DB_HOST:', process.env.DB_HOST);  // Log the DB_HOST from .env
