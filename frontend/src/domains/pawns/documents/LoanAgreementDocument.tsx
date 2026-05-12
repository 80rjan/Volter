import { forwardRef } from "react";
import { numberInWordsMkd } from "../../../shared/utils/numberInWordsMkd.ts";

interface Props {
    fullName: string; city: string; address: string; embg: string;
    idCard: string; telephone: string; moneyGiven: number; pawnDays: number;
    dateFrom: string; dateTo: string;
}

const sectionStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center", padding: "50px 20px", gap: "1rem" };
const clenStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center" };
const h1Style: React.CSSProperties = { fontSize: "14px" };
const h2Style: React.CSSProperties = { fontSize: "12px" };
const h3Style: React.CSSProperties = { fontSize: "10px", marginBottom: "10px" };
const h4Style: React.CSSProperties = { fontSize: "8px" };
const pStyle: React.CSSProperties = { fontSize: "8px" };
const bold: React.CSSProperties = { fontWeight: "bold" };
const divRowStyle: React.CSSProperties = { width: "100%", display: "flex", justifyContent: "space-between" };

const LoanAgreementDocument = forwardRef<HTMLDivElement, Props>(
    ({ fullName, city, address, embg, idCard, telephone, moneyGiven, pawnDays, dateFrom, dateTo }, ref) => {
        const moneyGivenToString = numberInWordsMkd(moneyGiven);
        const pawnDaysToString = numberInWordsMkd(pawnDays);

        return (
            <div ref={ref} style={sectionStyle}>
                <h1 style={h1Style}>DOGOVOR ZA ZAEM</h1>
                <p style={pStyle}>Sklucen vo Skopje, na den {dateFrom} godina, pomegu slednite dogovorni strani:</p>
                <p style={pStyle}><span style={bold}>1. Drustvo za uslugi VOLTER A&amp;B DOOEL Skopje</span>, so sediste na ul. Bulevar Partizanski odredi br.17-4, Skopje-Centar, so EDB 4080019581460 i EMBS 7349750, zastapuvano od ovlasteno lice Aleksandar Kocevski, Upravitel, (vo ponatamosniot tekst: <span style={bold}>Zaemodavac</span>), i</p>
                <p style={pStyle}><span style={bold}>2. {fullName}</span>, so zivealiste na {address}, grad {city}, so EMBG {embg} i l.k.br. {idCard} izdadena od MVR Skopje, Контакт {telephone}, (vo ponatamosniot tekst: <span style={bold}>Zaemoprimac</span>).</p>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 1</p><p style={pStyle}>Predmet na ovoj Dogovor e ureduvanje na odnosite pomegu dogovornite strani vo vrska so zaem na paricni sredstva koi Zaemodavacot mu gi dava na Zaemoprimacot, a Zaemoprimacot se obvrzuva deka istite ke gi vrati vo rok i na nacin kako sto e dogovoreno so ovoj Dogovor.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 2</p><p style={pStyle}>Zamodavacot mu dava na Zaemoprimacot iznos od {moneyGiven} <span style={bold}>denari</span> (so bukvi: {moneyGivenToString}). Zaemoprimacot gorenavedeniot iznos ke go koristi za sopstveni potrebi so <span style={bold}>dogovorna kamata od 1%</span> (eden procent) na gorenavedeniot iznos.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 3</p><p style={pStyle}>Zaemodavacot go dava, a Zaemoprimacot go prima zaemot opisan od clenot 2 na ovoj Dogovor pod slednite uslovi: - rok na otplata od {pawnDays} <span style={bold}>denovi</span> (so bukvi: {pawnDaysToString} denovi), smetano od den {dateFrom} godina, zaklucno so {dateTo} godina, najdocna do 16.00 casot istiot den; - so sekoja otplata se plakja i dogovorenata kamata od 1% na celokupniot zaem.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 4</p><p style={pStyle}>Zaemodavacot mu go dava zaemot na Zaemoprimacot neposredno pred sklucuvanje na ovoj Dogovor na raka, koe nesto go potvrduva Zaemoprimacot so svojot potpis na ovoj Dogovor.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 5</p><p style={pStyle}>Dokolku, Zaemoprimacot zadocni so plakjanje na zaemot vo rokot opredelen vo clen 3 na ovoj Dogovor, zaemot ke se smeta za dostasan vo celost i Zaemodavacot ke ima pravo da bara isplata na celiot zaem vednas ili prisilna naplata na svoeto pobaruvanje.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 6</p><p style={pStyle}>Zaemoprimacot izreceno se soglasuva i izjavuva, dokolku ne ja ispolni obvrskata i navremeno i celosno ne gi vrati primenite paricni sredstva na ime zaem, <span style={bold}>deka e soglasen Zaemodavacot moze da do naplati svoeto pristignato pobaruvanje so prodazba na zalozeniot predmet</span> predaden vo racen i vladetelski zalog.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 7</p><p style={pStyle}>Vo slucaj na spor, stranite se soglasni za mesno nadlezen da se smeta Osnovniot Gragjanski sud Skopje. <span style={bold}>Prilozite i aneksite se smetaat za sostaven del na ovoj Dogovor.</span></p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 8</p><p style={pStyle}>Soglasno cl.6 od Zakonot za zastita na licnite podatoci, obrabotkata na licnite podatoci na Zaemoprimacot se vrsi samo poradi celite na sklucuvanje na ovoj Dogovor.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 9</p><p style={pStyle}>Dogovornite strani go sklucuvaat ovoj dogovor vo sostojba na zdrav razum i cista svest. Dogovornite strani se soglasni trosocite povrzani so sklucuvanje na ovoj Dogovor da padnat na tovar na Zaemoprimacot.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 10</p><p style={pStyle}>Ovoj dogovor e sostaven od 10 clenovi na 2 lista ispecateni na dvete strani, potpisan od dvete dogovorni strani, i e sklucen vo 2 identicni primeroci, od koi po 1 za sekoja dogovorna strana.</p></div>
                <h2 style={h2Style}>DOGOVORNI STRANI</h2>
                <div style={divRowStyle}>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ZAEMODAVAC</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>Volter A&amp;B DOOEL Skopje</h4>
                        <h4 style={h4Style}>EDB 4080019581460 i EMBS 7349750</h4>
                        <h4 style={h4Style}>Aleksandar Kocevski, Upravitel</h4>
                    </div>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ZAEMOPRIMAC</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>{fullName}</h4>
                    </div>
                </div>
            </div>
        );
    }
);

export default LoanAgreementDocument;
