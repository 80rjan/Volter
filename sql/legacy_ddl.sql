create table cash_register
(
    num_pawns        bigint                   default 0 not null primary key,
    money_pawns      bigint                   default 0,
    num_sale_items   bigint                   default 0,
    money_sale_items bigint                   default 0,
    register_money   bigint                   default 0,
    last_updated     timestamp with time zone default CURRENT_TIMESTAMP,
    gold_grams       numeric(7, 3),
    total_provision  bigint,
    shop_id          integer
);

create table client
(
    id          bigint generated always as identity (start with 1 minvalue 0) primary key,
    name        varchar(100),
    embg        char(13),
    telephone   char(20),
    city        varchar(20),
    telephone_2 char(20),
    date_joined timestamp with time zone,
    constraint unique_client
        unique (name, embg, telephone)
);

create table electronics_pawn
(
    id              integer generated always as identity primary key,
    client_id       integer not null
        references client
        constraint fkelectronicspawnclient
            references client
            on delete cascade,
    brand           varchar(50),
    year            integer,
    price_pawned    bigint,
    price_to_redeem bigint,
    provision       bigint,
    date_from       date,
    date_to         date,
    total_days      integer,
    description     varchar(300),
    shop_id         integer
);

create table gold_pawn
(
    id              integer generated always as identity primary key,
    client_id       integer not null
        constraint fkgoldpawnclient
            references client
            on delete cascade
        references client,
    weight          numeric(7, 3),
    carats          integer,
    price_pawned    bigint,
    price_to_redeem bigint,
    provision       bigint,
    date_from       date,
    date_to         date,
    total_days      integer,
    description     varchar(300),
    type            varchar(300),
    shop_id         integer
);

create table monthly_report
(
    id                       integer generated always as identity primary key,
    year                     integer not null,
    month                    integer not null,
    money_given              bigint,
    money_got                bigint,
    total_turnover           bigint,
    total_pawns              integer,
    num_gold_pawns           integer,
    num_electronics_pawns    integer,
    num_vehicle_pawns        integer,
    num_watch_pawns          integer,
    num_other_pawns          integer,
    money_gold_pawns         bigint,
    money_electronics_pawns  bigint,
    money_vehicle_pawns      bigint,
    money_watch_pawns        bigint,
    money_other_pawns        bigint,
    total_sales              integer,
    money_sales              bigint,
    money_pawns              bigint,
    net_profit               bigint,
    gross_profit             bigint,
    profit_electronics_pawns bigint,
    profit_gold_pawns        bigint,
    profit_vehicle_pawns     bigint,
    profit_watch_pawns       bigint,
    profit_other_pawns       bigint,
    profit_sales             bigint,
    profit_pawns             bigint,
    shop_id                  integer,
    constraint unique_monthly_report
        unique (year, month, shop_id)
);

create table other_pawn
(
    id              integer generated always as identity primary key,
    client_id       integer not null
        constraint fkotherpawnclient
            references client
            on delete cascade
        references client,
    price_pawned    bigint,
    price_to_redeem bigint,
    provision       bigint,
    date_from       date,
    date_to         date,
    total_days      integer,
    description     varchar(300),
    shop_id         integer
);

create table sale
(
    id           integer generated always as identity primary key,
    price_bought bigint,
    date_from    date,
    description  varchar(300),
    shop_id      integer
);

create table transaction
(
    id          bigint generated always as identity primary key,
    client_id   bigint not null
        constraint fktransactionclient
            references client
            on delete cascade
        references client,
    money_given bigint,
    money_got   bigint,
    profit      bigint,
    date        timestamp with time zone,
    category    varchar(20),
    description varchar(200),
    shop_id     integer,
    money_diff  bigint
);

create table vehicle_pawn
(
    id              integer generated always as identity primary key,
    client_id       integer not null
        constraint fkvehiclepawnclient
            references client
            on delete cascade
        references client,
    brand           varchar(50),
    model           varchar(50),
    year            integer,
    price_pawned    bigint,
    price_to_redeem bigint,
    provision       bigint,
    date_from       date,
    date_to         date,
    total_days      integer,
    description     varchar(300),
    shop_id         integer
);

create table watch_pawn
(
    id              integer generated always as identity primary key,
    client_id       integer not null
        constraint fkwatchpawnclient
            references client
            on delete cascade
        references client,
    brand           varchar(50),
    year            integer,
    price_pawned    bigint,
    price_to_redeem bigint,
    provision       bigint,
    date_from       date,
    date_to         date,
    total_days      integer,
    description     varchar(300),
    shop_id         integer
);

create table expense
(
    year        integer,
    month       integer,
    rent        bigint,
    salaries    bigint,
    other       bigint,
    description varchar(300),
    bills       bigint,
    shop_id     integer
);

create table shop
(
    id       bigint not null primary key,
    name     varchar(100),
    city     varchar(20),
    location varchar(20)
);


