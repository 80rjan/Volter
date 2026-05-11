import Nav from "../components/Nav.tsx";
import CashRegister from "./CashRegister.tsx";

export default function YearlyReport() {
    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-4 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <h1 className="text-3xl font-semibold">Годишен Извештај</h1>
                </div>
                <div className="flex-1" />
                <CashRegister refreshDependency={true} />
            </div>
        </div>
    );
}
