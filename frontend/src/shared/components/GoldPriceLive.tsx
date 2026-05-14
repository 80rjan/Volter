import { useEffect, useState } from 'react';
import axios from "axios";
import { API_BASE } from "../api/config";

export default function GoldPriceLive() {
    const [pricePerGram, setPricePerGram] = useState<number | null>(null);

    const fetchGoldPrice = async () => {
        try {
            // TODO: No gold price endpoint in Spring Boot backend yet
            const res = await axios.get(`${API_BASE}/gold-price-live`);
            setPricePerGram(res.data.goldPricePerGram);
        } catch {
            // Silently fail until backend endpoint is implemented
        }
    };

    useEffect(() => {
        fetchGoldPrice();
        const interval = setInterval(fetchGoldPrice, 60000 * 5);
        return () => clearInterval(interval);
    }, []);

    const row = (label: string, multiplier: number) => (
        <div className="flex items-center justify-between w-full gap-1 text-base font-bold text-orange-600">
            <span>{label}:</span>
            <div>
                {pricePerGram ? (
                    <div>
                        {(pricePerGram * multiplier).toFixed(2)}€
                        <span className="font-normal text-sm"> / {(pricePerGram * multiplier * 61.5).toFixed(0)} den</span>
                    </div>
                ) : "—"}
            </div>
        </div>
    );

    return (
        <div className="mt-auto w-full">
            {row("24k", 1)}
            {row("18k", 0.75)}
            {row("14k", 0.585)}
        </div>
    );
}
