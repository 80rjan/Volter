import styled from "styled-components";

import { useEffect, useState } from 'react';
import axios from "axios";
import {Coins} from "lucide-react";

export default function GoldPriceLive() {
    const [pricePerGram, setPricePerGram] = useState(null);

    const fetchGoldPrice = async () => {
        try {
            const res = await axios.get("http://localhost:3000/goldPriceLive");
            setPricePerGram(res.data.goldPricePerGram);
        } catch (err) {
            console.error("Error fetching gold price:", err);
        }
    };

    useEffect(() => {
        fetchGoldPrice();

        const interval = setInterval(fetchGoldPrice, 60000 * 5); // refresh every 5 minutes
        return () => clearInterval(interval);
    }, []);

    return (
        <Wrapper>
            <GoldPrice>
                <Coins size={24} />
                {pricePerGram ? pricePerGram + "€" : "Loading..."}
            </GoldPrice>
        </Wrapper>
    );
}

const Wrapper = styled.div`
    margin-top: auto;
`

const GoldPrice = styled.div`
    display: flex;
    align-items: center;
    gap: .4rem;
    font-size: 1.2rem;
    font-weight: 600;
    color: darkgoldenrod;
`
