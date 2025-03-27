const express = require('express');
const app = express();

app.use(express.json());  // <-- This enables JSON body parsing
app.use(express.urlencoded({ extended: true })); // (Optional, for form data)

const cors = require('cors');
app.use(cors()); // This allows your frontend to make requests to the backend

const appController = require('./controller/appController');
app.get('/', appController.getAllPawns);
app.get('/getPawn', appController.getPawn);
app.post('/insertPawn', appController.insertPawn)
app.put('/updatePawn', appController.updatePawn)
app.put('/continuePawn', appController.continuePawn)
app.put('/closePawn', appController.closePawn)
app.put('/changePawnToSale', appController.changePawnToSale)

app.get('/sales', appController.getAllSales)
app.get('/sales/getSale', appController.getSale)
app.post('/sales/insertSale', appController.insertSale)
app.put('/sales/sellItem', appController.sellItem)

app.get('/cashRegister', appController.getCashRegister)
app.put('/cashRegister/insert', appController.insertIntoCashRegister)
app.put('/cashRegister/remove', appController.removeFromCashRegister)

app.get('/transactions', appController.getAllTransactions)

app.get('/clients', appController.getAllClients)

app.get('/dailyReport', appController.getDailyReport)

app.listen(3000);