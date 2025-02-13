const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';

    const pawns = await db.getAllPawns(req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
    res.send(pawns);
})

const insertPawn = asyncHandler(async (req, res) => {
    let category = req.body.category;
    let clientObj = {
        name: req.body.name,
        embg: req.body.embg,
        telephone: req.body.telephone,
        city: req.body.city
    }
    let pawnObj = null;
    let priceToRedeem = 0;

    switch (category) {
        case 'electronics_pawn' :
            priceToRedeem = req.body.price_pawned + (req.body.price_pawned * (req.body.provision / 100));
            pawnObj = {
            brand: req.body.brand,
            year: req.body.year,
            price_pawned: req.body.price_pawned,
            price_to_redeem: priceToRedeem,
            provision: req.body.provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'gold_pawn' :
            let pricePawned = req.body.weight * req.body.price_per_gram;
            priceToRedeem = pricePawned * (req.body.provision / 100);
            pawnObj = {
            weight: req.body.weight,
            carats: req.body.carats,
            type: req.body.type,
            price_per_gram: req.body.price_per_gram,
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: req.body.provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'vehicle_pawn' :
            priceToRedeem = req.body.price_pawned + (req.body.price_pawned * (req.body.provision / 100));
            pawnObj = {
            brand: req.body.brand,
            model: req.body.model,
            year: req.body.year,
            price_pawned: req.body.price_pawned,
            price_to_redeem: priceToRedeem,
            provision: req.body.provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'watch_pawn' :
            priceToRedeem = req.body.price_pawned + (req.body.price_pawned * (req.body.provision / 100));
            pawnObj = {
            brand: req.body.brand,
            year: req.body.year,
            price_pawned: req.body.price_pawned,
            price_to_redeem: priceToRedeem,
            provision: req.body.provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'other_pawn' :
            priceToRedeem = req.body.price_pawned + (req.body.price_pawned * (req.body.provision / 100));
            pawnObj = {
            price_pawned: req.body.price_pawned,
            price_to_redeem: priceToRedeem,
            provision: req.body.provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
    }

    try {
        await db.addNewPawn(category, pawnObj, clientObj);
        res.status(200).redirect('http://localhost:5173');
    } catch (error) {
        res.status(500).json({ message: "Error adding new pawn " + error });
    }
})

const continuePawn = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        const profit = await db.continuePawn(id, tableName);
        res.status(200).json({ profit });
    } catch (error) {
        res.status(500).json({ message: "Error continuing pawn" });
    }
})

const closePawn = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        const moneyIntoCashReg = await db.closePawn(id, tableName);
        res.status(200).json({ moneyIntoCashReg });
    } catch (error) {
        res.status(500).json({ message: "Error closing pawn" });
    }
})

const changePawnToSale = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        const profit = await db.changePawnToSale(id, tableName);
        res.status(200).json({ profit }); // Send a success response
    } catch (error) {
        res.status(500).json({ message: "Error closing pawn" }); // Send error message
    }
})

module.exports = {
    getAllPawns,
    insertPawn,
    continuePawn,
    closePawn,
    changePawnToSale,
}