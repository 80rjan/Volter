const { app, BrowserWindow } = require('electron');
const path = require('path');
require('dotenv').config();


let win;

function createWindow() {
    win = new BrowserWindow({
        title: 'Volter',
        width: 1200,
        height: 800,
        icon: path.join(__dirname, 'frontend', 'public', 'V.ico'),
        webPreferences: {
            nodeIntegration: false, // Disable Node.js integration for security
            contextIsolation: true, // Isolate context
        },
    });

    if (process.env.NODE_ENV === 'development') {
        win.loadURL('http://localhost:5173').catch((err) => {
            console.error('Failed to load URL:', err);
        }); // URL for Vite's development server
    } else {
        // In production, load the app from the build directory (Vite's production build)
        win.loadFile(path.join(__dirname, 'frontend','index.html')).catch((err) => {
            console.error('Failed to load file:', err);
        });
    }


    win.on('closed', () => {
        win = null;
    });
}

app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
        if (win === null) {
            createWindow();
        }
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});
