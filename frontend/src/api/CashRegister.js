import axios from "axios";

export const fetchCashRegisterApi = async () => {
    if (window.electronAPI) {
        const res = await window.electronAPI.invoke('get-cash-register');
        return res;
    } else {
        const res = await axios.get(`http://localhost:3000/cashRegister`)
        return res.data.cashReg;
    }
}