import styled from "styled-components";
import {forwardRef} from "react";


const PawnAgreementDocument = forwardRef((
    { fullName, city, address, embg, idCard, telephone, moneyGiven, moneyGivenStr, pawnDays, pawnDaysStr, dateFrom, dateTo, pawnInfo },
    ref) => {
    return (
        <Section ref={ref}>
            <p>Vrz osnov na clen 2, 3, 4, 14, 18, 23, 25, 29, 30, 32, 33 i 41 od Zakonot za dogovoren zalog (Sl.vesnik na
                RSM br.05/03, so site negovi izmeni i dopolnuvanja), na den {dateFrom} godina, vo Skopje, se sklucuva
                sledniot:</p>
            <h1>DOGOVOR ZA RACEN ZALOG</h1>
            <p>Pomegu slednite dogovorni strani:</p>
            <p><span className="bold">1. Drustvo za uslugi VOLTER A&B DOOEL Skopje</span>, so sediste na ul. Bulevar
                Partizanski odredi br.17-4, Skopje-
                Centar, so EDB 4080019581460 i EMBS 7349750, zastapuvano od ovlasteno lice Aleksandar Kocevski,
                Upravitel,
                (vo ponatamosniot tekst: <span className="bold">Zalozen doveritel</span>), i</p>
            <p><span className="bold">2. {fullName}</span>, so zivealiste na {address}, grad {city}, so EMBG {embg} i
                l.k.br. {idCard} izdadena od MVR Skopje, Контакт {telephone},
                (vo ponatamosniot tekst: <span className="bold">Zalozen dolznik</span>).</p>
            <Clen>
                <p className="bold">Clen 1</p>
                <p>Predmet na ovoj Dogovor za racen zalog (vo ponatamosniot tekst: Dogovorot) e zasnovanje na dogovorno
                    zalozno pravo vrz podvizna stvar - <span className="bold">racen zalog</span>, so koj se obezbeduva paricno pobaruvanje na Zalozniot doveritel
                    {/*DOGOVOR ZA ZAEM BROJ???*/}
                    vo iznos od {moneyGiven} denari koe go ima vrz Zalozniot dolznik vrz osnova na Dogovor za zaem br.__________
                    {/*DATEFROM DVA PATI, KOJ DATUM ZA STO E*/}
                    od {dateFrom} godina (vo ponatamosniot tekst: Dogovor za zaem), so rok na pristignatost na pobaruvanjeto na
                    den {dateFrom} godina.
                    Racniot zalog se zasnova so predavanje na predmetot na ovoj Dogovor vo vladenie na Zalozniot doveritel
                    (vladetelski zalog).
                    Zalozniot doveritel se steknuva so pravoto na zalog vo momentot na sklucuvanje na ovoj Dogovor i so
                    istovremeno predavanje na predmetot na zalog vo vladenie na Zalozniot doveritel.
                    So zalog se optovaruva celiot predmet na zalogot.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 2</p>
                <p>Predmet na zalog e:
                    {pawnInfo}, so proceneta
                    vrednost vo paricen iznos od {moneyGiven} denari.
                    Dogovornite strani se soglasni deka zalogot opisan vo stav 1 na ovoj clen e detalno i precizno identifikuvan
                    so dovolno podatoci koi nesporno ja dokazuvaat negovata posebnost i odredenost, i soglasni se deka procenetata
                    vrednost na zalozniot predmet e vzaemno opredelena i pretstavuva dogovorena vrednost na predmetot na zalog vo
                    momentot na sklucuvanje na ovoj Dogovor.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 3</p>
                <p>Dokolku pobaruvanjeto koe e obezbedeno so zalogot - predmet na ovoj Dogovor ne bide ispolneto vo rokot
                    na negovata pristignatost naznacen vo Dogovorot za zaem, Zalozniot doveritel moze da go namiri svoeto
                    pobaruvanje od vrednosta na predmetot na zalogot ili da se stekne so pravo na sopstvenost vrz predmetot na
                    zalogot <span className="bold">(lex commissoria)</span>.
                    Zalozniot dolznik e izrecno soglasen vo slucaj da ne go podmiri vo celost pobaruvanjeto koe e obezbedeno
                    so zalogot - predmet na ovoj Dogovor vo rokot na negovata pristignatost naznacen vo Dogovorot za zaem, <span className="bold">da
                    moze Zaloznikot doveritel da go prodade predmetot na zalog</span> po sopstveno naoganje so neposredna spogodba
                    so treto lice ili <span className="bold">da se stekne so pravo na sopstvenost vrz predmetot na zalog</span> i da raspolaga so istiot kako
                    edinstven i nesporen sopstvenik so site prava koi mu sleduvaat soglasno pozitivnite zakonski propisi.
                    Dokolku postoi pozitivna razlika pomegu iznosot na vrednosta na zalozeniot predmet navedena vo clen 2 na
                    ovoj Dogovor i postignatata kupoprodazna cena vo dogovorot za kupoprodazba na podvizen imot so koj Zalozniot
                    doveritel ke go prodade zalozeniot predmet na treto lice, istata ke uplati na smetkata na Zalozniot doveritel kako
                    priliv na dneven promet i istata e podlozna na danok na dobivka.</p>
            </Clen>
            {/*PROVERI TUKA*/}
            <Clen>
                <p className="bold">Clen 4</p>
                <p>Zalozniot dolznik pod polna moralna, materijalna i krivicna odgovornost so potpisuvanje na ovoj Dogovor i so
                    posebna izjava potvrduva i garantira deka e edinstven i nesporen sopstvenik na predmetot na zalog i deka istiot
                    nema nikakvi zakonski precki da bide vo pravniot promet. <span className="bold">(Prilog br.1)</span>
                    Zalozniot dolznik pod istata celosna odgovornost navedena vo stav 1 na ovoj clen potvrduva i garantira
                    deka predmetot na zalog go steknal vo svoja sopstvenost po legalen pat, a vo sprotivno ja isklucuva odgovornosta
                    na Zalozniot doveritel da snosi zakonski posledici dokolku predmetot na zalog prethodno go ima steknato na
                    protivpraven nacin. (<span className="bold">Prilog br.2</span>, dokolku Zalozniot dolznik poseduva dokaz za steknuvanje na sopstvenosta)
                    Vo slucaj na prekrsuvanje na odredbite od stav 1 i stav 2 na ovoj clen, se smeta deka ovoj Dogovor ne
                    proizveduva pravno dejstvo i Zalozniot doveritel ima pravo da go naplati svoeto pobaruvanje so drug podvizen i
                    nedvizen imot vo sopstvenost na Zalozniot dolznik.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 5</p>
                <p>Stranite se soglasni deka Zalozniot doveritel vo slucaj na docnenje od strana na Zalozniot dolznik da moze
                    da prevzema zastitni merki vo odnos na zalogot, so cel da se zacuva, odrzi ili zgolemi pazarnata vrednost na
                    predmetot na zalog, i istiot ima pravo na nadomest koj dopolnitelno bi se utvrdil vrz osnova na realnite trosoci koi ke
                    gi ima Zalozniot doveritel za cuvanje na predmetot na zalog vo sopstveniot objekt koj e sigurnosno obezbeden i
                    zastiten od agencija za obezbeduvanje ili vo sef, vo zavisnost od gabaritnosta na predmetot i po procenka na
                    dogovornite strani.
                    Zalozniot doveritel isto taka ima pravo na:
                    - <span className="bold">mesecen nadomest za cuvanje</span> vo visina od % od vrednosta na zalozeniot predmet;
                    - <span className="bold">trosoci za procenka na vrednosta na zalozeniot predmet</span> vo visina od od % od vrednosta na zalozeniot predmet;
                    - <span className="bold">trosoci za razgleduvanje i odobruvanje na zaemot</span> vo visina od % od vrednosta na zaemot.
                        Zalozniot dolznik izrecno se soglasuva i gi prifaka gorenavedenite nadomestoci i trosoci kako fer i korekten
                        nacin za nadomest na realnite trosocite koi ke gi ima Zalozniot doveritel.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 6</p>
                <p>Dogovornite strani go utvrduvaat maksimalniot iznos na pobaruvanjeto koe se obezbeduva so predmetot na
                    zalog vo visina od procenetata vrednost na zalozeniot predmet opredelena vo cl.2 st.1 na ovoj Dogovor, koj iznos
                    pretstavuva zbir od glavnite pobaruvanja zgolemeni za iznosot na dogovornata kamata, kaznenata kamata,
                    proviziite i drugite trosoci koi proizleguvaat po osnov na Dogovorot za zaem.
                    Delot na pobaruvanjeto na Zalozniot doveritel vo iznos pogolem od dogovoreniot maksimalen iznos
                    naznacen vo stav 1 na ovoj clen, ke se smeta kako neobezbedeno pobaruvanje.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 7</p>
                <p>Zalozniot doveritel e dolzen da go cuva zalozeniot predmet so vnimanie na dobar domakin.
                    Zalozniot doveritel e dolzen da go vrati zalozeniot predmet stom ke mu bide namireno pobaruvanjeto vo
                    celost, za sto izdava Potvrda za vrakanje na zalozen predmet <span className="bold">(Prilog br.3)</span>.
                    Zalozniot doveritel nema pravo da go upotrebuva zalozeniot predmet ili da mu go predade na upotreba na
                    drug, ili da go stavi vo podzalog, osven so soglasnost na Zalozniot dolznik.
                    Zalozniot doveritel ne odgovara za celosno ili delumno unistuvanje na zalozeniot predmet poradi visa sila
                    (poplava, pozar, zemjotres) ili rezultat na dejstvo na topla, voena ili vonredna sostojba.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 8</p>
                <p>Dokolku se pokaze deka predmetot na zalog ima nekoj praven ili materijalen nedostatok, Zalozniot dolznik
                    na baranje na Zalozniot doveritel, e dolzen vednas a najdocna vo rok od 7 dena da go oslobodi predmetot na zalog
                    od toj nedostatok, a vo sprotivno ima obvrska da go zameni so drug predmet.
                    Pokraj pravoto navedeno vo stav 1 na ovoj clen, Zalozniot doveritel moze i pred pristignuvanjeto na svoeto
                    pobaruvanje da bara negova naplata od predmetot na zalog, dokolku Zalozniot dolznik ne postapi na nacin ili vo rok
                    utvrden vo ovoj clen, a isto takvo pravo Zalozniot doveritel ima i dokolku Zalozniot dolznik prevzema ili propusta
                    dejstvija poradi koi se doveduva vo opasnost ili ocigledno se namaluva vrednosta na predmetot.
                    Zalozniot dolznik e dolzen na Zalozniot doveritel <span className="bold">da mu ja najavi namerata za podignuvanje na zalozeniot
                    predmet najmalku eden den odnapred</span>, pri celosna isplata na pristignatoto pobaruvanje.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 9</p>
                <p>Zaloznoto pravo zasnovano so ovoj dogovor prestanuva poradi: ispolnuvanje na obvrskata od strana na
                    Zalozniot dolznik; istek na opredelenoto vreme; propaganje na zalozeniot predmet poradi visa sila; gubenje na
                    vladenieto na zalozeniot predmet; prodazba na predmetot zaradi realizacija na zaloznoto pravo; raskinuvanje na
                    Dogovorot za zalog; i pod drugi uslovi opredeleni so zakon.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 10</p>
                <p>Soglasno odredbite od Zakonot za dogovoren zalog, za zaloznoto pravo zasnovano vrz osnova na ovoj
                    Dogovor, kako pravo na racen i vladetelski zalog, <span className="bold">ne se vrsi upis na zalogot vo Zalozniot registar</span> koj go vodi
                    Cetralniot registar na RSM.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 11</p>
                <p>Soglasno cl.6 od Zakonot za zastita na licnite podatoci, obrabotkata na licnite podatoci na Zalozniot
                    dolznik se vrsi samo poradi celite na sklucuvanje na ovoj Dogovor vo koj istiot se javuva kako dogovorna strana i
                    negova izrecna i prethodno dobiena soglasnost koja ja potvrduva so stavanje na svojot potpis vo ovoj Dogovor.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 12</p>
                <p>Vo slucaj na spor, stranite se soglasni za mesno nadlezen da se smeta Osnoven Graganski sud Skopje.
                    Za se sto ne e regulirano so ovoj dogovor, ke se primenuvaat odredbite od Zakonot za dogovoren zalog,
                    Zakonot za obligacionite odnosi, i drugite pozitivni propisi vo RSM.
                    <span className="bold">Prilozite i aneksite se smetaat za sostaven del na ovoj Dogovor.</span></p>
            </Clen>
            <Clen>
                <p className="bold">Clen 13</p>
                <p>Dogovornite strani go sklucuvaat ovoj dogovor vo sostojba na zdrav razum i cista svest, so slobodno
                    izrazena volja, dobro se zapoznaeni so negovata sodrzina i znacenje, go priznavaat za tocen i nepokolebliv izraz
                    na nivnata vojla i za seto pogore navedeno svedocat so svoeracni potpisi.
                    Dogovornite strani se soglasni eventualnite trosocite povrzani so sklucuvanje na ovoj Dogovor da padnat
                    na tovar na Zalozniot dolznik.</p>
            </Clen>
            <Clen>
                <p className="bold">Clen 14</p>
                <p>Ovoj dogovor e sostaven od 14 clenovi na 2 lista ispecateni na dvete strani, potpisan na sekoj list od dvete
                    dogovorni strani, i e sklucen vo 2 identicni primeroci, od koi po 1 za sekoja dogovorna strana.</p>
            </Clen>
            <h2>DOGOVORNI STRANI</h2>
            <div>
                <Clen>
                    <h3>ZALOZEN DOVERITEL</h3>
                    <p>______________________</p>
                    <h4>Volter A&B DOOEL Skopje</h4>
                    <h4>EDB 4080019581460 i EMBS 7349750</h4>
                    <h4>Aleksandar Kocevski, Upravitel</h4>
                </Clen>
                <Clen>
                    <h3>ZALOZEN DOLZNIK</h3>
                    <p>______________________</p>
                    <h4>{fullName}</h4>
                </Clen>
            </div>
        </Section>
    )
})

export default PawnAgreementDocument;

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