import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { GlobalProvider } from './GlobalContext.tsx'
import axios from 'axios'

const savedToken = localStorage.getItem('token');
if (savedToken) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
}

// Land on the login screen on a fresh app launch (the shop logs in each time the
// app is opened), but NOT on an in-app refresh. sessionStorage survives a reload
// and is only cleared when the app window is actually closed, so on a refresh the
// flag is already set and the user stays on whatever page they were on.
if (!sessionStorage.getItem('appLaunched')) {
    sessionStorage.setItem('appLaunched', '1');
    window.location.hash = '/login';
}

// Note: no global 401 handler. A 401 no longer force-logs-out or redirects — each
// caller handles its own request errors locally, so one failed call can't tear
// down the session.

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GlobalProvider>
      <App />
    </GlobalProvider>
  </StrictMode>,
)
