// @ts-ignore
import logo from "../../assets/volter-zalozna-kukja-3D-slika.png";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { LogIn, Store, ArrowLeft, ChevronRight, KeyRound } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

interface Shop {
    id: number;
    name: string;
    code: string;
}

type Step = "credentials" | "changePassword" | "shop";

export default function Login() {
    const { refreshUser } = useAuth();
    const [step, setStep] = useState<Step>("credentials");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [preAuthToken, setPreAuthToken] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [shops, setShops] = useState<Shop[]>([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    // Step 1: verify credentials, receive a short-lived pre-auth token + the
    // shops this staff member is allowed to use. No shop is chosen yet.
    const handleLogin = (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        axios.post(`${API_BASE}/auth/login`, { username, password })
            .then(res => {
                setPreAuthToken(res.data.preAuthToken);
                setShops(res.data.shops ?? []);
                // A manager-created account must replace its temporary password first.
                setStep(res.data.passwordChangeRequired ? "changePassword" : "shop");
            })
            .catch(err => {
                if (axios.isAxiosError(err) && err.response?.status === 401) {
                    const message = err.response.data?.message ?? "";
                    setError(message.includes("No shops")
                        ? "Немате доделено продавница"
                        : "Погрешно корисничко име или лозинка");
                    return;
                }
                setError("Неуспешна најава, обидете се повторно");
            })
            .finally(() => setLoading(false));
    };

    // Forced first-login change: replace the manager-set temporary password using the
    // pre-auth token. Until this succeeds the backend refuses select-shop.
    const handleChangePassword = (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        if (newPassword.length < 8) { setError("Лозинката мора да има барем 8 карактери"); return; }
        if (newPassword !== confirmPassword) { setError("Лозинките не се совпаѓаат"); return; }
        setLoading(true);
        axios.post(
            `${API_BASE}/auth/change-password`,
            { newPassword },
            { headers: { Authorization: `Bearer ${preAuthToken}` } },
        )
            .then(() => { setNewPassword(""); setConfirmPassword(""); setStep("shop"); })
            .catch(err => {
                if (axios.isAxiosError(err) && err.response?.status === 401) {
                    setError("Сесијата истече, најавете се повторно");
                    backToCredentials();
                    return;
                }
                setError("Неуспешна промена на лозинка, обидете се повторно");
            })
            .finally(() => setLoading(false));
    };

    // Step 2: exchange the pre-auth token for a shop-scoped access token. This
    // is the token the rest of the app uses for the session.
    const handleSelectShop = (shopId: number) => {
        setError("");
        setLoading(true);
        axios.post(
            `${API_BASE}/auth/select-shop`,
            { shopId },
            { headers: { Authorization: `Bearer ${preAuthToken}` } },
        )
            .then(async res => {
                localStorage.setItem("token", res.data.accessToken);
                axios.defaults.headers.common["Authorization"] = `Bearer ${res.data.accessToken}`;
                await refreshUser();   // load roles/permissions for the chosen shop
                navigate("/");
            })
            .catch(err => {
                if (axios.isAxiosError(err) && err.response?.status === 401) {
                    // Pre-auth token expired, or no longer assigned to this shop.
                    setError("Сесијата истече, најавете се повторно");
                    backToCredentials();
                    return;
                }
                setError("Неуспешен избор на продавница, обидете се повторно");
            })
            .finally(() => setLoading(false));
    };

    const backToCredentials = () => {
        setStep("credentials");
        setPreAuthToken("");
        setShops([]);
        setPassword("");
        setNewPassword("");
        setConfirmPassword("");
    };

    const inputClass = "border-none rounded-sm text-base p-2.5 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)] outline-none transition-shadow duration-200 focus:shadow-[0_0_6px_rgba(0,96,64,0.5)]";
    const labelClass = "text-grey text-sm";

    return (
        <div className="h-screen flex items-center justify-center bg-[#eee] px-4">
            <div className="flex flex-col items-center gap-8 bg-white rounded-xl shadow-[0_4px_28px_rgba(0,0,0,0.13)] p-6 md:p-10 w-full max-w-[360px]">
                <img src={logo} alt="Волтер Залозна Куќа" className="w-44 h-auto" />

                {step === "credentials" ? (
                    <form className="flex flex-col gap-5 w-full" onSubmit={handleLogin}>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Корисничко Име</p>
                            <input
                                type="text"
                                className={inputClass}
                                value={username}
                                onChange={e => setUsername(e.target.value)}
                                required
                                autoFocus
                            />
                        </div>

                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Лозинка</p>
                            <input
                                type="password"
                                className={inputClass}
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        {error && (
                            <p className="text-red-500 text-sm text-center -mt-1">{error}</p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="flex justify-center items-center gap-2 mt-1 py-2.5 rounded bg-green text-white text-lg font-semibold shadow-[0_0_8px_rgba(0,0,0,0.15)] transition-all duration-300 hover:scale-105 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            {loading
                                ? <Loading width={28} height={28} />
                                : <><LogIn size={20} /> Најави се</>
                            }
                        </button>
                    </form>
                ) : step === "changePassword" ? (
                    <form className="flex flex-col gap-5 w-full" onSubmit={handleChangePassword}>
                        <p className="text-grey text-sm text-center -mb-2">
                            Поставете нова лозинка за да продолжите
                        </p>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Нова лозинка</p>
                            <input
                                type="password"
                                className={inputClass}
                                value={newPassword}
                                onChange={e => setNewPassword(e.target.value)}
                                required
                                autoFocus
                            />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Потврди лозинка</p>
                            <input
                                type="password"
                                className={inputClass}
                                value={confirmPassword}
                                onChange={e => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>

                        {error && (
                            <p className="text-red-500 text-sm text-center -mt-1">{error}</p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="flex justify-center items-center gap-2 mt-1 py-2.5 rounded bg-green text-white text-lg font-semibold shadow-[0_0_8px_rgba(0,0,0,0.15)] transition-all duration-300 hover:scale-105 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            {loading
                                ? <Loading width={28} height={28} />
                                : <><KeyRound size={20} /> Зачувај лозинка</>
                            }
                        </button>
                    </form>
                ) : (
                    <div className="flex flex-col gap-4 w-full">
                        <div className="flex items-center gap-2">
                            <button
                                type="button"
                                onClick={backToCredentials}
                                disabled={loading}
                                className="text-grey hover:text-green transition-colors duration-200 disabled:opacity-40"
                                aria-label="Назад"
                            >
                                <ArrowLeft size={20} />
                            </button>
                            <p className="text-grey text-sm">Избери продавница</p>
                        </div>

                        <div className="flex flex-col gap-2">
                            {shops.map(shop => (
                                <button
                                    key={shop.id}
                                    type="button"
                                    onClick={() => handleSelectShop(shop.id)}
                                    disabled={loading}
                                    className="flex items-center gap-3 p-3 rounded-sm text-left shadow-[0_0_4px_rgba(0,0,0,0.2)] transition-all duration-200 hover:shadow-[0_0_6px_rgba(0,96,64,0.5)] hover:scale-[1.02] disabled:cursor-not-allowed disabled:opacity-40"
                                >
                                    <Store size={20} className="text-green shrink-0" />
                                    <span className="flex-1 text-base">{shop.name}</span>
                                    <ChevronRight size={18} className="text-grey shrink-0" />
                                </button>
                            ))}
                        </div>

                        {loading && (
                            <div className="flex justify-center py-1">
                                <Loading width={28} height={28} />
                            </div>
                        )}

                        {error && (
                            <p className="text-red-500 text-sm text-center">{error}</p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
