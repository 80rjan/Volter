import { forwardRef } from "react";
import { numberInWordsMkd } from "../utils/numberInWordsMkd.ts";

interface Props {
    fullName: string; city: string; address: string; embg: string;
    idCard: string; telephone: string; moneyGiven: number; pawnDays: number;
    dateFrom: string; dateTo: string;
}

const sectionStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center", padding: "50px 20px", gap: "1rem" };
const clenStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center" };
const h1Style: React.CSSProperties = { fontSize: "18px" };
const h2Style: React.CSSProperties = { fontSize: "14px" };
const h3Style: React.CSSProperties = { fontSize: "12px", marginBottom: "10px" };
const h4Style: React.CSSProperties = { fontSize: "12px" };
const pStyle: React.CSSProperties = { fontSize: "12px" };
const bold: React.CSSProperties = { fontWeight: "bold" };
const divRowStyle: React.CSSProperties = { width: "100%", display: "flex", justifyContent: "space-between" };

const AneksDogovorZaZaem = forwardRef<HTMLDivElement, Props>(
    ({ fullName, city, address, embg, idCard, telephone, moneyGiven, pawnDays, dateFrom, dateTo }, ref) => {
        const pawnDaysToString = numberInWordsMkd(pawnDays);

        return (
            <div ref={ref} style={sectionStyle}>
                <h1 style={h1Style}>АНЕКС ЗА ДОГОВОР ЗА ЗАЕМ</h1>
                <p style={pStyle}>Склучен во Скопје, на ден {dateFrom} година, помеѓу следните договорни страни:</p>
                <p style={pStyle}><span style={bold}>1. Друштво за услуги ВОЛТЕР А&amp;Б ДООЕЛ Скопје</span>, со седиште на ул. Булевар Партизански одреди бр.17-4, Скопје-Центар, со ЕДБ 4080019581460 и ЕМБС 7349750, застапувано од овластено лице Александар Коцевски, Управител, (во понатамошниот текст: <span style={bold}>Заемодавач</span>), и</p>
                <p style={pStyle}><span style={bold}>2. {fullName}</span>, со живеалиште на {address}, град {city}, со ЕМБГ {embg} и л.к.бр. {idCard} издадена од МВР Скопје, Контакт {telephone}, (во понатамошниот текст: <span style={bold}>Заемопримач</span>).</p>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Член 1</p><p style={pStyle}>Овој Анекс се склучува врз основа на чл.6 од основниот Договор за заем цитиран погоре и претставува негов составен дел.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Член 2</p><p style={pStyle}>Се менува чл.3 ст.1 т.1 од Договорот за заем цитиран погоре, па гласи: - рок на отплата се продолжува на дополнителни {pawnDays} <span style={bold}>денови</span> (со букви: {pawnDaysToString} денови), сметано од ден {dateFrom} година, заклучно со {dateTo} година, најдоцна до 16.00 часот истиот ден.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Член 3</p><p style={pStyle}>Сето останато во погоре цитираниот Договор за заем останува неизменето, односно истиот продолжува да важи со сите услови под кои што е склучен и договорен од двете страни.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Член 4</p><p style={pStyle}>Согласноста која ја има дадено Закупопримачот за обработката на личните податоци согласно чл.6 од Законот за заштита на личните податоци, се однесува и на овој Анекс.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Член 5</p><p style={pStyle}>Овој Анекс е составен од 5 членови на 1 лист и е склучен во 2 идентични примероци, од кои по 1 за секоја договорна страна.</p></div>
                <h2 style={h2Style}>ДОГОВОРНИ СТРАНИ</h2>
                <div style={divRowStyle}>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ЗАЕМОДАВАЧ</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>Волтер А&amp;В ДООЕЛ Скопје</h4>
                        <h4 style={h4Style}>ЕДБ 4080019581460 и ЕМБС 7349750</h4>
                        <h4 style={h4Style}>Aлександар Коцевски, Управител</h4>
                    </div>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ЗАЕМОПРИМАЧ</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>{fullName}</h4>
                    </div>
                </div>
            </div>
        );
    }
);

export default AneksDogovorZaZaem;
