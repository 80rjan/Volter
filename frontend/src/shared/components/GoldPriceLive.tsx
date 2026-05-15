import { useEffect, useState } from 'react';
import axios from "axios";
import { API_BASE } from "../api/config";

interface GoldPriceResponse {
    currency: string;
    price_gram_24k: number;
    price_gram_22k: number;
    price_gram_21k: number;
    price_gram_20k: number;
    price_gram_18k: number;
    price_gram_16k: number;
    price_gram_14k: number;
    price_gram_10k: number;
}

export default function GoldPriceLive() {
    const [prices, setPrices] = useState<GoldPriceResponse | null>(null);
    const [error, setError] = useState(false);

    const fetchGoldPrice = async () => {
        try {
            const res = await axios.get<GoldPriceResponse>(`${API_BASE}/gold/price`);
            setPrices(res.data);
            setError(false);
        } catch {
            setError(true);
        }
    };

    useEffect(() => {
        fetchGoldPrice();
        const interval = setInterval(fetchGoldPrice, 60000 * 5);
        return () => clearInterval(interval);
    }, []);

    const row = (label: string, price: number | undefined) => (
        <div className="flex items-center justify-between w-full gap-1 text-base font-bold text-orange-600">
            <span>{label}:</span>
            <div>
                {price != null ? (
                    <div>
                        {price.toFixed(2)}€
                        <span className="font-normal text-sm"> / {(price * 61.5).toFixed(0)} ден</span>
                    </div>
                ) : "—"}
            </div>
        </div>
    );

    if (error) return (
        <div className="mt-auto w-full text-center text-sm text-red-500">
            Грешка при вчитување на цената на злато
        </div>
    );

    return (
        <div className="mt-auto w-full">
            {row("24k", prices?.price_gram_24k)}
            {row("18k", prices?.price_gram_18k)}
            {row("14k", prices?.price_gram_14k)}
        </div>
    );
}
