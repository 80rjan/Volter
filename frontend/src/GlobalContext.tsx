import { createContext, useContext, ReactNode } from 'react';

interface GlobalContextType {}

const GlobalContext = createContext<GlobalContextType>({});

export const GlobalProvider = ({ children }: { children: ReactNode }) => {
    return (
        <GlobalContext.Provider value={{}}>
            {children}
        </GlobalContext.Provider>
    );
};

export const useGlobalContext = () => useContext(GlobalContext);
