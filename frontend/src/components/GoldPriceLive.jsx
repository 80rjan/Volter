import styled from "styled-components";

import {useEffect, useState} from 'react';
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
                {/*<Coins size={24} />*/}
                <span>24k:</span>
                <div>
                    {
                        pricePerGram ? (
                            <div>
                                {Number(pricePerGram).toFixed(2)}€
                                <span> / {(pricePerGram * 61.5).toFixed(0)} den</span>
                            </div>
                        ) : "Loading..."
                    }
                </div>
            </GoldPrice>
            <GoldPrice>
                {/*<Coins size={24} />*/}
                <span>18k:</span>
                {
                    pricePerGram ? (
                        <div>
                            {(pricePerGram * 0.75).toFixed(2)}€
                            <span> / {(pricePerGram * 0.75 * 61.5).toFixed(0)} den</span>
                        </div>
                    ) : "Loading..."
                }
            </GoldPrice>
            <GoldPrice>
                {/*<Coins size={24} />*/}
                <span>14k:</span>
                {
                    pricePerGram ? (
                        <div>
                            {(pricePerGram * 0.585).toFixed(2)}€
                            <span> / {(pricePerGram * 0.585 * 61.5).toFixed(0)} den</span>
                        </div>
                    ) : "Loading..."
                }
            </GoldPrice>
        </Wrapper>
    );
}

const Wrapper = styled.div`
    margin-top: auto;
    width: 100%;
`

const GoldPrice = styled.div`
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    gap: .4rem;
    font-size: 1rem;
    font-weight: 700;
    color: orangered;
`
