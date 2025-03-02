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
        city: '',
        brand: '',
        model: '',
        year: '',
        price_pawned: '',
        provision: '',
        total_days: '',
        description: '',
        weight: '',
        carat: '',
        type: '',
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
            case "electronics_pawn": return <ElectronicsInputs handleInputChange={handleInputChange}/>;
            case "gold_pawn": return <GoldInputs handleInputChange={handleInputChange}/>;
            case "vehicle_pawn": return <VehicleInputs handleInputChange={handleInputChange}/>;
            case "watch_pawn": return <WatchInputs handleInputChange={handleInputChange}/>;
            case "other_pawn": return <OtherInputs handleInputChange={handleInputChange}/>;
        }
    };

    const handleObjectSelect = (event, value) => {
        if (value) {
            setFormData((prev) => ({
                ...prev,
                name: value.name,
                embg: value.embg,
                telephone: value.telephone,
                city: value.city,
            }));
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
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

        axios.post('/insertPawn', formData)
            .then(response => {
                refresh();
                closeModal();
            })
            .catch(error => {
                console.error('Error adding pawn:', error);
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchClients(limit, offset.current, autocompleteValue);
    }, []);

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                {loading ? <Loading /> :
                    <>
                        <Header>
                            <div>
                                <CopyPlus size={32} />
                                <h1>Add New Pawn</h1>
                            </div>
                            <ButtonClose onClick={closeModal}>
                                <X size={32} />
                            </ButtonClose>
                        </Header>
                        <Form onSubmit={handleSubmit}>
                            <div>
                                <ClientInputs>
                                    <span><User size={20} /> Enter Client Details</span>
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
                                        ListboxProps={{ onScroll: handleScroll }}
                                        renderInput={(params) => <TextField {...params} label="Search existing clients" />}
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
                                            </li>
                                        )}
                                        filterOptions={(options, state) =>
                                            options.filter(option =>
                                                option.name.toLowerCase().includes(state.inputValue.toLowerCase()) ||
                                                option.embg.includes(state.inputValue) ||
                                                option.telephone.includes(state.inputValue)
                                            )
                                        }
                                        sx={{
                                            background: "white",
                                            borderRadius: ".3rem",
                                            fontSize: "1rem",
                                            boxShadow: "0 0 4px rgba(0,0,0,0.2)",
                                        }}
                                    />
                                    <StyledInput
                                        placeholder="Client name"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleInputChange}
                                        required
                                    />
                                    <StyledInput
                                        placeholder="Client embg"
                                        name="embg"
                                        value={formData.embg}
                                        onChange={handleInputChange}
                                        required
                                    />
                                    <StyledInput
                                        placeholder="Client telephone"
                                        name="telephone"
                                        value={formData.telephone}
                                        onChange={handleInputChange}
                                        required
                                    />
                                    <StyledInput
                                        placeholder="Client city"
                                        name="city"
                                        value={formData.city}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </ClientInputs>
                                <PawnInputs>
                                    <span><Database size={20}/> Enter Pawn Details</span>
                                    <select name="category" value={formData.category} onChange={handleInputChange}>
                                        <option value="electronics_pawn">Electronics</option>
                                        <option value="gold_pawn">Gold</option>
                                        <option value="vehicle_pawn">Vehicle</option>
                                        <option value="watch_pawn">Watch</option>
                                        <option value="other_pawn">Other</option>
                                    </select>
                                    {renderCategoryInputs()}
                                </PawnInputs>
                            </div>
                            <Button type="submit">
                                <CheckCheck size={28} /> Confirm
                            </Button>
                        </Form>
                    </>
                }
            </Wrapper>
        </>,
        document.getElementById("portal")
    );
}

function ElectronicsInputs({ handleInputChange }) {
    return (
        <>
            <StyledInput placeholder="Item brand" name="brand" onChange={handleInputChange} required />
            <StyledInput placeholder="Item year" name="year" onChange={handleInputChange} required />
            <StyledInput placeholder="Price pawned" name="price_pawned" onChange={handleInputChange} required />
            <StyledInput placeholder="Monthly provision" name="provision" onChange={handleInputChange} required />
            <StyledInput placeholder="Total days" name="total_days" onChange={handleInputChange} required />
            <StyledInput placeholder="Item description" name="description" onChange={handleInputChange} required />
        </>
    );
}

function GoldInputs({ handleInputChange }) {
    return (
        <>
            <StyledInput placeholder="Gold weight" name="weight" onChange={handleInputChange} required />
            <StyledInput placeholder="Gold carats" name="carats" onChange={handleInputChange} required />
            <StyledInput placeholder="Gold type" name="type" onChange={handleInputChange} required />
            <StyledInput placeholder="Price pawned" name="price_pawned" onChange={handleInputChange} required />
            <StyledInput placeholder="Monthly provision" name="provision" onChange={handleInputChange} required />
            <StyledInput placeholder="Total days" name="total_days" onChange={handleInputChange} required />
            <StyledInput placeholder="Item description" name="description" onChange={handleInputChange} required />
        </>
    );
}

function VehicleInputs({ handleInputChange }) {
    return (
        <>
            <StyledInput placeholder="Vehicle brand" name="brand" onChange={handleInputChange} required />
            <StyledInput placeholder="Vehicle model" name="model" onChange={handleInputChange} required />
            <StyledInput placeholder="Vehicle year" name="year" onChange={handleInputChange} required />
            <StyledInput placeholder="Price pawned" name="price_pawned" onChange={handleInputChange} required />
            <StyledInput placeholder="Monthly provision" name="provision" onChange={handleInputChange} required />
            <StyledInput placeholder="Total days" name="total_days" onChange={handleInputChange} required />
            <StyledInput placeholder="Item description" name="description" onChange={handleInputChange} required />
        </>
    );
}

function WatchInputs({ handleInputChange }) {
    return (
        <>
            <StyledInput placeholder="Watch brand" name="brand" onChange={handleInputChange} required />
            <StyledInput placeholder="Watch year" name="year" onChange={handleInputChange} required/>
            <StyledInput placeholder="Price pawned" name="price_pawned" onChange={handleInputChange} required/>
            <StyledInput placeholder="Monthly provision" name="provision" onChange={handleInputChange} required />
            <StyledInput placeholder="Total days" name="total_days" onChange={handleInputChange} required />
            <StyledInput placeholder="Item description" name="description" onChange={handleInputChange} required />
        </>
    );
}

function OtherInputs({ handleInputChange }) {
    return (
        <>
            <StyledInput placeholder="Price pawned" name="price_pawned" onChange={handleInputChange} required />
            <StyledInput placeholder="Monthly provision" name="provision" onChange={handleInputChange} required />
            <StyledInput placeholder="Total days" name="total_days" onChange={handleInputChange} required />
            <StyledInput placeholder="Item description" name="description" onChange={handleInputChange} required />
        </>
    );
}

const Overlay = styled.div`
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0,0,0, .7);
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
        font-weight: 500;
    }
`;

const ClientInputs = styled.div`
    display: flex;
    flex-direction: column;
    gap: .8rem;
`;

const PawnInputs = styled.div`
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: .8rem 2rem;

    select {
        padding: 0 .5rem;
        border-radius: .2rem;
        border: 2px solid rgba(0, 0, 0, 0.6);
        cursor: pointer;
        font-size: 1rem;
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