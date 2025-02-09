const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    const pawns = await db.getAllPawns();
    res.send(pawns);
})

const continuePawn = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.continuePawn(id, tableName);
        res.status(200).json({ message: "Pawn continued successfully!" }); // Send a success response
    } catch (error) {
        res.status(500).json({ message: "Error continuing pawn" }); // Send error message
    }
})

const closePawn = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.closePawn(id, tableName);
        res.status(200).json({ message: "Pawn closed successfully!" }); // Send a success response
    } catch (error) {
        res.status(500).json({ message: "Error closing pawn" }); // Send error message
    }
})

const addSale = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.addSale(id, tableName);
        res.status(200).json({ message: "Pawn closed successfully!" }); // Send a success response
    } catch (error) {
        res.status(500).json({ message: "Error closing pawn" }); // Send error message
    }
})

module.exports = {
    getAllPawns,
    continuePawn,
    closePawn,
    addSale,
}