const pool = require("./pool"); // Import your database configuration

async function testConnection() {
    try {
        const res = await pool.query("SELECT NOW()");
        console.log("Connected to DB:", res.rows);
    } catch (err) {
        console.error("Database connection error:", err);
    } finally {
        pool.end();
    }
}

testConnection();
