import styled from "styled-components";
import ReactDom from "react-dom";
import { X, CopyPlus, CheckCheck, User, Database } from 'lucide-react';
import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Autocomplete, TextField } from "@mui/material";
import Loading from "./Loading.jsx";

export default function ModalAddNewPawn({ closeModal, refresh }) {
    const [clients, setClients] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        embg: '',
        telephone: '',
        telephone_2: '',
        city: '',
        brand: '',
        model: '',
        year: '',
        price_pawned: '',
        provision: 0,
        provisionPercent: 0,
        total_days: '',
        description: '',
        weight: '',
        carat: '',
        type: '',
        date: new Date().toISOString().split("T")[0],
        category: 'electronics_pawn' // Add category to formData
    });
    const offset = useRef(0);
    const limit = 5;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableClientsRef = useRef(null);
    const [autocompleteValue, setAutocompleteValue] = useState("");
    const [loading, setLoading] = useState(false);

    const renderCategoryInputs = () => {
        switch (formData.category) {
            case "electronics_pawn": return <ElectronicsInputs handleInputChange={handleInputChange} date={formData.date} formData={formData}/>;
            case "gold_pawn": return <GoldInputs handleInputChange={handleInputChange} date={formData.date} formData={formData}/>;
            case "vehicle_pawn": return <VehicleInputs handleInputChange={handleInputChange} date={formData.date} formData={formData}/>;
            case "watch_pawn": return <WatchInputs handleInputChange={handleInputChange} date={formData.date} formData={formData}/>;
            case "other_pawn": return <OtherInputs handleInputChange={handleInputChange} date={formData.date} formData={formData}/>;
        }
    };

    const handleObjectSelect = (event, value) => {
        if (value) {
            setFormData((prev) => ({
                ...prev,
                name: value.name,
                embg: value.embg,
                telephone: value.telephone,
                telephone_2: value.telephone_2,
                city: value.city,
            }));
        }
    };

    const handleInputChange = (name, value) => {
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const fetchClients = (limit, offset, search) => {
        axios.get(`http://localhost:3000/clients?limit=${limit}&offset=${offset}&search=${search}`)
            .then(res => {
                if (res.data.length > 0) {
                    offset === 0 ? setClients(res.data) : setClients(prev => [...prev, ...res.data]);
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => {
                console.error('Error fetching all clients:', error);
            });
    };

    const handleScroll = (event) => {
        const scrollDiv = event.target;
        const scrollHeight = scrollDiv.scrollHeight;
        const scrollTop = scrollDiv.scrollTop;
        const clientHeight = scrollDiv.clientHeight;

        if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.2 && !isLastPage) {
            offset.current += limit;
            fetchClients(limit, offset.current, autocompleteValue);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true);
        axios.post(`http://localhost:3000/insertPawn`, formData)
            .then(response => {
                closeModal();
                refresh();
            })
            .catch(error => {
                console.error('Error adding pawn:', error);
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchClients(limit, offset.current, autocompleteValue);
    }, []);

    console.log(clients)

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                {loading ? <Loading /> :
                    <>
                        <Header>
                            <div>
                                <CopyPlus size={32} />
                                <h1>Внеси Нов Залог</h1>
                            </div>
                            <ButtonClose onClick={closeModal}>
                                <X size={32} />
                            </ButtonClose>
                        </Header>
                        <Form onSubmit={handleSubmit}>
                            <div>
                                <ClientInputs>
                                    <span style={{width: "max-content", marginBottom: ".8rem"}}><User
                                        size={20}/> Внеси Податоци за Клиентот</span>
                                    <Autocomplete
                                        ref={scrollableClientsRef}
                                        options={clients}
                                        getOptionLabel={(option) => option.name}
                                        onChange={handleObjectSelect}
                                        onInputChange={(event, value) => {
                                            offset.current = 0;
                                            setAutocompleteValue(value);
                                            fetchClients(limit, offset.current, value);
                                        }}
                                        ListboxProps={{onScroll: handleScroll}}
                                        renderInput={(params) => <TextField {...params} label="Постоечки клиенти"/>}
                                        isOptionEqualToValue={(option, value) => option.id === value.id}
                                        renderOption={(props, option) => (
                                            <li {...props} key={option.id} style={{
                                                display: 'flex',
                                                flexDirection: 'column',
                                                gap: '.1rem',
                                                alignItems: 'flex-start'
                                            }}>
                                                <strong>{option.name}</strong>
                                                <span>{option.embg}</span>
                                                <span>{option.telephone}</span>
                                                <span>{option.telephone_2}</span>
                                            </li>
                                        )}
                                        filterOptions={(options, state) =>
                                            options.filter(option =>
                                                option.name.toLowerCase().includes(state.inputValue.toLowerCase()) ||
                                                (option.embg && option.embg.includes(state.inputValue)) ||
                                                ( option.telephone && option.telephone.includes(state.inputValue)) ||
                                                (option.telephone_2 && option.telephone_2.includes(state.inputValue))
                                            )
                                        }
                                        sx={{
                                            background: "white",
                                            borderRadius: ".3rem",
                                            fontSize: "1rem",
                                            boxShadow: "0 0 4px rgba(0,0,0,0.2)",
                                        }}
                                    />
                                    <div>
                                        <p>Име</p>
                                        <StyledInput
                                            name="name"
                                            value={formData.name}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div>
                                        <p>Ембг</p>
                                        <StyledInput
                                            name="embg"
                                            value={formData.embg}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div>
                                        <p>Телефон 1</p>
                                        <StyledInput
                                            name="telephone"
                                            value={formData.telephone}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div>
                                        <p>Телефон 2</p>
                                        <StyledInput
                                            name="telephone_2"
                                            value={formData.telephone_2}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}
                                        />
                                    </div>
                                    <div>
                                        <p>Град</p>
                                        <StyledInput
                                            name="city"
                                            value={formData.city}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}
                                            required
                                        />
                                    </div>
                                </ClientInputs>
                                <PawnInputs>
                                    <span style={{width: "max-content"}}><Database size={20}/> Внеси Податоци за Предметот</span>
                                    <select name="category" value={formData.category}
                                            onChange={e => handleInputChange(e.target.name, e.target.value)}>
                                        <option value="electronics_pawn">Електроника</option>
                                        <option value="gold_pawn">Злато</option>
                                        <option value="vehicle_pawn">Возила</option>
                                        <option value="watch_pawn">Часовници</option>
                                        <option value="other_pawn">Останато</option>
                                    </select>
                                    {renderCategoryInputs()}
                                </PawnInputs>
                            </div>
                            <Button type="submit">
                                <CheckCheck size={28} /> Потврди
                            </Button>
                        </Form>
                    </>
                }
            </Wrapper>
        </>,
        document.getElementById("portal")
    );
}

function ElectronicsInputs({ handleInputChange, formData, date }) {
    return (
        <>
            <div>
                <p>Бренд</p>
                <StyledInput name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Година</p>
                <StyledInput name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required/>
            </div>
            <div>
                <p>Вредност на залогот</p>
                <StyledInput name="price_pawned" onChange={e => {
                    handleInputChange(e.target.name, e.target.value)
                    handleInputChange('provisionPercent', Math.round((formData.provision /  e.target.value * 100) * 100) / 100)
                }} type="number" required/>
            </div>
            <div>
                <p>Провизија</p>
                <div>
                    <span><StyledInput name="provision" value={formData.provision || ''} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provisionPercent', Math.round((e.target.value / formData.price_pawned * 100) * 100) / 100)
                    }} type="number" required/>ден</span>
                    <span><StyledInput name="provisionPercent" value={formData.provisionPercent || ''} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provision', Math.round(e.target.value / 100 * formData.price_pawned))
                    }} type="number" step="0.001" required/>%</span>
                </div>
            </div>
            <div>
                <p>Валидност во денови</p>
                <StyledInput name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required/>
            </div>
            <div>
                <p>Опис</p>
                <StyledInput name="description" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Заложено на</p>
                <StyledInput type="date" value={date} name="date" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
        </>
    );
}

function GoldInputs({ handleInputChange, formData, date }) {
    return (
        <>
            <div>
                <p>Тежина (во грамови)</p>
                <StyledInput name="weight" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" step="0.001" required/>
            </div>
            <div>
                <p>Каратажа (број)</p>
                <StyledInput name="carats" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required/>
            </div>
            <div>
                <p>Тип на злато</p>
                <StyledInput name="type" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Вредност на залогот</p>
                <StyledInput name="price_pawned" onChange={e => {
                    handleInputChange(e.target.name, e.target.value)
                    handleInputChange('provisionPercent', Math.round((formData.provision /  e.target.value * 100) * 100) / 100)
                }} type="number" required/>
            </div>
            <div>
                <p>Провизија</p>
                <div>
                    <span><StyledInput name="provision" value={formData.provision} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provisionPercent', Math.round((e.target.value / formData.price_pawned * 100) * 100) / 100)
                    }} type="number" required/>ден</span>
                    <span><StyledInput name="provisionPercent" value={formData.provisionPercent} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provision', Math.round(e.target.value / 100 * formData.price_pawned))
                    }} type="number" step="0.001" required/>%</span>
                </div>
            </div>
            <div>
                <p>Валидност во денови</p>
                <StyledInput name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             type="number" required/>
            </div>
            <div>
                <p>Опис</p>
                <StyledInput name="description" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             required/>
            </div>
            <div>
                <p>Заложено на</p>
                <StyledInput type="date" value={date} name="date"
                             onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
        </>
    );
}

function VehicleInputs({handleInputChange, formData, date}) {
    return (
        <>
            <div>
            <p>Бренд</p>
                <StyledInput name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Модел</p>
                <StyledInput name="model" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Година</p>
                <StyledInput name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required/>
            </div>
            <div>
                <p>Вредност на залогот</p>
                <StyledInput name="price_pawned" onChange={e => {
                    handleInputChange(e.target.name, e.target.value)
                    handleInputChange('provisionPercent', Math.round((formData.provision /  e.target.value * 100) * 100) / 100)
                }} type="number" required/>
            </div>
            <div>
                <p>Провизија</p>
                <div>
                    <span><StyledInput name="provision" value={formData.provision} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provisionPercent', Math.round((e.target.value / formData.price_pawned * 100) * 100) / 100)
                    }} type="number" required/>ден</span>
                    <span><StyledInput name="provisionPercent" value={formData.provisionPercent} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provision', Math.round(e.target.value / 100 * formData.price_pawned))
                    }} type="number" step="0.001" required/>%</span>
                </div>
            </div>
            <div>
                <p>Валидност во денови</p>
                <StyledInput name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             type="number" required/>
            </div>
            <div>
                <p>Опис</p>
                <StyledInput name="description" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             required/>
            </div>
            <div>
                <p>Заложено на</p>
                <StyledInput type="date" value={date} name="date"
                             onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
        </>
    );
}

function WatchInputs({handleInputChange, formData, date}) {
    return (
        <>
            <div>
            <p>Бренд</p>
                <StyledInput name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
            <div>
                <p>Година</p>
                <StyledInput name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required/>
            </div>
            <div>
                <p>Вредност на залогот</p>
                <StyledInput name="price_pawned" onChange={e => {
                    handleInputChange(e.target.name, e.target.value)
                    handleInputChange('provisionPercent', Math.round((formData.provision /  e.target.value * 100) * 100) / 100)
                }} type="number" required/>
            </div>
            <div>
                <p>Провизија</p>
                <div>
                    <span><StyledInput name="provision" value={formData.provision} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provisionPercent', Math.round((e.target.value / formData.price_pawned * 100) * 100) / 100)
                    }} type="number" required/>ден</span>
                    <span><StyledInput name="provisionPercent" value={formData.provisionPercent} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provision', Math.round(e.target.value / 100 * formData.price_pawned))
                    }} type="number" step="0.001" required/>%</span>
                </div>
            </div>
            <div>
                <p>Валидност во денови</p>
                <StyledInput name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             type="number" required/>
            </div>
            <div>
                <p>Опис</p>
                <StyledInput name="description" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             required/>
            </div>
            <div>
                <p>Заложено на</p>
                <StyledInput type="date" value={date} name="date"
                             onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
        </>
    );
}

function OtherInputs({handleInputChange, formData, date}) {
    return (
        <>
            <div>
            <p>Вредност на залогот</p>
                <StyledInput name="price_pawned" onChange={e => {
                    handleInputChange(e.target.name, e.target.value)
                    handleInputChange('provisionPercent', Math.round((formData.provision /  e.target.value * 100) * 100) / 100)
                }} type="number" required/>
            </div>
            <div>
                <p>Провизија</p>
                <div>
                    <span><StyledInput name="provision" value={formData.provision} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provisionPercent', Math.round((e.target.value / formData.price_pawned * 100) * 100) / 100)
                    }} type="number" required/>ден</span>
                    <span><StyledInput name="provisionPercent" value={formData.provisionPercent} onChange={e => {
                        handleInputChange(e.target.name, e.target.value)
                        handleInputChange('provision', Math.round(e.target.value / 100 * formData.price_pawned))
                    }} type="number" step="0.001" required/>%</span>
                </div>
            </div>
            <div>
                <p>Валидност во денови</p>
                <StyledInput name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             type="number" required/>
            </div>
            <div>
                <p>Опис</p>
                <StyledInput name="description" onChange={e => handleInputChange(e.target.name, e.target.value)}
                             required/>
            </div>
            <div>
                <p>Заложено на</p>
                <StyledInput type="date" value={date} name="date"
                             onChange={e => handleInputChange(e.target.name, e.target.value)} required/>
            </div>
        </>
    );
}

const Overlay = styled.div`
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, .7);
    z-index: 1000;
`;

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 2rem;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: #eee;
    z-index: 1000;
    padding: 2rem;
    border-radius: 8px;
    min-width: fit-content;
    max-width: 90%;
`;

const Header = styled.div`
    display: flex;
    justify-content: space-between;

    div {
        display: flex;
        align-items: center;
        gap: .4rem;
    }
`;

const ButtonClose = styled.button`
    svg {
        transition: all 400ms ease-in-out;
    }
    svg:hover {
        transform: rotate(90deg);
    }
`;

const Form = styled.form`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2rem;

    &>div {
        display: flex;
        gap: 2rem;
        align-items: flex-start;
    }

    span {
        display: flex;
        align-items: center;
        gap: .4rem;
        //font-size: 1.2rem;
        font-weight: 500;
        margin-bottom: .4rem;
    }
`;

const ClientInputs = styled.div`
    display: flex;
    flex-direction: column;
    gap: .4rem;

    & > div {
        display: flex;
        align-items: center;
        gap: 1rem;
        justify-content: space-between;

        & > p {
            color: #666;
            white-space: nowrap;
        }
    }
`;

const PawnInputs = styled.div`
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: .4rem 2rem;

    select {
        padding: 0 .5rem;
        border-radius: .2rem;
        border: 2px solid rgba(0, 0, 0, 0.6);
        cursor: pointer;
        font-size: 1rem;
    }

    & > div {
        display: flex;
        flex-direction: column;
        gap: .2rem;

        & > p {
            margin-left: -0.4rem;
            color: #666;
        }

        & > div {
            display: flex;
            gap: .4rem; 
            align-items: flex-end; 
            width: 100%;
            
            & > span {
                color: #666;
                & > input {
                    width: 50%;
                }
            }
        } 
    }
`;

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1rem;
    padding: .5rem;
    box-shadow: 0 0 4px rgba(0,0,0,0.2);
    height: fit-content;
`;

const Button = styled.button`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: .4rem;
    padding: .6rem 8rem;
    border-radius: 4px;
    background: var(--green);
    color: white;
    font-size: 1.4rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    transition: scale 400ms ease-in-out;

    &:hover {
        scale: 1.05;
    }
`;