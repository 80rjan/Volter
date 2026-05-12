import axios from "axios";
import { CashRegisterData } from "../types";

export const fetchCashRegisterApi = async (): Promise<CashRegisterData> => {
    if (window.electronAPI) {
        const res = await window.electronAPI.invoke('get-cash-register');
        return res as CashRegisterData;
    } else {
        const res = await axios.get(`http://localhost:3000/cashRegister`);
        return res.data.cashReg as CashRegisterData;
    }
};
