const asyncHandler = require("express-async-handler");
const db = require('../model/queries');

const getAllPawns = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';
    let searchByCategory = req.query.searchByCategory !== 'undefined' ? req.query.searchByCategory : '';
    const limit = req.query.limit;
    const offset = req.query.offset;

    try {
        const pawns = await db.getAllPawns(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory);
        res.send(pawns).end();
    } catch (error) {
        res.status(500).send({ message: `Error query get all pawns ${error}` }).end();
    }
});

const getPawn = asyncHandler(async (req, res) => {
    try {
        const pawnInfo = await db.getPawn(req.query.clientId, req.query.category, req.query.pawnId);
        res.status(200).json({ pawnInfo }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get specified pawn " + error }).end();
    }
})

const insertPawn = asyncHandler(async (req, res) => {
    let category = req.body.category;
    let clientObj = {
        name: req.body.name,
        embg: req.body.embg,
        telephone: req.body.telephone,
        telephone_2: req.body.telephone_2,
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
            priceToRedeem = pricePawned + provision;
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
            priceToRedeem = pricePawned + provision;
            pricePerGram = Number(req.body.price_pawned) / Number(req.body.weight);
            pawnObj = {
                weight: req.body.weight,
                carats: req.body.carats,
                type: req.body.type,
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
            priceToRedeem = pricePawned + provision;
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
            priceToRedeem = pricePawned + provision;
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
            priceToRedeem = pricePawned + provision;
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
        res.status(200).json({ message: "Success" }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query insert new pawn " + error }).end();
    }
})

const updatePawn = asyncHandler(async (req, res) => {
    const { tableName, id, pricePawned, provision, description, goldGramsDiff, totalDays } = req.body;

    try {
        const pawn = await db.updatePawn(tableName, id, parseInt(pricePawned), parseFloat(provision), description, goldGramsDiff, totalDays);
        res.status(200).json({ pawn }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query update pawn" + error }).end();
    }
})

const continuePawn = asyncHandler(async (req, res) => {
    const { id, tableName, provision, description, carryOverDays } = req.body;

    try {
        await db.continuePawn(id, tableName, provision, description, carryOverDays);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ error: "Error query continuing pawn" }).end();
    }
})

const closePawn = asyncHandler(async (req, res) => {
    const { id, tableName, priceClosed, description } = req.body;

    try {
        await db.closePawn(id, tableName, priceClosed, description);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ message: `Error query closing pawn ${error}` }).end();
    }
})


const changePawnToSale = asyncHandler(async (req, res) => {
    const { id, tableName } = req.body;

    try {
        await db.changePawnToSale(id, tableName);
        res.status(200).end();
    } catch (error) {
        // res.status(500).json({ message: "Error query change pawn to sale" }); // Send error message
        res.status(500).json({ message: error }).end(); // Send error message
    }
})

const getAllSales = asyncHandler(async (req, res) => {
    const sales = await db.getAllSales(req.query.limit, req.query.offset, req.query.orderBy, req.query.orderDirection);
    res.send(sales).end();
})

const getSale = asyncHandler(async (req, res) => {

    try {
        const sale = await db.getSale(req.query.saleId);
        res.status(200).json({ sale }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get specific sale " + error }).end();
    }
})

const insertSale = asyncHandler(async (req, res) => {
    const { price_bought, description } = req.body;

    try {
        await db.addNewSale(Number(price_bought), description);
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,OPTIONS');
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type,Authorization');
        res.status(200).json({ message: "Success" }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query insert new sale " + error }).end();
    }
})

const sellItem = asyncHandler(async (req, res) => {
    const { id, priceSold, description } = req.body;

    try {
        const moneyIntoCashReg = await db.closeSale(id, priceSold, description);
        res.status(200).json({ moneyIntoCashReg }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query sell item" }).end();
    }
})


const getAllClientsAutocomplete = asyncHandler(async (req, res) => {
    const limit = req.query.limit;
    const offset = req.query.offset;
    const search = req.query.search.toLowerCase();

    try {
        const clients = await db.getAllClientsAutocomplete(limit, offset, search);
        res.send(clients).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get all clients" }).end();
    }
})

const getAllClients = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByTel = req.query.searchByTel !== 'undefined' ? req.query.searchByTel : '';
    const limit = req.query.limit;
    const offset = req.query.offset;

    try {
        const clients = await db.getAllClients(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByTel);
        res.send(clients).end();
    } catch (error) {
        res.status(500).send({ message: `Error query get all clients ${error}` }).end();
    }
});


const getCashRegister = asyncHandler(async (req, res) => {

    try {
        const rows = await db.getCashRegister();
        res.status(200).json({ cashReg: rows.cashRegister, profit: rows.profit }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get cash register" }).end();
    }
})

const insertIntoCashRegister = asyncHandler(async (req, res) => {
    const { amount, description } = req.body;

    try {
        await db.insertIntoCashRegister(Number(amount), description);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ message: "Error query insert money into cash register " + error }).end();
    }
})

const removeFromCashRegister = asyncHandler(async (req, res) => {
    const { amount, description } = req.body;

    try {
        await db.removeFromCashRegister(Number(amount), description);
        res.status(200).end();
    } catch (error) {
        res.status(500).json({ message: "Error query remove money from cash register " + error }).end();
    }
})


const getAllExpenses = asyncHandler(async (req, res) => {
    let searchByMonth = req.query.searchByMonth;
    let searchByYear = req.query.searchByYear;
    const limit = req.query.limit;
    const offset = req.query.offset;

    const expenses = await db.getAllExpenses(limit, offset, req.query.orderBy, req.query.orderDirection, searchByMonth, searchByYear);
    res.send(expenses).end();
})

const getExpense = asyncHandler(async (req, res) => {
    let month = req.query.month;
    let year = req.query.year;

    const expense = await db.getExpense(month, year);
    res.send(expense).end();
})

const insertExpense = asyncHandler(async (req, res) => {
    const { year, month, rent, salaries, bills, other, description } = req.body;

    try {
        const message = await db.insertExpense(Number(year), Number(month), Number(rent), Number(salaries), Number(bills), Number(other), description);
        res.status(200).json({ message: message }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query insert money into cash register " + error }).end();
    }
})


const getAllTransactions = asyncHandler(async (req, res) => {
    let searchByName = req.query.searchByName !== 'undefined' ? req.query.searchByName.toLowerCase() : '';
    let searchByEmbg = req.query.searchByEmbg !== 'undefined' ? req.query.searchByEmbg : '';
    let searchByDate = req.query.searchByDate && req.query.searchByDate !== 'undefined' && req.query.searchByDate !== '' ? req.query.searchByDate : null;
    let searchByCategory = req.query.searchByCategory !== 'undefined' ? req.query.searchByCategory : '';
    const limit = req.query.limit;
    const offset = req.query.offset;

    const transactions = await db.getAllTransactions(limit, offset, req.query.orderBy, req.query.orderDirection, searchByName, searchByEmbg, searchByDate, searchByCategory);
    res.send(transactions).end();
})

const getDailyReport = asyncHandler(async (req, res) => {
    try {
        const report = await db.getDailyReport(req.query.date)
        res.status(200).send(report).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get daily report" }).end();
    }
})

const getPeriodReport = asyncHandler(async (req, res) => {
    try {
        const report = await db.getPeriodReport(req.query.dateFrom, req.query.dateTo)
        res.status(200).send(report).end();
    } catch (error) {
        res.status(500).json({ message: "Error query get period report" }).end();
    }
})

const getAllMonthlyReports = asyncHandler(async (req, res) => {
    let searchByMonth = req.query.searchByMonth;
    let searchByYear = req.query.searchByYear;
    const limit = req.query.limit;
    const offset = req.query.offset;

    const reports = await db.getAllMonthlyReports(limit, offset, req.query.orderBy, req.query.orderDirection, searchByMonth, searchByYear);
    res.send(reports).end();
})

const getMonthlyReport = asyncHandler(async (req, res) => {
    const year = req.query.year;
    const month = req.query.month;

    const report = await db.getMonthlyReport(year, month);
    res.status(200).send(report).end();
})

const generateNewMonthReport = asyncHandler(async (req, res) => {
    const { year, month } = req.body;

    try {
        const { passed, message } = await db.generateNewMonthReport(year, month);
        res.status(200).json({ passed: passed, message: message }).end();
    } catch (error) {
        res.status(500).json({ message: "Error query generate new month report " + error }).end();
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
    getAllClientsAutocomplete,
    getCashRegister,
    insertIntoCashRegister,
    removeFromCashRegister,
    getAllExpenses,
    getExpense,
    insertExpense,
    getAllTransactions,
    getDailyReport,
    getPeriodReport,
    getAllMonthlyReports,
    getMonthlyReport,
    generateNewMonthReport,
}