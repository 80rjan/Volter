import axios from "axios";
import { CashRegisterData } from "../types";
import { API_BASE } from "./config";

export const fetchCashRegisterApi = async (): Promise<CashRegisterData> => {
    const res = await axios.get(`${API_BASE}/cash-register`);
    return res.data as CashRegisterData;
};
