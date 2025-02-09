const express = require('express');
const app = express();

const cors = require('cors');
app.use(cors()); // This allows your frontend to make requests to the backend

const appController = require('./controller/appController');
app.get('/', appController.getAllPawns);

app.listen(3000);