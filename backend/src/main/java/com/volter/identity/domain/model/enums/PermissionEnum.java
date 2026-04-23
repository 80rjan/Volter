package com.volter.identity.domain.model.enums;

import lombok.Getter;

@Getter
public enum PermissionEnum {
    PAWN_CREATE("pawn:create"),
    PAWN_MODIFY("pawn:modify"),
    PAWN_RENEW("pawn:renew"),
    PAWN_REDEEM("pawn:redeem"),
    PAWN_FORFEIT("pawn:forfeit"),

    SALE_CREATE("sale:create"),
    SALE_MODIFY("sale:modify"),
    SALE_PURCHASE("sale:purchase"),
    SALE_SELL("sale:sell"),

    EXPENSE_CREATE("expense:create"),
    EXPENSE_READ("expense:read"),

    REPORT_READ("report:read"),

    CASH_REGISTER_SESSION_OPEN("cashregsession:open"),
    CASH_REGISTER_SESSION_CLOSE("cashregsession:close"),
    CASH_REGISTER_WITHDRAW("cashreg:withdraw"),
    CASH_REGISTER_DEPOSIT("cashreg:deposit"),

    STAFF_MANAGEMENT("staff:management"),
    STAFF_READ("staff:read"),

    ;

    private final String permission;

    PermissionEnum(String permission) {
        this.permission = permission;
    }

}
