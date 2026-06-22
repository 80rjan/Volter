// Customer record (GET /customers and GET /customers/{id} -> CustomerResponse).
// The list and detail endpoints return the same shape.
export interface Customer {
    id: number;
    fullName: string;
    nationalId: string;
    phonePrimary: string;
    phoneSecondary: string | null;
    address: string;
    city: string;
    createdAt: string;
    updatedAt: string;
}

// Body for PATCH /customers/{id} (CustomerUpdateRequest). nationalId is immutable.
export interface CustomerUpdate {
    fullName: string;
    phonePrimary: string;
    phoneSecondary: string;
    address: string;
    city: string;
}
