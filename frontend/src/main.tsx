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

// axios.interceptors.response.use(
//     response => response,
//     error => {
//         if (error.response?.status === 401) {
//             localStorage.removeItem('token');
//             delete axios.defaults.headers.common['Authorization'];
//             window.location.hash = '/login';
//         }
//         return Promise.reject(error);
//     }
// );

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GlobalProvider>
      <App />
    </GlobalProvider>
  </StrictMode>,
)
