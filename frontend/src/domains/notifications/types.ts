export type NotificationType = "RISK_FLAG" | "CASH_REGISTER_SESSION_DISCREPANCY" | "PAWN_UPDATED" | "SYSTEM";

// GET /notifications -> NotificationResponse
export interface NotificationRow {
    id: number;
    recipientStaffId: number;
    shopId: number | null;
    type: NotificationType;
    title: string;
    description: string | null;
    entityType: string | null;
    entityId: number | null;
    read: boolean;
    readAt: string | null;
    createdAt: string;
}

// GET /notifications/{id}/detail -> NotificationDetailResponse
export interface NotificationDetail {
    notification: NotificationRow;
    entityKind: "PAWN" | "SALE" | null;
    entityId: number | null;
}

export const NOTIFICATION_TYPE_LABEL: Record<NotificationType, string> = {
    RISK_FLAG: "Ризик",
    CASH_REGISTER_SESSION_DISCREPANCY: "Отстапување во каса",
    PAWN_UPDATED: "Ажуриран залог",
    SYSTEM: "Системско",
};
