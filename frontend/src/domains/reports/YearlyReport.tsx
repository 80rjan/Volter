import CashRegister from "../../shared/components/CashRegister.tsx";

export default function YearlyReport() {
    return (
        <div className="h-screen flex pl-16">
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
