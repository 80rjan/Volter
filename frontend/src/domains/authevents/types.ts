export type AuthEventType = "LOGIN_SUCCESS" | "LOGIN_FAILURE" | "LOGOUT";

// GET /admin/auth-events -> PageResponse<AuthEventResponse>
export interface AuthEvent {
    id: number;
    staffId: number;
    type: AuthEventType;
    ipAddress: string | null;
    userAgent: string | null;
    occurredAt: string;
}
