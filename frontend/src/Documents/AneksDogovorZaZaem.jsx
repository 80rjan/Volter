import styled from "styled-components";
import {forwardRef} from "react";
import {numberInWordsMkd} from "../Utils/numberInWordsMkd.js";


const AneksDogovorZaZaem = forwardRef((
    { fullName, city, address, embg, idCard, telephone, moneyGiven, pawnDays, dateFrom, dateTo },
    ref) => {

    const pawnDaysToString = numberInWordsMkd(pawnDays)

    return (
        <Section ref={ref}>
            <h1>АНЕКС ЗА ДОГОВОР ЗА ЗАЕМ</h1>
            <p>Склучен во Скопје, на ден {dateFrom} година, помеѓу следните договорни страни:</p>
            <p><span className="bold">1. Друштво за услуги ВОЛТЕР А&Б ДООЕЛ Скопје</span>, со седиште на ул. Булевар
                Партизански одреди бр.17-4, Скопје-Центар, со ЕДБ 4080019581460 и ЕМБС 7349750, застапувано од овластено лице Александар Коцевски, Управител,
                (во понатамошниот текст: <span className="bold">Заемодавач</span>), и</p>
            <p><span className="bold">2. {fullName}</span>, со живеалиште на {address}, град {city}, со ЕМБГ {embg} и л.к.бр. {idCard} издадена од МВР Скопје, Контакт {telephone},
                (во понатамошниот текст: <span className="bold">Заемопримач</span>).</p>
            <Clen>
                <p className="bold">Член 1</p>
                <p>Овој Анекс се склучува врз основа на чл.6 од основниот Договор за заем цитиран погоре и претставува негов составен дел.</p>
            </Clen>
            <Clen>
                <p className="bold">Член 2</p>
                <p>Се менува чл.3 ст.1 т.1 од Договорот за заем цитиран погоре, па гласи: - рок на отплата се продолжува
                    на дополнителни {pawnDays} <span className="bold">денови</span> (со букви: {pawnDaysToString} денови),
                    сметано од ден {dateFrom} година, заклучно со {dateTo} година, најдоцна до 16.00 часот истиот
                    ден.
                </p>
            </Clen>
            <Clen>
                <p className="bold">Член 3</p>
                <p>Сето останато во погоре цитираниот Договор за заем останува неизменето, односно истиот продолжува да важи со сите услови под кои што е склучен и договорен од двете страни.</p>
            </Clen>
            <Clen>
                <p className="bold">Член 4</p>
                <p>Согласноста која ја има дадено Закупопримачот за обработката на личните податоци согласно чл.6 од Законот за заштита на личните податоци, се однесува и на овој Анекс.</p>
            </Clen>
            <Clen>
                <p className="bold">Член 5</p>
                <p>Овој Анекс е составен од 5 членови на 1 лист и е склучен во 2 идентични примероци, од кои по 1 за секоја договорна страна.</p>
            </Clen>
            <h2>ДОГОВОРНИ СТРАНИ</h2>
            <div>
                <Clen>
                    <h3>ЗАЕМОДАВАЧ</h3>
                    <p>______________________</p>
                    <h4>Волтер А&В ДООЕЛ Скопје</h4>
                    <h4>ЕДБ 4080019581460 и ЕМБС 7349750</h4>
                    <h4>Aлександар Коцевски, Управител</h4>
                </Clen>
                <Clen>
                    <h3>ЗАЕМОПРИМАЧ</h3>
                    <p>______________________</p>
                    <h4>{fullName}</h4>
                </Clen>
            </div>

        </Section>
    )
})

export default AneksDogovorZaZaem;

const Section = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 50px 20px;
    gap: 1rem;

    & h1 {
        font-size: 18px;
    }
    h2 {
        font-size: 14px;
    }
    & h3 {
        font-size: 12px;
        margin-bottom: 10px;
    }
    & h4 {
        font-size: 12px;
    }
    & p {
        font-size: 12px;
    }

    & > div {
        width: 100%;
        display: flex;
        justify-content: space-between;
    }

    & .bold {
        font-weight: bold;
    }
`

const Clen = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
`