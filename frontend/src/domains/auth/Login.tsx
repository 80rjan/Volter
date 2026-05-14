// @ts-ignore
import logo from "../../assets/volter-zalozna-kukja-3D-slika.png";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { LogIn } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        axios.post(`${API_BASE}/auth/login`, { username, password })
            .then(res => {
                localStorage.setItem("token", res.data.token);
                navigate("/");
            })
            .catch(() => setError("Погрешно корисничко име или лозинка"))
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded-sm text-base p-2.5 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)] outline-none transition-shadow duration-200 focus:shadow-[0_0_6px_rgba(0,96,64,0.5)]";
    const labelClass = "text-grey text-sm";

    return (
        <div className="h-screen flex items-center justify-center bg-[#eee]">
            <div className="flex flex-col items-center gap-8 bg-white rounded-xl shadow-[0_4px_28px_rgba(0,0,0,0.13)] p-10 w-[360px]">
                <img src={logo} alt="Волтер Залозна Куќа" className="w-44 h-auto" />

                <form className="flex flex-col gap-5 w-full" onSubmit={handleSubmit}>
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
            </div>
        </div>
    );
}
