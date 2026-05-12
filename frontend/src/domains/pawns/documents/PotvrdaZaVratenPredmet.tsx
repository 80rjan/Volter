import { forwardRef } from "react";

interface Props {
    fullName: string;
}

const sectionStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center", padding: "50px 50px", gap: "1rem" };
const clenStyle: React.CSSProperties = { display: "flex", flexDirection: "column", alignItems: "center", marginLeft: "auto" };
const h1Style: React.CSSProperties = { fontSize: "18px" };
const h2Style: React.CSSProperties = { fontSize: "16px" };
const pStyle: React.CSSProperties = { fontSize: "12px" };
const bold: React.CSSProperties = { fontWeight: "bold" };
const divRowStyle: React.CSSProperties = { width: "100%", display: "flex", justifyContent: "flex-end" };

const PotvrdaZaVratenPredmet = forwardRef<HTMLDivElement, Props>(
    ({ fullName }, ref) => {
        return (
            <div ref={ref} style={sectionStyle}>
                <h1 style={h1Style}>Потврда за враќање на заложен предмет</h1>
                <p style={pStyle}>Јас, долупотпишаниот/та во својство на Заложен должник во Договор за рачен залог чиј составен дел е овој Прилог, на денот на склучувањето на Договорот, <span style={bold}>под полна морална, материјална и кривична одговорност, ПОТВРДУВАМ</span> дека подвижната ствар од чл.2 на Договорот, која ја предадов како <span style={bold}>заложен предмет</span> во рачен владетелски залог на Заложниот доверител: <span style={bold}>Друштво за услуги ВОЛТЕР А&amp;Б ДООЕЛ Скопје</span>, со седиште на ул. Булевар Партизански одреди бр.17-4, Скопје-Центар, со ЕДБ 4080019581460 и ЕМБС 7349750 застапувано од овластено лице Александар Коцевски Управител, по целосно подмирување на побарувањето, односно исплата на заемот, <span style={bold}>ми се враќа назад во сопственост и владение</span> од страна на Заложниот доверител, во истата состојба во која што му го предадов заложениот предмет.</p>
                <p style={pStyle}>Дополнителни рекламации за состојбата, својствата и функционалноста на заложениот предмет нема да истакнувам, поради фактот што на денот на враќањето на предметот се уверив лично и непоколебливо дека истиот е во идентична состојба со онаа во која што е предаден во залог.</p>
                <p style={pStyle}>Оваа потврда да му послужи на Заложниот доверител во случај истиот да има потреба да докаже пред мене, трето лице или надлежен орган дека заложениот предмет не е повеќе во негово владение, односно ми е вратен.</p>
                <p style={pStyle}>Се согласувам доказот да се прикачи кон овој Прилог и кон Договорот за рачен залог и да стане негов составен дел.</p>
                <div style={divRowStyle}>
                    <div style={clenStyle}>
                        <h2 style={h2Style}>ЗАЛОЖЕН ДОЛЖНИК</h2>
                        <p style={pStyle}>______________________</p>
                        <h2 style={h2Style}>{fullName}</h2>
                    </div>
                </div>
            </div>
        );
    }
);

export default PotvrdaZaVratenPredmet;
