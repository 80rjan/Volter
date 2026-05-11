import { forwardRef } from "react";

interface Props {
    fullName: string; city: string; address: string; embg: string;
    idCard: string; telephone: string; moneyGiven: number; pawnDays: number;
    dateFrom: string; dateTo: string; pawnInfo: string;
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

const PawnAgreementDocument = forwardRef<HTMLDivElement, Props>(
    ({ fullName, city, address, embg, idCard, telephone, moneyGiven, dateFrom, dateTo, pawnInfo }, ref) => {
        return (
            <div ref={ref} style={sectionStyle}>
                <p style={pStyle}>Vrz osnov na clen 2, 3, 4, 14, 18, 23, 25, 29, 30, 32, 33 i 41 od Zakonot za dogovoren zalog (Sl.vesnik na RSM br.05/03, so site negovi izmeni i dopolnuvanja), na den {dateFrom} godina, vo Skopje, se sklucuva sledniot:</p>
                <h1 style={h1Style}>DOGOVOR ZA RACEN ZALOG</h1>
                <p style={pStyle}>Pomegu slednite dogovorni strani:</p>
                <p style={pStyle}><span style={bold}>1. Drustvo za uslugi VOLTER A&amp;B DOOEL Skopje</span>, so sediste na ul. Bulevar Partizanski odredi br.17-4, Skopje-Centar, so EDB 4080019581460 i EMBS 7349750, zastapuvano od ovlasteno lice Aleksandar Kocevski, Upravitel, (vo ponatamosniot tekst: <span style={bold}>Zalozen doveritel</span>), i</p>
                <p style={pStyle}><span style={bold}>2. {fullName}</span>, so zivealiste na {address}, grad {city}, so EMBG {embg} i l.k.br. {idCard} izdadena od MVR Skopje, Контакт {telephone}, (vo ponatamosniot tekst: <span style={bold}>Zalozen dolznik</span>).</p>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 1</p><p style={pStyle}>Predmet na ovoj Dogovor za racen zalog e zasnovanje na dogovorno zalozno pravo vrz podvizna stvar - <span style={bold}>racen zalog</span>, so koj se obezbeduva paricno pobaruvanje na Zalozniot doveritel vo iznos od {moneyGiven} denari koe go ima vrz Zalozniot dolznik vrz osnova na Dogovor za zaem od {dateFrom} godina, so rok na pristignatost na pobaruvanjeto na den {dateTo} godina.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 2</p><p style={pStyle}>Predmet na zalog e: {pawnInfo}, so proceneta vrednost vo paricen iznos od {moneyGiven} denari.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 3</p><p style={pStyle}>Dokolku pobaruvanjeto koe e obezbedeno so zalogot - predmet na ovoj Dogovor ne bide ispolneto vo rokot na negovata pristignatost naznacen vo Dogovorot za zaem, Zalozniot doveritel moze da go namiri svoeto pobaruvanje od vrednosta na predmetot na zalogot ili da se stekne so pravo na sopstvenost vrz predmetot na zalogot <span style={bold}>(lex commissoria)</span>.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 4</p><p style={pStyle}>Zalozniot dolznik pod polna moralna, materijalna i krivicna odgovornost so potpisuvanje na ovoj Dogovor i so posebna izjava potvrduva i garantira deka e edinstven i nesporen sopstvenik na predmetot na zalog. <span style={bold}>(Prilog br.1)</span></p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 5</p><p style={pStyle}>Stranite se soglasni deka Zalozniot doveritel vo slucaj na docnenje od strana na Zalozniot dolznik da moze da prevzema zastitni merki vo odnos na zalogot. Zalozniot doveritel isto taka ima pravo na: - <span style={bold}>mesecen nadomest za cuvanje</span> vo visina od % od vrednosta na zalozeniot predmet; - <span style={bold}>trosoci za procenka na vrednosta na zalozeniot predmet</span> vo visina od % od vrednosta; - <span style={bold}>trosoci za razgleduvanje i odobruvanje na zaemot</span> vo visina od % od vrednosta na zaemot.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 6</p><p style={pStyle}>Dogovornite strani go utvrduvaat maksimalniot iznos na pobaruvanjeto koe se obezbeduva so predmetot na zalog vo visina od procenetata vrednost na zalozeniot predmet opredelena vo cl.2 st.1 na ovoj Dogovor.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 7</p><p style={pStyle}>Zalozniot doveritel e dolzen da go cuva zalozeniot predmet so vnimanie na dobar domakin. Zalozniot doveritel e dolzen da go vrati zalozeniot predmet stom ke mu bide namireno pobaruvanjeto vo celost, za sto izdava Potvrda za vrakanje na zalozen predmet <span style={bold}>(Prilog br.3)</span>.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 8</p><p style={pStyle}>Dokolku se pokaze deka predmetot na zalog ima nekoj praven ili materijalen nedostatok, Zalozniot dolznik e dolzen vednas a najdocna vo rok od 7 dena da go oslobodi predmetot na zalog od toj nedostatok. Zalozniot dolznik e dolzen na Zalozniot doveritel <span style={bold}>da mu ja najavi namerata za podignuvanje na zalozeniot predmet najmalku eden den odnapred</span>, pri celosna isplata na pristignatoto pobaruvanje.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 9</p><p style={pStyle}>Zaloznoto pravo zasnovano so ovoj dogovor prestanuva poradi: ispolnuvanje na obvrskata od strana na Zalozniot dolznik; istek na opredelenoto vreme; propaganje na zalozeniot predmet poradi visa sila; gubenje na vladenieto na zalozeniot predmet; prodazba na predmetot zaradi realizacija na zaloznoto pravo; raskinuvanje na Dogovorot za zalog.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 10</p><p style={pStyle}>Soglasno odredbite od Zakonot za dogovoren zalog, za zaloznoto pravo zasnovano vrz osnova na ovoj Dogovor, kako pravo na racen i vladetelski zalog, <span style={bold}>ne se vrsi upis na zalogot vo Zalozniot registar</span> koj go vodi Cetralniot registar na RSM.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 11</p><p style={pStyle}>Soglasno cl.6 od Zakonot za zastita na licnite podatoci, obrabotkata na licnite podatoci na Zalozniot dolznik se vrsi samo poradi celite na sklucuvanje na ovoj Dogovor.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 12</p><p style={pStyle}>Vo slucaj na spor, stranite se soglasni za mesno nadlezen da se smeta Osnoven Graganski sud Skopje. <span style={bold}>Prilozite i aneksite se smetaat za sostaven del na ovoj Dogovor.</span></p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 13</p><p style={pStyle}>Dogovornite strani go sklucuvaat ovoj dogovor vo sostojba na zdrav razum i cista svest, so slobodno izrazena volja. Dogovornite strani se soglasni eventualnite trosocite povrzani so sklucuvanje na ovoj Dogovor da padnat na tovar na Zalozniot dolznik.</p></div>
                <div style={clenStyle}><p style={{ ...pStyle, ...bold }}>Clen 14</p><p style={pStyle}>Ovoj dogovor e sostaven od 14 clenovi na 2 lista ispecateni na dvete strani, potpisan na sekoj list od dvete dogovorni strani, i e sklucen vo 2 identicni primeroci, od koi po 1 za sekoja dogovorna strana.</p></div>
                <h2 style={h2Style}>DOGOVORNI STRANI</h2>
                <div style={divRowStyle}>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ZALOZEN DOVERITEL</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>Volter A&amp;B DOOEL Skopje</h4>
                        <h4 style={h4Style}>EDB 4080019581460 i EMBS 7349750</h4>
                        <h4 style={h4Style}>Aleksandar Kocevski, Upravitel</h4>
                    </div>
                    <div style={clenStyle}>
                        <h3 style={h3Style}>ZALOZEN DOLZNIK</h3>
                        <p style={pStyle}>______________________</p>
                        <h4 style={h4Style}>{fullName}</h4>
                    </div>
                </div>
            </div>
        );
    }
);

export default PawnAgreementDocument;
