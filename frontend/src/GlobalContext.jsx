import { createContext, useContext, useState } from 'react';

// Create the context
const GlobalContext = createContext();

// Create a provider component
export const GlobalProvider = ({ children }) => {

    return (
        <GlobalContext.Provider value={{  }}>
            {children}
        </GlobalContext.Provider>
    );
};

// Optional: custom hook for easier usage
export const useGlobalContext = () => useContext(GlobalContext);
