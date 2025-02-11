const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';

    const pawns = await db.getAllPawns(req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
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

const changePawnToSale = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.changePawnToSale(id, tableName);
        res.status(200).json({ message: "Pawn closed successfully!" }); // Send a success response
    } catch (error) {
        res.status(500).json({ message: "Error closing pawn" }); // Send error message
    }
})

module.exports = {
    getAllPawns,
    continuePawn,
    closePawn,
    changePawnToSale,
}