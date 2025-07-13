import styled from "styled-components";
import {forwardRef} from "react";
import {numberInWordsMkd} from "../Utils/numberInWordsMkd.js";


const LoanAgreementDocument = forwardRef((
    { fullName, city, address, embg, idCard, telephone, moneyGiven, pawnDays, dateFrom, dateTo },
    ref) => {

    const moneyGivenToString = numberInWordsMkd(moneyGiven)
    const pawnDaysToString = numberInWordsMkd(pawnDays)

    return (
        <Section ref={ref}>
            <h1>DOGOVOR ZA ZAEM</h1>
            <p>Sklucen vo Skopje, na den {dateFrom} godina, pomegu slednite dogovorni strani:</p>
            <p><span className="bold">1. Drustvo za uslugi VOLTER A&B DOOEL Skopje</span>, so sediste na ul. Bulevar Partizanski odredi br.17-4, Skopje-
                Centar, so EDB 4080019581460 i EMBS 7349750, zastapuvano od ovlasteno lice Aleksandar Kocevski, Upravitel,
                (vo ponatamosniot tekst: <span className="bold">Zaemodavac</span>), i</p>
            <p><span className="bold">2. {fullName}</span>, so zivealiste na {address}, grad {city}, so EMBG {embg} i l.k.br. {idCard} izdadena od MVR Skopje, Контакт {telephone},
                 (vo ponatamosniot tekst: <span className="bold">Zaemoprimac</span>).</p>
            <Clen>
                <p className="bold">Clen 1</p>
                <p>Predmet na ovoj Dogovor e ureduvanje na odnosite pomegu dogovornite strani vo vrska so zaem na paricni
                    sredstva koi Zaemodavacot mu gi dava na Zaemoprimacot, a Zaemoprimacot se obvrzuva deka istite ke gi vrati vo
                    rok i na nacin kako sto e dogovoreno so ovoj Dogovor.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 2</p>
                <p>Zamodavacot mu dava na Zaemoprimacot iznos od {moneyGiven} <span className="bold">denari</span> (so bukvi: {moneyGivenToString}).
                    Zaemoprimacot gorenavedeniot iznos ke go koristi za sopstveni potrebi so <span className="bold">dogovorna kamata od 1%</span>
                    (eden procent) na gorenavedeniot iznos.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 3</p>
                <p>Zaemodavacot go dava, a Zaemoprimacot go prima zaemot opisan od clenot 2 na ovoj Dogovor pod
                    slednite uslovi:
                    - rok na otplata od {pawnDays} <span className="bold">denovi</span> (so bukvi: {pawnDaysToString} denovi), smetano od den {dateFrom} godina, zaklucno
                    so {dateTo} godina, najdocna do 16.00 casot istiot den;
                    - so sekoja otplata se plakja i dogovorenata kamata od 1% na celokupniot zaem.
                    Za sekoj den na zadocnuvanje, na site dostasani, a nenaplateni pobaruvanja po osnov na zaemot od clen 2
                    na ovoj dogovor, Zaemodavacot je presmetuva i naplatuva zakonska kaznena kamata soglasno cl.266-a od
                    Zakonot za obligacionite odnosi, od denot na dostasuvanje do denot na naplatata.</p>
            </Clen>
            {/*PROVERI TUKA*/}
            <Clen>
                <p className="bold">Clen 4</p>
                <p>Zaemodavacot mu go dava zaemot na Zaemoprimacot neposredno pred sklucuvanje na ovoj Dogovor na
                    raka, koe nesto go potvrduva Zaemoprimacot so svojot potpis na ovoj Dogovor.
                    Kako obezbeduvanje na pobaruvanjeto na Zaemodavacot, odnosno garancija za navremena i celosna
                    isplata na dolgot po osnov na zaemot predmet na ovoj Dogovor, Zaemoprimacot vednas po sklucuvanjeto na ovoj
                    Za navedenoto vo stav 2 na ovoj clen pomegu istite strani se sklucuva i dogovor za racen vladetelski
                    Dogovor mu predava na raka na Zaemodavacot svoj podvizen predmet vo racen i vladetelski zalog.
                    Za navedenoto vo stav 2 na ovoj clen pomegju istite strani se sklucuva i dogovor za racen vladetelski
                    zalog na podvizen imot.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 5</p>
                <p>Dokolku, Zaemoprimacot zadocni so plakjanje na zaemot vo rokot opredelen vo clen 3 na ovoj Dogovor,
                    zaemot ke se smeta za dostasan vo celost i Zaemodavacot ke ima pravo da bara isplata na celiot zaem vednas ili
                    prisilna naplata na svoeto pobaruvanje.
                    Vo slucaj na delumno vraten zaem, predmet na pobaruvanje ke pretstavuva razlikata od isplateniot del do
                    vkupnata obvrska, zaedno so site dogovorni i kazneni kamati, kako i trosocite koi ke gi ima Zaemodavacot vo i
                    povrzani so postapkata za prisilna naplata.</p>
            </Clen>
            {/*DVA PATI IMA CLEN 5*/}
            <Clen>
                <p className="bold">Clen 5</p>
                <p>Eventualnite promeni na rokovite na otplata ili celosno vrakanje na zaemot ili osloboduvanje od dogovornite
                    obvrski pred celosna isplata na zemot ili drugi elementi na ovoj dogovor, po soglasnost na dvete strani ke se
                    reguliraat so <span className="bold">Aneks</span> na ovoj dogovor potpisan od dvete strani, koj ke bide sostaven del na istiot.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 6</p>
                <p>Zaemoprimacot izreceno se soglasuva i izjavuva, dokolku ne ja ispolni obvrskata i navremeno i celosno
                    ne gi vrati primenite paricni sredstva na ime zaem, na nacin kako sto e regulirano vo clen 3 i 5 na ovoj Dogovor,
                    <span className="bold">deka e soglasen Zaemodavacot moze da do naplati svoeto pristignato pobaruvanje so prodazba na
                    zalozeniot predmet</span> predaden vo racen i vladetelski zalog vrz osnova na Dogovor za racen zalog ili
                    preminuvanje na sopstvenosta vrz predmetot na Zaemodavacot od momentot na paganjeto vo docnenje.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 7</p>
                <p>Vo slucaj na spor, stranite se soglasni za mesno nadlezen da se smeta Osnovniot Gragjanski sud Skopje.
                    Za se sto ne e predvideno so ovoj dogovor, ke se primenuva Zakonot za obligacionite odnosi i drugite
                    pozitivni propisi vo RSM.
                    <span className="bold">Prilozite i aneksite se smetaat za sostaven del na ovoj Dogovor.</span> </p>
            </Clen>
            <Clen>
                <p className="bold">Clen 8</p>
                <p>Soglasno cl.6 od Zakonot za zastita na licnite podatoci, obrabotkata na licnite podatoci na Zaemoprimacot
                    se vrsi samo poradi celite na sklucuvanje na ovoj Dogovor vo koj istiot se javuva kako dogovorna strana i negova
                    izrecna i prethodno dobiena soglasnost koja ja potvrduva so stavanje na svojot potpis vo ovoj Dogovor.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 9</p>
                <p>Dogovornite strani go sklucuvaat ovoj dogovor vo sostojba na zdrav razum i cista svest, so slobodno
                    izrazena volja, dobro se zapoznaeni so negovata sodrzina i znacenjee, go priznavaat za tocen izraz na svojata
                    nepokolebliva volja i za seto pogore navedeno svedocat so svoeracni potpisi.
                    Dogovornite strani se soglasni trosocite povrzani so sklucuvanje na ovoj Dogovor da padnat na tovar na
                    Zaemoprimacot.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 10</p>
                <p>Ovoj dogovor e sostaven od 10 clenovi na 2 lista ispecateni na dvete strani, potpisan od dvete dogovorni
                    strani, i e sklucen vo 2 identicni primeroci, od koi po 1 za sekoja dogovorna strana.</p>
            </Clen>
            <h2>DOGOVORNI STRANI</h2>
            <div >
                <Clen>
                    <h3>ZAEMODAVAC</h3>
                    <p>______________________</p>
                    <h4>Volter A&B DOOEL Skopje</h4>
                    <h4>EDB 4080019581460 i EMBS 7349750</h4>
                    <h4>Aleksandar Kocevski, Upravitel</h4>
                </Clen>
                <Clen>
                    <h3>ZAEMOPRIMAC</h3>
                    <p>______________________</p>
                    <h4>{fullName}</h4>
                </Clen>
            </div>
        </Section>
    )
})

export default LoanAgreementDocument;

const Section = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 50px 20px;
    gap: 1rem;
    
    & h1 {
        font-size: 14px;
    }
    h2 {
        font-size: 12px;
    }
    & h3 {
        font-size: 10px;
        margin-bottom: 10px;
    }
    & h4 {
        font-size: 8px;
    }
    & p {
        font-size: 8px;
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