import ReactDom from "react-dom";
import styled from "styled-components";
import { Euro, X, Tag} from 'lucide-react'

export default function ModalReadMoreSale({sale, closeModal, sellItem}) {

    return ReactDom.createPortal(
        <>
            <Overlay/>
            <Wrapper>
                <X size={32} onClick={closeModal}/>
                <InformationWrapper>

                    <div>
                        <Tag size={32}/>
                        Продажба
                    </div>
                    <SaleDetailsWrapper>
                        <span>
                            <p>Шифра на продажба:</p>
                            <p>{sale.Id}</p>
                        </span>
                        <span>
                            <p>Вредност на предметот:</p>
                            <p>{Number(sale["Item Cost"]).toLocaleString("de-DE")}</p>
                        </span>
                        <span>
                            <p>Опис:</p>
                            <p>{sale.About}</p>
                        </span>
                        <span>
                            <p>Купено на:</p>
                            <p>{sale["Date Bought"].substring(0, 10)}</p>
                        </span>
                    </SaleDetailsWrapper>

                </InformationWrapper>
                <ButtonWrapper>
                    <button onClick={() => {
                        sellItem();
                        closeModal();
                    }}><Euro size={32}/> Продади Предмет
                    </button>
                </ButtonWrapper>

            </Wrapper>
        </>,
        document.getElementById("portal")
    )
}


const Overlay = styled.div`
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, .7);
    z-index: 1000;
`

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: #eee;
    z-index: 1000;
    padding: 1rem 2rem 2rem 2rem;
    border-radius: 8px;
    min-width: fit-content;
    max-width: 90%;

    & > svg {
        margin-left: auto;
        transition: all 400ms ease-in-out;
        cursor: pointer;
    }

    & > svg:hover {
        transform: rotate(90deg);
    }
`

const InformationWrapper = styled.div`
    display: grid;
    flex-direction: column;
    gap: 1rem;

    div:first-child {
        display: flex;
        align-items: center;
        gap: .4rem;
        font-size: 1.6rem;
        font-weight: 600;
    }
`

const SaleDetailsWrapper = styled.div`
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: .4rem 2rem;

    span {
        display: flex;
        flex-direction: column;
        gap: 0;
        font-weight: 500;

    }

    span > p:first-child {
        font-weight: 400;
        color: #666;
        margin-left: -.4rem;
    }
    
    span > p {
        min-width: fit-content;
    }
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: 2rem;
    margin-top: 2rem;

    button {
        display: flex;
        align-items: center;
        gap: 1rem;
        border-radius: 4px;
        padding: .5rem 2rem;
        font-size: 1.2rem;
        color: white;
        width: max-content;
        transition: scale 300ms ease-in-out;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        background: var(--green);
    }

    button:hover {
        scale: 1.05;
    }
`