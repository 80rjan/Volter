const asyncHandler = require("express-async-handler");
const db = require('../model/queries');
const {resume} = require("react-dom/server");

const getAllPawns = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';

    const pawns = await db.getAllPawns(req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
    res.send(pawns);
})

const getPawn = asyncHandler(async (req, res) => {
    try {
        const pawnInfo = await db.getPawn(req.query.clientId, req.query.category, req.query.pawnId);
        res.status(200).json({ pawnInfo });
    } catch (error) {
        res.status(500).json({ message: "Error getting specified pawn " + error });
    }
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
    let pricePawned = 0;
    let provision = 0;

    switch (category) {
        case 'electronics_pawn' :
            provision = Number(req.body.provision);
            pricePawned = Number(req.body.price_pawned)
            priceToRedeem = pricePawned + (pricePawned * (provision / 100));
            pawnObj = {
            brand: req.body.brand,
            year: req.body.year,
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'gold_pawn' :
            provision = Number(req.body.provision)
            pricePawned = Number(req.body.weight) * Number(req.body.price_per_gram);
            priceToRedeem = pricePawned + (pricePawned * (provision / 100))
            pawnObj = {
            weight: req.body.weight,
            carats: req.body.carats,
            type: req.body.type,
            price_per_gram: req.body.price_per_gram,
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'vehicle_pawn' :
            provision = Number(req.body.provision);
            pricePawned = Number(req.body.price_pawned)
            priceToRedeem = pricePawned + (pricePawned * (provision / 100));
            pawnObj = {
            brand: req.body.brand,
            model: req.body.model,
            year: req.body.year,
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'watch_pawn' :
            provision = Number(req.body.provision);
            pricePawned = Number(req.body.price_pawned)
            priceToRedeem = pricePawned + (pricePawned * (provision / 100));
            pawnObj = {
            brand: req.body.brand,
            year: req.body.year,
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: provision,
            total_days: req.body.total_days,
            description: req.body.description,
        }; break;
        case 'other_pawn' :
            provision = Number(req.body.provision);
            pricePawned = Number(req.body.price_pawned)
            priceToRedeem = pricePawned + (pricePawned * (provision / 100));
            pawnObj = {
            price_pawned: pricePawned,
            price_to_redeem: priceToRedeem,
            provision: provision,
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

const getAllSales = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';

    const sales = await db.getAllSales(req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
    res.send(sales);
})

const getSale = asyncHandler(async (req, res) => {
    console.log("Im here")
    console.log(req.query.clientId)
    console.log(req.query.saleId)

    try {
        const saleInfo = await db.getSale(req.query.clientId, req.query.saleId);
        res.status(200).json({ saleInfo });
    } catch (error) {
        res.status(500).json({ message: "Error getting specified sale " + error });
    }
})

const insertSale = asyncHandler(async (req, res) => {
    let clientObj = {
        name: req.body.name,
        embg: req.body.embg,
        telephone: req.body.telephone,
        city: req.body.city
    }
    let saleObj = {
        priceBought: req.body.price_bought,
        description: req.body.description
    }

    try {
        await db.addNewSale(saleObj, clientObj);
        res.status(200).redirect('http://localhost:5173/sales');
    } catch (error) {
        res.status(500).json({ message: "Error adding new sale " + error });
    }
})

const sellItem = asyncHandler(async (req, res) => {
    const { id, priceSold } = req.body;

    try {
        const moneyIntoCashReg = await db.closeSale(id, priceSold);
        res.status(200).json({ moneyIntoCashReg });
    } catch (error) {
        res.status(500).json({ message: "Error selling item" });
    }
})

const getCashRegister = asyncHandler(async (req, res) => {

    try {
        const cashReg = await db.getCashRegister();
        console.log(cashReg[0])
        res.status(200).json({ cashReg: cashReg[0] });
    } catch (error) {
        res.status(500).json({ message: "Error getting cash register" });
    }
})

module.exports = {
    getAllPawns,
    getPawn,
    insertPawn,
    continuePawn,
    closePawn,
    changePawnToSale,
    getAllSales,
    getSale,
    insertSale,
    sellItem,
    getCashRegister,

}