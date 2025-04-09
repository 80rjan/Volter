const { app, BrowserWindow } = require('electron');
const path = require('path');
const { spawn } = require('child_process');
const fs = require('fs');

let win;

// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP
// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP
// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP
// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP
// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP
// RUN NPM INSTALL IN BACK END WHEN DOWNLOADING THE EXE APP


function getTimestamp() {
    const now = new Date();
    return now.toLocaleString(); // e.g., "4/5/2025, 4:15:22 PM"
}

function startBackend() {
    // Path to the unpacked backend directory
    const backendPath = path.join(process.resourcesPath, 'app.asar.unpacked', 'backend');
    const serverPath = path.join(backendPath, 'server.js');
    const logFile = fs.createWriteStream(path.join(process.resourcesPath, 'backend.log'), { flags: 'a' });

    // console.log('=== Starting backend ===');
    // console.log(`backendPath: ${backendPath}`);
    // console.log(`serverPath: ${serverPath}`);
    // console.log(`process.execPath: ${process.execPath}`);
    // console.log(`process.resourcesPath: ${process.resourcesPath}`);

    // Call node to run the server.js
    const backendProcess = spawn('node', [serverPath], {
        cwd: backendPath,
        env: process.env,
        stdio: 'pipe'
    });

    backendProcess.stdout?.on('data', (data) => {
        // Do nothing for stdout logs (no logging on successful output)
        // console.log(`stdout: ${data}`);
        // logFile.write(`[${getTimestamp()}] stdout: ${data}\n`);
    });

    backendProcess.stderr?.on('data', (data) => {
        console.error(`[${getTimestamp()}] stderr: ${data}`);
        logFile.write(`[${getTimestamp()}] stderr: ${data}\n`);
    });

    backendProcess.on('error', (err) => {
        console.error(`[${getTimestamp()}] Failed to start backend process:`, err);
        logFile.write(`[${getTimestamp()}] Failed to start backend process: ${err}\n`);
    });

    backendProcess.on('exit', (code) => {
        console.log(`[${getTimestamp()}] Backend process exited with code ${code}`);
        logFile.write(`[${getTimestamp()}] Backend process exited with code ${code}\n`);
    });
}

function createWindow() {
    win = new BrowserWindow({
        title: 'Volter',
        width: 1200,
        height: 800,
        icon: path.join(__dirname, 'frontend', 'public', 'V.ico'),
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
        },
    });

    win.loadFile(path.join(app.getAppPath(), 'frontend/dist/index.html'))
        .catch((err) => console.error('Failed to load file:', err));

    win.on('closed', () => {
        win = null;
    });
}

app.whenReady().then(() => {
    startBackend();  // Start the backend server when the app is ready
    createWindow();

    app.on('activate', () => {
        if (win === null) createWindow();
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') app.quit();
});
