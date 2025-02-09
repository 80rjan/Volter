const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    const pawns = await db.getAllPawns();
    res.send(pawns);
})

module.exports = {
    getAllPawns
}