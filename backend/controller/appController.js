const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';
    const limit = req.query.limit;
    const offset = req.query.offset;

    try {
        const pawns = await db.getAllPawns(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
        res.send(pawns);
    } catch (error) {
        res.status(500).send({ message: 'Error query get all pawns' });
    }
});

const getPawn = asyncHandler(async (req, res) => {
    try {
        const pawnInfo = await db.getPawn(req.query.clientId, req.query.category, req.query.pawnId);
        res.status(200).json({ pawnInfo });
    } catch (error) {
        res.status(500).json({ message: "Error query get specified pawn " + error });
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
    let pricePerGram = 0;

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
                date: req.body.date
            }; break;
        case 'gold_pawn' :
            provision = Number(req.body.provision)
            pricePawned = Number(req.body.price_pawned);
            priceToRedeem = pricePawned + (pricePawned * (provision / 100))
            pricePerGram = Number(req.body.price_pawned) / Number(req.body.weight);
            pawnObj = {
                weight: req.body.weight,
                carats: req.body.carats,
                type: req.body.type,
                price_per_gram: pricePerGram,
                price_pawned: pricePawned,
                price_to_redeem: priceToRedeem,
                provision: provision,
                total_days: req.body.total_days,
                description: req.body.description,
                date: req.body.date
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
                date: req.body.date
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
                date: req.body.date
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
                date: req.body.date
            }; break;
    }

    try {
        await db.addNewPawn(category, pawnObj, clientObj);
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,OPTIONS');
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type,Authorization');
        res.status(200).json({ message: "Success" });
    } catch (error) {
        res.status(500).json({ message: "Error query insert new pawn " + error });
    }
})

const updatePawn = asyncHandler(async (req, res) => {
    const { tableName, id, pricePawned, provision, description } = req.body;

    try {
        const pawn = await db.updatePawn(tableName, id, parseInt(pricePawned), parseFloat(provision), description);
        res.status(200).json({ pawn });
    } catch (error) {
        res.status(500).json({ message: "Error query update pawn" + error });
    }
})

const continuePawn = asyncHandler(async (req, res) => {
    const { id, tableName, provision } = req.body;

    try {
        await db.continuePawn(id, tableName, provision);
        res.status(200);
    } catch (error) {
        res.status(500).json({ message: "Error query continuing pawn" });
    }
})

const closePawn = asyncHandler(async (req, res) => {
    const { id, tableName, priceClosed } = req.body;

    try {
        await db.closePawn(id, tableName, priceClosed);
        res.status(200);
    } catch (error) {
        res.status(500).json({ message: "Error query closing pawn" });
    }
})

const changePawnToSale = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.changePawnToSale(id, tableName);
        res.status(200).end();
    } catch (error) {
        // res.status(500).json({ message: "Error query change pawn to sale" }); // Send error message
        res.status(500).json({ message: error }); // Send error message
    }
})

const getAllSales = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';
    const limit = req.query.limit;
    const offset = req.query.offset;

    const sales = await db.getAllSales(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
    res.send(sales);
})

const getSale = asyncHandler(async (req, res) => {

    try {
        const saleInfo = await db.getSale(req.query.clientId, req.query.saleId);
        res.status(200).json({ saleInfo });
    } catch (error) {
        res.status(500).json({ message: "Error query get specific sale " + error });
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
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,OPTIONS');
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type,Authorization');
        res.status(200).json({ message: "Success" });
    } catch (error) {
        res.status(500).json({ message: "Error query insert new sale " + error });
    }
})

const sellItem = asyncHandler(async (req, res) => {
    const { id, priceSold } = req.body;

    try {
        const moneyIntoCashReg = await db.closeSale(id, priceSold);
        res.status(200).json({ moneyIntoCashReg });
    } catch (error) {
        res.status(500).json({ message: "Error query sell item" });
    }
})

const getAllClients = asyncHandler(async (req, res) => {
    const limit = req.query.limit;
    const offset = req.query.offset;
    const search = req.query.search.toLowerCase();

    try {
        const clients = await db.getAllClients(limit, offset, search);
        res.send(clients);
    } catch (error) {
        res.status(500).json({ message: "Error query get all clients" });
    }
})

const getCashRegister = asyncHandler(async (req, res) => {

    try {
        const cashReg = await db.getCashRegister();
        res.status(200).json({ cashReg: cashReg[0] });
    } catch (error) {
        res.status(500).json({ message: "Error query get cash register" });
    }
})

const insertIntoCashRegister = asyncHandler(async (req, res) => {
    const { amount, description } = req.body;

    try {
        await db.insertIntoCashRegister(Number(amount), description);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ message: "Error query insert money into cash register " + error });
    }
})

const removeFromCashRegister = asyncHandler(async (req, res) => {
    const { amount, description } = req.body;

    try {
        await db.removeFromCashRegister(Number(amount), description);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ message: "Error query remove money from cash register " + error });
    }
})

const getAllTransactions = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByDate = req.query.searchByDate && req.query.searchByDate !== 'undefined' && req.query.searchByDate !== '' ? req.query.searchByDate : null;
    const limit = req.query.limit;
    const offset = req.query.offset;

    const transactions = await db.getAllTransactions(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByDate);
    res.send(transactions);
})

const getDailyReport = asyncHandler(async (req, res) => {
    try {
        const report = await db.getDailyReport(req.query.date)
        res.status(200).send(report);
    } catch (error) {
        res.status(500).json({ message: "Error query get daily report" });
    }
})

module.exports = {
    getAllPawns,
    getPawn,
    insertPawn,
    updatePawn,
    continuePawn,
    closePawn,
    changePawnToSale,
    getAllSales,
    getSale,
    insertSale,
    sellItem,
    getAllClients,
    getCashRegister,
    insertIntoCashRegister,
    removeFromCashRegister,
    getAllTransactions,
    getDailyReport
}