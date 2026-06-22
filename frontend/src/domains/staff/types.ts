export type StaffStatus = "ACTIVE" | "INACTIVE" | "SUSPENDED";
export type StaffRoleStatus = "GRANTED" | "REVOKED";

export const STAFF_STATUS_LABEL: Record<StaffStatus, string> = {
    ACTIVE: "Активен",
    INACTIVE: "Неактивен",
    SUSPENDED: "Суспендиран",
};

// GET /admin/staff -> StaffResponse (list row).
export interface StaffRow {
    id: number;
    fullName: string;
    username: string;
    status: StaffStatus;
    managerId: number | null;
    createdAt: string;
    deletedAt: string | null;
}

export interface Permission {
    id: number;
    name: string;
    category: string;
}

export interface RoleDetailed {
    id: number;
    name: string;
    permissions: Permission[];
    createdAt: string;
}

export interface Shop {
    id: number;
    name: string;
    code: string;
}

export interface StaffRoleGrant {
    id: number;
    role: RoleDetailed;
    shop: Shop | null;
    status: StaffRoleStatus;
    grantedAt: string;
    revokedAt: string | null;
}

// GET /admin/staff/shop-options -> ShopResponse[] (picker)
export interface ShopOption {
    id: number;
    name: string;
    code: string;
}

// GET /admin/roles -> RoleResponse[] (picker)
export interface RoleOption {
    id: number;
    name: string;
    permissionCount?: number;
}

// GET /admin/staff/{id}/shops -> StaffShopAssignmentResponse[]
export interface StaffShopAssignment {
    shopId: number;
    shopName: string;
    shopCode: string;
    staffRoleId: number | null;
    roleId: number | null;
    roleName: string | null;
}

// GET /reports/staff/{id}/period -> StaffPerformanceResponse
export interface StaffPerformance {
    staffId: number;
    dateFrom: string;
    dateTo: string;
    revenue: number;
    moneyGiven: number;
    pawnProfit: number;
    saleProfit: number;
    totalProfit: number;
    pawnsOpened: number;
    pawnsExtended: number;
    pawnsRedeemed: number;
    salesCreated: number;
    salesSold: number;
    expensesRecorded: number;
    avgLoanSize: number;
    discrepancyCount: number;
    discrepancyTotal: number;
    underpricedSales: number;
    underpaidRedemptions: number;
    riskFlags: number;
}

// GET /admin/staff/{id} -> StaffDetailedResponse.
export interface StaffDetailed {
    id: number;
    fullName: string;
    username: string;
    nationalId: string;
    phonePrimary: string;
    phoneSecondary: string | null;
    baseSalary: number;
    bonusPercent: number;
    bonusAmount: number;
    totalCompensation: number;
    status: StaffStatus;
    managerId: number | null;
    roleGrants: StaffRoleGrant[];
    createdAt: string;
    updatedAt: string;
    deletedAt: string | null;
}
