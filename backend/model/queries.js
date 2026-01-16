const pool = require('./pool');

const SHOP_ID = process.env.VITE_DB_SHOP_ID;
console.log("Shop id: ", SHOP_ID)

function getUTCDateNow() {
    return new Date(Date.now() - new Date().getTimezoneOffset() * 60000);
}

async function getAllPawns(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "", searchByCategory = "") {
    const client = await pool.connect()

    try {
        const {rows: summaryRows} = await client.query(`
        SELECT COUNT(*)                      AS "Num Pawns",
               COALESCE(SUM("Item Cost"), 0) AS "Money Pawns",
               COALESCE(SUM("Provision"), 0) AS "Provision"
        FROM (SELECT ep.price_pawned AS "Item Cost", ep.provision AS "Provision"
              FROM client c
                       INNER JOIN electronics_pawn ep ON c.id = ep.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Electronics')
                AND ep.shop_id = $5

              UNION ALL

              SELECT gp.price_pawned AS "Item Cost", gp.provision AS "Provision"
              FROM client c
                       INNER JOIN gold_pawn gp
                                  ON c.id = gp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Gold')
                AND gp.shop_id = $5

              UNION ALL

              SELECT op.price_pawned AS "Item Cost", op.provision AS "Provision"
              FROM client c
                       INNER JOIN other_pawn op
                                  ON c.id = op.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Other')
                AND op.shop_id = $5

              UNION ALL

              SELECT vp.price_pawned AS "Item Cost", vp.provision AS "Provision"
              FROM client c
                       INNER JOIN vehicle_pawn vp
                                  ON c.id = vp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Vehicle')
                AND vp.shop_id = $5

              UNION ALL

              SELECT wp.price_pawned AS "Item Cost", wp.provision AS "Provision"
              FROM client c
                       INNER JOIN watch_pawn wp
                                  ON c.id = wp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Watch')
                AND wp.shop_id = $5) as allPawns;
    `, [searchByName, searchByEmbg, searchByTel, searchByCategory, SHOP_ID]);


        const {rows: pawnRows} = await client.query(`
        SELECT *
        FROM (SELECT c.id                                                AS "Client Id",
                     c.name                                              AS "Name",
                     'Electronics'                                       AS "Category",
                     ep.id                                               AS "Id",
                     ep.brand || ' ' || ep.year || ' ' || ep.description AS "About",
                     to_char(ep.date_to, 'YYYY-MM-DD')                   AS "Valid Until",
                     ep.date_to - CURRENT_DATE                           AS "Days Left",
                     ep.provision                                        AS "Provision",
                     ep.price_pawned                                     AS "Item Cost",
                     ep.total_days                                       AS "Total Days"
              FROM client c
                       INNER JOIN electronics_pawn ep
                                  ON c.id = ep.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Electronics')
                AND ep.shop_id = $5

              UNION ALL

              SELECT c.id                                                                                    AS "Client Id",
                     c.name                                                                                  AS "Name",
                     'Gold'                                                                                  AS "Category",
                     gp.id                                                                                   AS "Id",
                     gp.weight::FLOAT::TEXT || 'g ' || gp.carats || 'k ' || gp.type || ' ' || gp.description AS "About",
                     to_char(gp.date_to, 'YYYY-MM-DD')                                                       AS "Valid Until",
                     gp.date_to - CURRENT_DATE                                                               AS "Days Left",
                     gp.provision                                                                            AS "Provision",
                     gp.price_pawned                                                                         AS "Item Cost",
                     gp.total_days                                                                           AS "Total Days"
              FROM client c
                       INNER JOIN gold_pawn gp
                                  ON c.id = gp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Gold')
                AND gp.shop_id = $5

              UNION ALL

              SELECT c.id                              AS "Client Id",
                     c.name                            AS "Name",
                     'Other'                           AS "Category",
                     op.id                             AS "Id",
                     op.description                    AS "About",
                     to_char(op.date_to, 'YYYY-MM-DD') AS "Valid Until",
                     op.date_to - CURRENT_DATE         AS "Days Left",
                     op.provision                      AS "Provision",
                     op.price_pawned                   AS "Item Cost",
                     op.total_days                     AS "Total Days"
              FROM client c
                       INNER JOIN other_pawn op
                                  ON c.id = op.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Other')
                AND op.shop_id = $5

              UNION ALL

              SELECT c.id                                                                   AS "Client Id",
                     c.name                                                                 AS "Name",
                     'Vehicle'                                                              AS "Category",
                     vp.id                                                                  AS "Id",
                     vp.brand || ' ' || vp.model || ' ' || vp.year || ' ' || vp.description AS "About",
                     to_char(vp.date_to, 'YYYY-MM-DD')                                      AS "Valid Until",
                     vp.date_to - CURRENT_DATE                                              AS "Days Left",
                     vp.provision                                                           AS "Provision",
                     vp.price_pawned                                                        AS "Item Cost",
                     vp.total_days                                                          AS "Total Days"
              FROM client c
                       INNER JOIN vehicle_pawn vp
                                  ON c.id = vp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Vehicle')
                AND vp.shop_id = $5

              UNION ALL

              SELECT c.id                                                 AS "Client Id",
                     c.name                                               AS "Name",
                     'Watch'                                              AS "Category",
                     wp.id                                                AS "Id",
                     wp.brand || ' ' || wp.year || ', ' || wp.description AS "About",
                     to_char(wp.date_to, 'YYYY-MM-DD')                    AS "Valid Until",
                     wp.date_to - CURRENT_DATE                            AS "Days Left",
                     wp.provision                                         AS "Provision",
                     wp.price_pawned                                      AS "Item Cost",
                     wp.total_days                                        AS "Total Days"
              FROM client c
                       INNER JOIN watch_pawn wp
                                  ON c.id = wp.client_id
              WHERE LOWER(c.name) LIKE $1 || '%'
                AND c.embg LIKE $2 || '%'
                AND (c.telephone LIKE $3 || '%'
                  OR c.telephone_2 LIKE $3 || '%')
                AND ($4 = '' OR $4 = 'Watch')
                AND wp.shop_id = $5
              )
                 AS pawns
        ORDER BY "${orderBy}" ${orderDirection}, "Category" ${orderDirection}, "Id" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel, searchByCategory, SHOP_ID]);


        return {pawns: pawnRows, summary: summaryRows[0]};
    } catch (error) {
        throw error;
    } finally {
        client.release();
    }
}

async function getPawn(clientId, category, pawnId) {
    let pawnTable = "";
    switch (category) {
        case "Electronics":
            pawnTable = "electronics_pawn";
            break;
        case "Gold":
            pawnTable = "gold_pawn";
            break;
        case "Vehicle":
            pawnTable = "vehicle_pawn";
            break;
        case "Watch":
            pawnTable = "watch_pawn";
            break;
        case "Other":
            pawnTable = "other_pawn";
            break;
    }

    const {rows: pawnRows} = await pool.query(`SELECT *,
                                                      to_char(date_from, 'YYYY-MM-DD') AS date_from,
                                                      to_char(date_to, 'YYYY-MM-DD')   AS date_to
                                               FROM ${pawnTable}
                                               WHERE id = $1`, [pawnId]);
    const pawn = pawnRows[0];
    const {rows: clientRows} = await pool.query(`SELECT *
                                                 FROM client
                                                 WHERE id = $1`, [clientId]);
    const client = clientRows[0];
    return {pawn, client};
}

async function closePawn(id, tableName, priceClosed, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
        if (!validTables.includes(tableName)) {
            throw new Error("Invalid table name in REMOVE PAWN");
        }

        const query = await client.query(`SELECT provision,
                                                 price_pawned,
                                                 client_id,
                                                 date_to - CURRENT_DATE as days_left,
                                                 ${tableName === 'gold_pawn' ? 'weight' : '0 as weight'}
                                          FROM ${tableName}
                                          WHERE id = $1;`, [id])
        let gold_grams = 0;
        let provision = null;
        let pawnMoney = null;
        let clientId = null;
        let isExpired = false;
        if (query.rows.length > 0) {
            provision = query.rows[0].provision;
            pawnMoney = query.rows[0].price_pawned;
            clientId = query.rows[0].client_id;
            gold_grams = Number(query.rows[0]?.weight);
            isExpired = query.rows[0].days_left < 0;
        } else
            throw new Error(`Pawn with id ${id} not found in table ${tableName} to REMOVE PAWN`);

        //Delete pawn from pawn table
        await client.query(`
            DELETE
            FROM ${tableName}
            WHERE id = $1;
        `, [id])


        let transactionCategory = tableName === "electronics_pawn" ? "Electronics" :
            tableName === "gold_pawn" ? "Gold" :
                tableName === "vehicle_pawn" ? "Vehicle" :
                    tableName === "watch_pawn" ? "Watch" : "Other";
        let transactionDescription = `Затворен залог. ${description}`;

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with cash inserted and profit made
        const priceDiff = priceClosed - (+pawnMoney + +provision + (isExpired ? +provision : 0));
        let finalDescription = transactionDescription
        if (priceDiff < 0) {
            if (isExpired)
                if (priceDiff <= -(+provision * 2))
                    finalDescription += '\n Недостасува провизија и казна за залог кој го надминал дозволениот рок.';
                else
                    finalDescription += '\n Недостасува казна за залог кој го надминал дозволениот рок.';
            else
                finalDescription += '\n Недостасува провизија за залог.';
        }
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES ($1, $2, $3, 0, $4, $5, $6, $7, $8);
        `, [clientId, transactionCategory, finalDescription, pawnMoney, priceClosed - pawnMoney, priceDiff, UTC_TIME, SHOP_ID])

        //Update cash register with money inserted, decrease numPawns, decrease moneyPawns, update quantity of gold in grams and update total provision for pawns
        await client.query(`
            UPDATE cash_register
            SET total_provision = total_provision - $4,
                num_pawns       = num_pawns - 1,
                money_pawns     = money_pawns - $1,
                register_money  = register_money + $2,
                gold_grams      = gold_grams - $3,
                last_updated    = $5
            WHERE shop_id = $6;
        `, [pawnMoney, priceClosed, gold_grams, provision, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function continuePawn(id, tableName, provision, description, carryOverDays) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
        if (!validTables.includes(tableName)) {
            throw new Error("Invalid table name in CONTINUE PAWN");
        }

        let query = await client.query(`SELECT client_id, provision
                                        FROM ${tableName}
                                        WHERE id = $1;`, [id])
        let clientId = null;
        let priceDiff = null;
        if (query.rows.length > 0) {
            clientId = query.rows[0].client_id;
            priceDiff = +provision - +query.rows[0].provision;
        } else
            throw new Error(`Pawn with id ${id} not found in ${tableName} to CONTINUE PAWN`);

        //Update table with new date until pawn is valid
        // total_days + daysleft
        // SET date_to = date_to + total_days*INTERVAL '1 day'
        await client.query(`
            UPDATE ${tableName}
            SET date_to =
                    CASE
                        WHEN date_to > CURRENT_DATE THEN date_to + (total_days + $2) * INTERVAL '1 day'
                        ELSE CURRENT_DATE + (total_days + $2) * INTERVAL '1 day'
                        END
            WHERE id = $1;
        `, [id, carryOverDays])


        let transactionCategory = tableName === "electronics_pawn" ? "Electronics" :
            tableName === "gold_pawn" ? "Gold" :
                tableName === "vehicle_pawn" ? "Vehicle" :
                    tableName === "watch_pawn" ? "Watch" : "Other";
        let transactionDescription = `Продолжен залог. ${description}`;
        if (priceDiff < 0)
            transactionDescription += "\n Залогот е продолжен со износ помал од очекуваниот."

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with profit made
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES ($1, $2, $3, 0, 0, $4, $5, $6, $7);
        `, [clientId, transactionCategory, transactionDescription, provision, priceDiff, UTC_TIME, SHOP_ID])

        //Update cash register with money inserted
        await client.query(`
            UPDATE cash_register
            SET register_money = register_money + $1,
                last_updated   = $2
            WHERE shop_id = $3;
        `, [provision, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function addNewPawn(pawnCategory, pawnObj, clientObj) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
        if (!validTables.includes(pawnCategory)) {
            throw new Error("Invalid pawn category in ADD NEW PAWN");
        }
        let gold_grams = 0;
        if (pawnCategory === "gold_pawn")
            gold_grams = pawnObj.weight;


        const clientCheck = await client.query(
            `SELECT id
             FROM client
             WHERE name = $1
               AND embg = $2;`,
            [clientObj.name, clientObj.embg]
        );

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        let clientId;
        if (clientCheck.rows.length > 0) {
            // Client found → update telephones
            clientId = clientCheck.rows[0].id;
            await client.query(
                `UPDATE client
                 SET telephone   = $1,
                     telephone_2 = $2
                 WHERE id = $3;`,
                [clientObj.telephone, clientObj.telephone_2, clientId]
            );
        } else {
            // Insert new client since no match by name & embg
            const insertRes = await client.query(
                `INSERT INTO client (name, embg, telephone, telephone_2, city, date_joined)
                 VALUES ($1, $2, $3, $4, $5, $6)
                 RETURNING id;`,
                [clientObj.name, clientObj.embg, clientObj.telephone, clientObj.telephone_2, clientObj.city, UTC_TIME]
            );
            clientId = insertRes.rows[0].id;
        }

        //Insert pawn in pawn table
        switch (pawnCategory) {
            case 'electronics_pawn':
                await client.query(`
                    INSERT INTO electronics_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision,
                                                  date_from, date_to, total_days, description, shop_id)
                    VALUES ($1, $2, $3, $4, $5, $6, $7::DATE, $7::DATE + $8 * INTERVAL '1 day', $8, $9, $10);
                `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date, pawnObj.total_days, pawnObj.description, SHOP_ID]);
                break;
            case 'gold_pawn':
                await client.query(`
                    INSERT INTO gold_pawn (client_id, weight, carats, price_pawned, price_to_redeem, provision,
                                           date_from,
                                           date_to, total_days, description, type, shop_id)
                    VALUES ($1, $2, $3, $4, $5, $6, $7::DATE, $7::DATE + $8 * INTERVAL '1 day', $8, $9, $10, $11);
                `, [clientId, pawnObj.weight, pawnObj.carats, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date, pawnObj.total_days, pawnObj.description, pawnObj.type, SHOP_ID]);
                break;
            case 'other_pawn':
                await client.query(`
                    INSERT INTO other_pawn (client_id, price_pawned, price_to_redeem, provision, date_from, date_to,
                                            total_days, description, shop_id)
                    VALUES ($1, $2, $3, $4, $5::DATE, $5::DATE + $6 * INTERVAL '1 day', $6, $7, $8);
                `, [clientId, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date, pawnObj.total_days, pawnObj.description, SHOP_ID]);
                break;
            case 'vehicle_pawn':
                await client.query(`
                    INSERT INTO vehicle_pawn (client_id, brand, model, year, price_pawned, price_to_redeem, provision,
                                              date_from, date_to, total_days, description, shop_id)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8::DATE, $8::DATE + $9 * INTERVAL '1 day', $9, $10, $11);
                `, [clientId, pawnObj.brand, pawnObj.model, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date, pawnObj.total_days, pawnObj.description, SHOP_ID]);
                break;
            case 'watch_pawn':
                await client.query(`
                    INSERT INTO watch_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from,
                                            date_to, total_days, description, shop_id)
                    VALUES ($1, $2, $3, $4, $5, $6, $7::DATE, $7::DATE + $8 * INTERVAL '1 day', $8, $9, $10);
                `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date, pawnObj.total_days, pawnObj.description, SHOP_ID]);
                break;
            default:
                throw new Error("Invalid pawn table name to ADD PAWN");
        }

        let transactionCategory = pawnCategory === "electronics_pawn" ? "Electronics" :
            pawnCategory === "gold_pawn" ? "Gold" :
                pawnCategory === "vehicle_pawn" ? "Vehicle" :
                    pawnCategory === "watch_pawn" ? "Watch" : "Other";
        let transactionDescription = "Added new pawn";

        //Insert a new transaction where money is given to client for pawn
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES ($1, $2, $3, $4, 0, 0, 0, $5::DATE, $6);
        `, [clientId, transactionCategory, transactionDescription, pawnObj.price_pawned, pawnObj.date, SHOP_ID])

        //Update cash register with money taken, increase numPawns, increase moneyPawns, update quantity of gold in grams and update total provision for pawns
        await client.query(`
            UPDATE cash_register
            SET total_provision = total_provision + $3,
                num_pawns       = num_pawns + 1,
                money_pawns     = money_pawns + $1,
                register_money  = register_money - $1,
                gold_grams      = gold_grams + $2,
                last_updated    = $4
            WHERE shop_id = $5;
        `, [pawnObj.price_pawned, gold_grams, pawnObj.provision, UTC_TIME, SHOP_ID]);

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function updatePawn(pawnTable, pawnId, pricePawned, provision, description, goldGramsDiff, totalDays) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        let oldPawn = null
        try {
            const {rows} = await client.query(`SELECT price_pawned, client_id, provision, total_days
                                               FROM ${pawnTable}
                                               WHERE id = $1;`, [pawnId]);
            oldPawn = rows
        } catch (error) {
            console.error("Error executing query old pawn: " + error)
            throw new Error("Failed to fetch old pawn data")
        }
        const clientId = oldPawn[0].client_id;
        const oldPrice = parseInt(oldPawn[0].price_pawned);
        const oldProvision = parseInt(oldPawn[0].provision)
        const oldTotalDays = parseInt(oldPawn[0].total_days)
        let category = ''
        switch (pawnTable) {
            case "electronics_pawn":
                category = "Electronics";
                break;
            case "gold_pawn":
                category = "Gold";
                break;
            case "vehicle_pawn":
                category = "Vehicle";
                break;
            case "watch_pawn":
                category = "Watch";
                break;
            case "other_pawn":
                category = "Other";
                break;
        }


        let s = `Промена! `
        if (oldPrice !== pricePawned)
            s += `Цена: ${Number(oldPrice).toLocaleString("de-DE")} во ${Number(pricePawned).toLocaleString("de-DE")}. `
        if (oldProvision !== provision)
            s += `Месечна провизија: ${Number(oldProvision).toLocaleString("de-DE")} во ${Number(provision).toLocaleString("de-DE")}. `
        if (oldTotalDays !== totalDays)
            s += `Времетраење на залогот во денови: ${oldTotalDays} во ${totalDays}. `
        if (goldGramsDiff !== 0)
            s += `Злато додадено: ${Number(goldGramsDiff).toLocaleString("de-DE")}гр. `

        let query = `
            UPDATE ${pawnTable}
            SET price_pawned    = CAST($2 AS NUMERIC),
                price_to_redeem = CAST($2 AS NUMERIC) + CAST($3 AS NUMERIC),
                provision       = CAST($3 AS NUMERIC),
                description     = $4,
                total_days      = $5,
                date_to         = date_to + $6 * INTERVAL '1 day'
        `;
        const params = [pawnId, pricePawned, provision, description, totalDays, totalDays - oldTotalDays];

        if (pawnTable === "gold_pawn") {
            query += `, weight = weight + CAST($7 AS NUMERIC)`;
            params.push(goldGramsDiff)
        }

        query += ` WHERE id = $1 RETURNING *;`;

        let result = null
        try {
            const {rows} = await client.query(query, params);
            result = rows[0];
        } catch (error) {
            console.error("Error executing query: ", error);
            throw error;
        }

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with money given
        try {
            await client.query(`
                INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff,
                                         date,
                                         shop_id)
                VALUES ($1, $2, $3, $4, 0, 0, 0, $5, $6);
            `, [clientId, category, s, pricePawned - oldPrice, UTC_TIME, SHOP_ID])
        } catch (error) {
            console.error("Error executing query transaction: " + error)
            throw new Error("Failed to modify transactions")
        }

        //Update cash register with balance of money in pawns, average percent, money removed
        try {
            await client.query(`
                UPDATE cash_register
                SET register_money  = register_money - $1,
                    money_pawns     = money_pawns + $1,
                    total_provision = total_provision + $2,
                    gold_grams      = gold_grams + $3,
                    last_updated    = $4
                WHERE shop_id = $5;
            `, [pricePawned - oldPrice, provision - oldProvision, goldGramsDiff, UTC_TIME, SHOP_ID])
        } catch (error) {
            console.error("Error executing query cash reg: " + error)
            throw new Error("Failed to modify cash reg")
        }


        await client.query("COMMIT");
        return result;
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}


async function changePawnToSale(id, pawnCategory) {
    const client = await pool.connect();

    try {
        await client.query('BEGIN');

        const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
        if (!validTables.includes(pawnCategory))
            throw new Error("Invalid pawn category in CHANGE PAWN TO SALE");

        let gold_grams = 0;
        let description = null;
        let provision = null;
        let clientId = null;
        let priceBought = null;
        let query;
        switch (pawnCategory) {
            case "electronics_pawn":
                query = await client.query(`SELECT brand, year, description, provision, client_id, price_pawned
                                            FROM electronics_pawn
                                            WHERE id = $1;`, [id])

                if (query.rows.length === 0) {
                    throw new Error(`No pawn found in ${pawnCategory} with id ${id}`);
                }

                description = `${query.rows[0].brand}, ${query.rows[0].year}, ${query.rows[0].description}`;
                provision = query.rows[0].provision;
                clientId = query.rows[0].client_id;
                priceBought = query.rows[0].price_pawned;
                break;
            case "gold_pawn":
                query = await client.query(`SELECT price_pawned,
                                                   weight,
                                                   carats,
                                                   description,
                                                   provision,
                                                   client_id,
                                                   price_pawned
                                            FROM gold_pawn
                                            WHERE id = $1;`, [id])

                if (query.rows.length === 0) {
                    throw new Error(`No pawn found in ${pawnCategory} with id ${id}`);
                }

                gold_grams = query.rows[0].weight;
                description = `${Number.isInteger(Number(query.rows[0].weight)) ? Number(query.rows[0].weight).toFixed(0) : query.rows[0].weight}g, ${query.rows[0].carats}k, ${Math.round(Number(query.rows[0].price_pawned / query.rows[0].weight))} по грам, ${query.rows[0].description}`;
                provision = query.rows[0].provision;
                clientId = query.rows[0].client_id;
                priceBought = query.rows[0].price_pawned;
                break;
            case "other_pawn":
                query = await client.query(`SELECT description, provision, client_id, price_pawned
                                            FROM other_pawn
                                            WHERE id = $1;`, [id])

                if (query.rows.length === 0) {
                    throw new Error(`No pawn found in ${pawnCategory} with id ${id}`);
                }

                description = query.rows[0].description;
                provision = query.rows[0].provision;
                clientId = query.rows[0].client_id;
                priceBought = query.rows[0].price_pawned;
                break;
            case "vehicle_pawn":
                query = await client.query(`SELECT brand, model, year, description, provision, client_id, price_pawned
                                            FROM vehicle_pawn
                                            WHERE id = $1;`, [id])

                if (query.rows.length === 0) {
                    throw new Error(`No pawn found in ${pawnCategory} with id ${id}`);
                }

                description = `${query.rows[0].brand} ${query.rows[0].model}, ${query.rows[0].year}, ${query.rows[0].description}`;
                provision = query.rows[0].provision;
                clientId = query.rows[0].client_id;
                priceBought = query.rows[0].price_pawned;
                break;
            case "watch_pawn":
                query = await client.query(`SELECT brand, year, description, provision, client_id, price_pawned
                                            FROM watch_pawn
                                            WHERE id = $1;`, [id])

                if (query.rows.length === 0) {
                    throw new Error(`No pawn found in ${pawnCategory} with id ${id}`);
                }

                description = `${query.rows[0].brand}, ${query.rows[0].year}, ${query.rows[0].description}`;
                provision = query.rows[0].provision;
                clientId = query.rows[0].client_id;
                priceBought = query.rows[0].price_pawned;
                break;
            default:
                throw new Error("Invalid pawn category in ADD SALE");
        }

        if (!provision || !clientId || !priceBought) {
            throw new Error("Cant find information in pawn table to CHANGE PAWN TO SALE");
        }

        //Insert sale into sale table
        await client.query(`
            INSERT INTO sale (price_bought, date_from, description, shop_id)
            VALUES ($1, CURRENT_DATE, $2, $3)
        `, [priceBought, description, SHOP_ID])

        //Delete pawn from pawn table
        await client.query(`
            DELETE
            FROM ${pawnCategory}
            WHERE id = $1;
        `, [id])

        let transactionCategory = pawnCategory === "electronics_pawn" ? "Electronics" :
            pawnCategory === "gold_pawn" ? "Gold" :
                pawnCategory === "vehicle_pawn" ? "Vehicle" :
                    pawnCategory === "watch_pawn" ? "Watch" : "Other";
        let transactionDescription = "Transferred pawn to sale";

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with profit made
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES ($1, $2, $3, 0, 0, 0, 0, $4, $5);
        `, [clientId, transactionCategory, transactionDescription, UTC_TIME, SHOP_ID])

        //Update cash register with decrease numPawns, decrease moneyPawns, increase numSales, increase moneySales, update quantity of gold in grams and update total provision for pawns
        await client.query(`
            UPDATE cash_register
            SET total_provision  = total_provision - $3,
                num_pawns        = num_pawns - 1,
                money_pawns      = money_pawns - $1,
                num_sale_items   = num_sale_items + 1,
                money_sale_items = money_sale_items + $1,
                gold_grams       = gold_grams - $2,
                last_updated     = $4
            WHERE shop_id = $5;
        `, [priceBought, gold_grams, provision, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function getAllSales(limit, offset, orderBy, orderDirection) {
    const {rows} = await pool.query(`
        SELECT s.id                               AS "Id",
               s.description                      AS "About",
               to_char(s.date_from, 'YYYY-MM-DD') AS "Date Bought",
               s.price_bought                     AS "Item Cost"
        FROM sale s
        WHERE s.shop_id = $1
        ORDER BY "${orderBy}" ${orderDirection}, "Id" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [SHOP_ID])

    return rows;
}

async function getSale(saleId) {

    const {rows} = await pool.query(`SELECT *, to_char(date_from, 'YYYY-MM-DD') AS date_from
                                     FROM sale
                                     WHERE id = $1`, [saleId]);
    return rows[0];
}

async function closeSale(id, priceSold, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        const query = await client.query(`SELECT price_bought
                                          FROM sale
                                          WHERE id = $1;`, [id]);
        let priceBought = null;
        if (query.rows.length > 0) {
            priceBought = query.rows[0].price_bought;
        } else
            throw new Error(`Sale with id ${id} not found in sale table to CLOSE SALE`);


        //Delete sale from sale table
        await client.query(`
            DELETE
            FROM sale
            WHERE id = $1;
        `, [id])

        let transactionCategory = "Sale";
        let transactionDescription = `Затворена продажба. ${description}`;

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with cash inserted and profit made
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES (0, $1, $2, 0, $3, $4, 0, $5, $6);
        `, [transactionCategory, transactionDescription, priceBought, priceSold - priceBought, UTC_TIME, SHOP_ID])

        //Update cash register with money inserted, decrease numSales and decrease moneySales
        await client.query(`
            UPDATE cash_register
            SET num_sale_items   = num_sale_items - 1,
                money_sale_items = money_sale_items - $1,
                register_money   = register_money + $2,
                last_updated     = $3
            WHERE shop_id = $4;
        `, [priceBought, priceSold, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
        return priceSold;
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function addNewSale(priceBought, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        //Insert a new sale into sale table
        await client.query(`
            INSERT INTO sale (price_bought, date_from, description, shop_id)
            VALUES ($1, CURRENT_DATE, $2, $3)
        `, [priceBought, description, SHOP_ID])

        let transactionCategory = "Sale";
        let transactionDescription = "Added new sale";

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        //Insert a new transaction with money given
        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES (0, $1, $2, $3, 0, 0, 0, $4, $5);
        `, [transactionCategory, transactionDescription, priceBought, UTC_TIME, SHOP_ID])

        //Update cash register with money given, increase numSales and increase moneySales
        await client.query(`
            UPDATE cash_register
            SET num_sale_items   = num_sale_items + 1,
                money_sale_items = money_sale_items + $1,
                register_money   = register_money - $1,
                last_updated     = $2
            WHERE shop_id = $3;
        `, [priceBought, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}


async function getAllClientsAutocomplete(limit, offset, search) {

    const {rows} = await pool.query(`
        SELECT *
        FROM client
        WHERE LOWER(name) LIKE $1
           OR embg LIKE $1
           OR telephone LIKE $1
           OR telephone_2 LIKE $1
        ORDER BY id
        LIMIT ${limit} OFFSET ${offset};
    `, [`${search}%`]);
    return rows;
}

async function getAllClients(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "") {
    // Active pawns, money pawns, money provision are calculated for users with shop_id = SHOP_ID (which is the current shop)
    // everything else is calculated for all users
    const {rows} = await pool.query(`
        SELECT c.id                                                  AS "Id",
               c.name                                                AS "Name",
               c.telephone                                           AS "Telephone 1",
               c.telephone_2                                         AS "Telephone 2",
               c.city                                                AS "City",
               c.embg                                                AS "Embg",
               c.date_joined                                         AS "Date Joined",
               COALESCE((SELECT COUNT(*)
                         FROM transaction t
                         WHERE t.client_id = c.id
                           AND t.description = 'Added new pawn'), 0) AS "Total Pawns",
               COALESCE((SELECT COUNT(*)
                         FROM (SELECT id
                               FROM gold_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT id
                               FROM electronics_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT id
                               FROM watch_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT id
                               FROM vehicle_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT id
                               FROM other_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4) AS all_pawns), 0) AS "Active Pawns",
               COALESCE((SELECT SUM(price_pawned)
                         FROM (SELECT price_pawned
                               FROM gold_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT price_pawned
                               FROM electronics_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT price_pawned
                               FROM watch_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT price_pawned
                               FROM vehicle_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT price_pawned
                               FROM other_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4) AS all_pawns), 0) AS "Money Pawns",
               COALESCE((SELECT SUM(provision)
                         FROM (SELECT provision
                               FROM gold_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT provision
                               FROM electronics_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT provision
                               FROM watch_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT provision
                               FROM vehicle_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4
                               UNION ALL
                               SELECT provision
                               FROM other_pawn
                               WHERE client_id = c.id
                                 AND shop_id = $4) AS all_pawns),
                        0)                                           AS "Money Provision"
        FROM client c
        WHERE c.name <> 'Admin'
          AND LOWER(c.name) LIKE $1 || '%'
          AND c.embg LIKE $2 || '%'
          AND (c.telephone LIKE $3 || '%'
            OR c.telephone_2 LIKE $3 || '%')
        ORDER BY "${orderBy}" ${orderDirection}, "Id" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel, SHOP_ID]);
    return rows;
}


async function getClientPawnsAndTransactions(clientId) {
    const {rows: pawns} = await pool.query(`
        SELECT 'Gold' AS category,
               gp.id,
               gp.weight::FLOAT::TEXT || 'g ' || gp.carats || 'k ' || gp.type || ' ' || gp.description AS description,
               gp.date_to,
               gp.date_from,
               gp.provision,
               gp.price_pawned,
               gp.date_to - CURRENT_DATE AS days_left,
               gp.total_days
        FROM gold_pawn gp
        WHERE gp.client_id = $1 AND gp.shop_id = $2

        UNION ALL

        SELECT 'Electronics' AS category,
               ep.id,
               ep.brand || ' ' || ep.year || ' ' || ep.description AS description,
               ep.date_to,
               ep.date_from,
               ep.provision,
               ep.price_pawned,
               ep.date_to - CURRENT_DATE AS days_left,
               ep.total_days
        FROM electronics_pawn ep
        WHERE ep.client_id = $1 AND ep.shop_id = $2

        UNION ALL

        SELECT 'Other' AS category,
               op.id,
               op.description,
               op.date_to,
               op.date_from,
               op.provision,
               op.price_pawned,
               op.date_to - CURRENT_DATE AS days_left,
               op.total_days
        FROM other_pawn op
        WHERE op.client_id = $1 AND op.shop_id = $2

        UNION ALL

        SELECT 'Vehicle' AS category,
               vp.id,
               vp.brand || ' ' || vp.model || ' ' || vp.year || ' ' || vp.description AS description,
               vp.date_to,
               vp.date_from,
               vp.provision,
               vp.price_pawned,
               vp.date_to - CURRENT_DATE AS days_left,
               vp.total_days
        FROM vehicle_pawn vp
        WHERE vp.client_id = $1 AND vp.shop_id = $2

        UNION ALL

        SELECT 'Watch' AS category,
               wp.id,
               wp.brand || ' ' || wp.year || ', ' || wp.description AS description,
               wp.date_to,
               wp.date_from,
               wp.provision,
               wp.price_pawned,
               wp.date_to - CURRENT_DATE AS days_left,
               wp.total_days
        FROM watch_pawn wp
        WHERE wp.client_id = $1 AND wp.shop_id = $2

        ORDER BY date_to DESC
    `, [clientId, SHOP_ID]);

    const {rows: transactions} = await pool.query(`
        SELECT *
        FROM transaction
        WHERE client_id = $1 AND shop_id = $2
        ORDER BY date DESC
    `, [clientId, SHOP_ID]);

    return {
        pawns,
        transactions
    };
}



async function getCashRegister() {
    const {rows} = await pool.query(`SELECT *
                                     FROM cash_register
                                     WHERE shop_id = $1`, [SHOP_ID]);

    const {rows: profit} = await pool.query(`
        SELECT SUM(profit) 
        FROM transaction
        WHERE date >= date_trunc('month', CURRENT_DATE)
          AND date < (date_trunc('month', CURRENT_DATE) + interval '1 month')
        AND shop_id = $1
    `, [SHOP_ID]);

    return {
        cashRegister: rows[0],
        profit: profit[0].sum
    };
}

async function insertIntoCashRegister(amount, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        await client.query(`
            UPDATE cash_register
            SET register_money = register_money + $1,
                last_updated   = $2
            WHERE shop_id = $3;
        `, [amount, UTC_TIME, SHOP_ID])

        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES (0, 'Insert', $1, 0, $2, 0, 0, $3, $4);
        `, [description, amount, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}

async function removeFromCashRegister(amount, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME = getUTCDateNow();

        await client.query(`
            UPDATE cash_register
            SET register_money = register_money - $1,
                last_updated   = $2
            WHERE shop_id = $3;
        `, [amount, UTC_TIME, SHOP_ID])

        await client.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, money_diff, date,
                                     shop_id)
            VALUES (0, 'Remove', $1, $2, 0, 0, 0, $3, $4);
        `, [description, amount, UTC_TIME, SHOP_ID])

        await client.query("COMMIT");
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}


async function getAllExpenses(limit, offset, orderBy, orderDirection, searchByMonth = "", searchByYear = "") {
    const {rows} = await pool.query(`
        SELECT e.year        AS "Year",
               e.month       AS "Month",
               e.rent        AS "Rent",
               e.salaries    AS "Salaries",
               e.bills       AS "Bills",
               e.other       AS "Other",
               e.description AS "Description"
        FROM expense e
        WHERE ($1 = '' OR e.year = $1::INT)
          AND ($2 = '' OR e.month = $2::INT)
          AND e.shop_id = $3
        ORDER BY ${orderBy} ${orderDirection}, "Year" ${orderDirection}, "Month" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByYear, searchByMonth, SHOP_ID])

    return rows;
}

async function getExpense(month, year) {
    // First, get expense
    const expenseResult = await pool.query(`
        SELECT e.year        AS "Year",
               e.month       AS "Month",
               e.rent        AS "Rent",
               e.salaries    AS "Salaries",
               e.bills       AS "Bills",
               e.other       AS "Other",
               e.description AS "Description"
        FROM expense e
        WHERE month = $1::INT
          AND year = $2::INT
          AND shop_id = $3;
    `, [month, year, SHOP_ID]);

    if (expenseResult.rows.length === 0) {
        throw new Error("Expense not found");
    }

    const expense = expenseResult.rows[0];

    // Get all transactions matching category 'Expense' and same month/year
    const transactionsResult = await pool.query(`
        SELECT t.date        AS "Date",
               t.description AS "Description",
               t.money_given AS "Money Given"
        FROM transaction t
        WHERE category = 'Expense'
          AND EXTRACT(MONTH FROM date) = $1
          AND EXTRACT(YEAR FROM date) = $2
          AND shop_id = $3
    `, [month, year, SHOP_ID]);

    // Return both
    return {
        expense,
        transactions: transactionsResult.rows
    };
}

async function insertExpense(year, month, rent, salaries, bills, other, description) {
    const client = await pool.connect();
    try {
        await client.query("BEGIN");

        const {rows: hasReport} = await client.query(`SELECT *
                                                      FROM monthly_report
                                                      WHERE year = $1::INT
                                                        AND month = $2::INT
                                                        AND shop_id = $3`, [year, month, SHOP_ID])

        if (hasReport.length > 0) {
            return `Не може да се внесе расход, бидејќи веќе е внесен месечен извештај за ${month}/${year}. За внес контактирајте со одржувачот на системот.`
        }

        const {rows: hasExpense} = await client.query(`SELECT *
                                                       FROM expense
                                                       WHERE year = $1::INT
                                                         AND month = $2::INT
                                                         AND shop_id = $3`, [year, month, SHOP_ID])
        if (hasExpense.length > 0)
            await client.query(`
                UPDATE expense
                SET rent        = rent + $3,
                    salaries    = salaries + $4,
                    bills       = bills + $5,
                    other       = other + $6,
                    description = $7
                WHERE year = $1::INT
                  AND month = $2::INT
                  AND shop_id = $8;
            `, [year, month, rent, salaries, bills, other, '', SHOP_ID]) // no description in expense, descriptions are kept in transactions
        else {
            await client.query(`
                INSERT INTO expense (year, month, rent, salaries, bills, other, description, shop_id)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
            `, [year, month, rent, salaries, bills, other, '', SHOP_ID]) // no description in expense, descriptions are kept in transactions
        }

        const today = new Date();
        let expenseDate;
        if (today.getFullYear() === year && (today.getMonth() + 1) === month) {
            // Same year and month as today → use full today’s date (including day & time)
            expenseDate = today;
        } else {
            // Different month/year → use the 1st day of that month/year
            expenseDate = new Date(year, month, 0);
        }
        // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
        const UTC_TIME_EXPENSE_DATE = new Date(expenseDate.getTime() - (expenseDate.getTimezoneOffset() * 60000));

        //Insert a new transaction with cash inserted and profit made
        const transactions = [
            {label: 'Кирија', value: rent},
            {label: 'Плати', value: salaries},
            {label: 'Сметки', value: bills},
            {label: 'Останато', value: other}
        ];

        for (const {label, value} of transactions) {
            if (value && value !== 0) {
                let transactionDescription = `${month}/${year}. Расход за ${label}.`;
                if (description && description.trim() !== '' && transactionDescription.length + description.length + " Опис: .".length < 200) {
                    transactionDescription += ` Опис: ${description}.`;
                }
                await client.query(`
                    INSERT INTO transaction (client_id, category, description, money_given, money_got, profit,
                                             money_diff, date,
                                             shop_id)
                    VALUES (0, 'Expense', $1, $2, 0, 0, 0, $3, $4)
                `, [transactionDescription, value, UTC_TIME_EXPENSE_DATE, SHOP_ID]);
            }
        }


        if (rent + salaries + bills + other !== 0) {
            const UTC_TIME_TODAY = new Date(today.getTime() - (today.getTimezoneOffset() * 60000));
            await client.query(`
                UPDATE cash_register
                SET register_money = register_money - $1,
                    last_updated   = $2
                WHERE shop_id = $3;
            `, [rent + salaries + bills + other, UTC_TIME_TODAY, SHOP_ID])
        }


        await client.query("COMMIT");
        return 'Успешно внесен расход';
    } catch (err) {
        await client.query("ROLLBACK");
        throw err;
    } finally {
        client.release();
    }
}


async function getAllTransactions(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", dateFrom, dateTo, searchByCategory = "") {
    const {rows} = await pool.query(`
        SELECT c.id          AS "Client Id",
               c.name        AS "Name",
               c.embg        AS "Embg",
               t.id          AS "Id",
               t.money_given AS "Given",
               t.money_got   AS "Got",
               t.profit      AS "Profit",
               t.money_diff  AS "Diff",
               t.date        AS "Date",
               t.category    AS "Category",
               t.description AS "Description"
        FROM client c
                 INNER JOIN transaction t
                            ON c.id = t.client_id
        WHERE LOWER(c.name) LIKE $1 || '%'
          AND c.embg LIKE $2 || '%'
          AND (
            t.date::DATE >= COALESCE($3::DATE, t.date::DATE)
                AND t.date::DATE <= COALESCE($4::DATE, t.date::DATE)
            )
          AND (
            $5 = ''
                OR ($5 = 'Change' AND t.description ILIKE '%Промена%')
                OR ($5 = 'Sale' AND (t.category = 'Sale' OR t.description = 'Transferred pawn to sale'))
                OR ($5 <> 'Change' AND t.category = $5)
            )
          AND t.shop_id = $6
        ORDER BY "${orderBy}" ${orderDirection}, "Id" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, dateFrom, dateTo, searchByCategory, SHOP_ID])

    return rows;
}


async function getDailyReport(date) {
    // Get total money given, total profit, and total turnover
    const {rows: totalRows} = await pool.query(`
        SELECT COUNT(*)                     AS "numTransactions",
               SUM(money_given)             AS "moneyGiven",
               SUM(profit)                  AS "profit",
               SUM(money_given + money_got) AS "turnover"
        FROM transaction
        WHERE DATE(date) = $1
          AND (category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale'))
          AND shop_id = $2;
    `, [date, SHOP_ID]);

    // Get the number of transactions from Pawn category
    const {rows: numPawnsRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) = $1
          AND description = 'Added new pawn'
          AND shop_id = $2;
    `, [date, SHOP_ID]);

    // Second query: Get the number of transactions from Sale category
    const {rows: numSaleRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) = $1
          AND (description = 'Added new sale' OR description = 'Transferred pawn to sale')
          AND shop_id = $2;
    `, [date, SHOP_ID]);

    // Get count and sums grouped by category
    const {rows: categoryRows} = await pool.query(`
        SELECT category,
               COUNT(*)                AS "numTransactions",
               SUM(money_given)        AS "moneyGiven",
               SUM(profit)             AS "profit",
               (SELECT COUNT(*)
                FROM transaction t2
                WHERE DATE(t2.date) = $1
                  AND t2.description LIKE 'Added new%'
                  AND t2.category = t1.category
                  AND t2.shop_id = $2) AS "numNew"
        FROM transaction t1
        WHERE DATE(t1.date) = $1
          AND t1.category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale')
          AND t1.shop_id = $2
        GROUP BY category;
    `, [date, SHOP_ID]);

    const {rows: cashFlowRows} = await pool.query(`
        SELECT category,
               SUM(money_given) AS "moneyGiven",
               SUM(money_got)   AS "moneyGot"
        FROM transaction t1
        WHERE DATE(t1.date) = $1
          AND t1.category IN ('Remove', 'Insert', 'Expense')
          AND t1.shop_id = $2
        GROUP BY category;
    `, [date, SHOP_ID]);

    const {rows: transactionRows} = await pool.query(`
        SELECT c.name        AS "Name",
               c.embg        AS "Embg",
               t.money_given AS "Given",
               t.money_got   AS "Got",
               t.profit      AS "Profit",
               t.money_diff  AS "Diff",
               t.category    AS "Category",
               t.description AS "Description"
        FROM client c
                 INNER JOIN transaction t
                            ON c.id = t.client_id
        WHERE DATE(date) = $1
          AND t.shop_id = $2
        ORDER BY t.date DESC;
    `, [date, SHOP_ID])

    // Combine all the results into one object
    return {
        total: totalRows[0],
        numPawns: numPawnsRows[0],
        numSales: numSaleRows[0],
        categories: categoryRows,
        cashFlow: cashFlowRows,
        transactions: transactionRows
    };
}

async function getPeriodReport(dateFrom, dateTo) {
    // Get total money given, total profit, and total turnover
    const {rows: totalRows} = await pool.query(`
        SELECT COUNT(*)                     AS "numTransactions",
               SUM(money_given)             AS "moneyGiven",
               SUM(profit)                  AS "profit",
               SUM(money_given + money_got) AS "turnover"
        FROM transaction
        WHERE DATE(date) BETWEEN $1 AND $2
          AND (category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale'))
          AND shop_id = $3;
    `, [dateFrom, dateTo, SHOP_ID]);

    // Get the number of transactions from Pawn category
    const {rows: numPawnsRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) BETWEEN $1 AND $2
          AND description = 'Added new pawn'
          AND shop_id = $3;
    `, [dateFrom, dateTo, SHOP_ID]);

    // Second query: Get the number of transactions from Sale category
    const {rows: numSaleRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) BETWEEN $1 AND $2
          AND (description = 'Added new sale' OR description = 'Transferred pawn to sale')
          AND shop_id = $3;
    `, [dateFrom, dateTo, SHOP_ID]);

    // Get count and sums grouped by category
    const {rows: categoryRows} = await pool.query(`
        SELECT category,
               COUNT(*)                AS "numTransactions",
               SUM(money_given)        AS "moneyGiven",
               SUM(profit)             AS "profit",
               (SELECT COUNT(*)
                FROM transaction t2
                WHERE DATE(t2.date) BETWEEN $1 AND $2
                  AND t2.description LIKE 'Added new%'
                  AND t2.category = t1.category
                  AND t2.shop_id = $3) AS "numNew"
        FROM transaction t1
        WHERE DATE(date) BETWEEN $1 AND $2
          AND t1.category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale')
          AND t1.shop_id = $3
        GROUP BY category;
    `, [dateFrom, dateTo, SHOP_ID]);

    const {rows: cashFlowRows} = await pool.query(`
        SELECT category,
               SUM(money_given) AS "moneyGiven",
               SUM(money_got)   AS "moneyGot"
        FROM transaction t1
        WHERE DATE(date) BETWEEN $1 AND $2
          AND t1.category IN ('Remove', 'Insert', 'Expense')
          AND t1.shop_id = $3
        GROUP BY category;
    `, [dateFrom, dateTo, SHOP_ID]);

    const {rows: transactionRows} = await pool.query(`
        SELECT c.name        AS "Name",
               c.embg        AS "Embg",
               t.money_given AS "Given",
               t.money_got   AS "Got",
               t.profit      AS "Profit",
               t.money_diff  AS "Diff",
               t.category    AS "Category",
               t.description AS "Description"
        FROM client c
                 INNER JOIN transaction t
                            ON c.id = t.client_id
        WHERE DATE(date) BETWEEN $1 AND $2
          AND t.shop_id = $3
        ORDER BY t.date DESC;
    `, [dateFrom, dateTo, SHOP_ID])

    // Combine all the results into one object
    return {
        total: totalRows[0],
        numPawns: numPawnsRows[0],
        numSales: numSaleRows[0],
        categories: categoryRows,
        cashFlow: cashFlowRows,
        transactions: transactionRows
    };
}

async function getAllMonthlyReports(limit, offset, orderBy, orderDirection, searchByMonth = "", searchByYear = "") {
    const decodedOrderBy = decodeURIComponent(orderBy);
    let orderByClause;

    if (/^[a-zA-Z_][a-zA-Z0-9_ ]*$/.test(decodedOrderBy)) {
        orderByClause = `"${decodedOrderBy}"`;  // safe to wrap in quotes
    } else {
        orderByClause = decodedOrderBy; // assume it's an expression, no quotes
    }

    const {rows} = await pool.query(`
        SELECT m.year                     AS "Year",
               m.month                    AS "Month",
               m.money_given              AS "Money Given",
               m.money_got                AS "Money Got",
               m.total_turnover           AS "Total Turnover",
               m.gross_profit             AS "Gross Profit",
               m.net_profit               AS "Net Profit",
               m.total_pawns              AS "Total Pawns",
               m.money_pawns              AS "Money Pawns",
               m.profit_pawns             AS "Profit Pawns",
               m.num_gold_pawns           AS "Num Gold Pawns",
               m.money_gold_pawns         AS "Money Gold Pawns",
               m.profit_gold_pawns        AS "Profit Gold Pawns",
               m.num_electronics_pawns    AS "Num Electronics Pawns",
               m.money_electronics_pawns  AS "Money Electronics Pawns",
               m.profit_electronics_pawns AS "Profit Electronics Pawns",
               m.num_vehicle_pawns        AS "Num Vehicle Pawns",
               m.money_vehicle_pawns      AS "Money Vehicle Pawns",
               m.profit_vehicle_pawns     AS "Profit Vehicle Pawns",
               m.num_watch_pawns          AS "Num Watch Pawns",
               m.money_watch_pawns        AS "Money Watch Pawns",
               m.profit_watch_pawns       AS "Profit Watch Pawns",
               m.num_other_pawns          AS "Num Other Pawns",
               m.money_other_pawns        AS "Money Other Pawns",
               m.profit_other_pawns       AS "Profit Other Pawns",
               m.total_sales              AS "Total Sales",
               m.money_sales              AS "Money Sales",
               m.profit_sales             AS "Profit Sales"
        FROM monthly_report m
        WHERE ($1 = '' OR m.year = $1::INT)
          AND ($2 = '' OR m.month = $2::INT)
          AND m.shop_id = $3
        ORDER BY ${orderByClause} ${orderDirection}, "Year" ${orderDirection}, "Month" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByYear, searchByMonth, SHOP_ID])

    return rows;
}

async function getMonthlyReport(year, month) {
    const {rows} = await pool.query(`
        SELECT *
        FROM monthly_report
        WHERE year = $1::INT
          AND month = $2::INT
          AND shop_id = $3
    `, [year, month, SHOP_ID])

    if (rows.length === 0)
        return null
    else
        return rows[0]
}

async function generateNewMonthReport(year, month) {
    const {rows: hasReport} = await pool.query(`SELECT *
                                                FROM monthly_report
                                                WHERE year = $1::INT
                                                  AND month = $2::INT
                                                  AND shop_id = $3`, [year, month, SHOP_ID])
    if (hasReport.length > 0)
        return {passed: false, message: `Имаш веќе внесено извештај за месец: ${month}-${year}`}

    const {rows: hasExpense} = await pool.query(`SELECT *
                                                 FROM expense
                                                 WHERE year = $1::INT
                                                   AND month = $2::INT
                                                   AND shop_id = $3`, [year, month, SHOP_ID])
    if (hasExpense.length === 0)
        return {passed: false, message: `Внеси расходи за месец ${month}-${year}`}

    const {rows: money} = await pool.query(`
        SELECT COALESCE(SUM(money_given), 0) AS "moneyGiven",
               COALESCE(SUM(money_got), 0)   AS "moneyGot",
               COALESCE(SUM(profit), 0)      AS "grossProfit"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND (category != 'Remove' AND category != 'Insert' AND category != 'Expense')
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numNewPawns} = await pool.query(`
        SELECT COUNT(*) AS "totalPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: pawns} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyPawns",
               COALESCE(SUM(profit), 0)                               AS "profitPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other')
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numNewSales} = await pool.query(`
        SELECT COUNT(*) AS "totalSales"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND (description = 'Added new sale' OR description = 'Transferred pawn to sale')
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: sales} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneySales",
               COALESCE(SUM(profit), 0)                               AS "profitSales"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Sale'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numElectronicsPawn} = await pool.query(`
        SELECT COUNT(*) AS "numElectronicsPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Electronics'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: electronicsPawn} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyElectronicsPawns",
               COALESCE(SUM(profit), 0)                               AS "profitElectronicsPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Electronics'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numGoldPawn} = await pool.query(`
        SELECT COUNT(*) AS "numGoldPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Gold'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: goldPawn} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyGoldPawns",
               COALESCE(SUM(profit), 0)                               AS "profitGoldPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Gold'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numVehiclePawn} = await pool.query(`
        SELECT COUNT(*) AS "numVehiclePawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Vehicle'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: vehiclePawn} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyVehiclePawns",
               COALESCE(SUM(profit), 0)                               AS "profitVehiclePawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Vehicle'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numWatchPawn} = await pool.query(`
        SELECT COUNT(*) AS "numWatchPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Watch'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: watchPawn} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyWatchPawns",
               COALESCE(SUM(profit), 0)                               AS "profitWatchPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Watch'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: numOtherPawn} = await pool.query(`
        SELECT COUNT(*) AS "numOtherPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Other'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: otherPawn} = await pool.query(`
        SELECT COALESCE(SUM(money_got), 0) + COALESCE(SUM(profit), 0) AS "moneyOtherPawns",
               COALESCE(SUM(profit), 0)                               AS "profitOtherPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Other'
          AND shop_id = $3;
    `, [year, month, SHOP_ID]);

    const {rows: expenses} = await pool.query(`
        SELECT COALESCE(rent + salaries + bills + other, 0) AS "expenses"
        FROM expense
        WHERE year = $1
          AND month = $2
          AND shop_id = $3;
    `, [year, month, SHOP_ID])

    await pool.query(`
        INSERT INTO monthly_report (year, month, money_given, money_got, total_turnover,
                                    gross_profit, net_profit,
                                    total_pawns, money_pawns, profit_pawns,
                                    total_sales, money_sales, profit_sales,
                                    num_electronics_pawns, money_electronics_pawns, profit_electronics_pawns,
                                    num_gold_pawns, money_gold_pawns, profit_gold_pawns,
                                    num_vehicle_pawns, money_vehicle_pawns, profit_vehicle_pawns,
                                    num_watch_pawns, money_watch_pawns, profit_watch_pawns,
                                    num_other_pawns, money_other_pawns, profit_other_pawns, shop_id)
        VALUES ($1, $2, $3, $4, $5,
                $6, $7,
                $8, $9, $10,
                $11, $12, $13,
                $14, $15, $16,
                $17, $18, $19,
                $20, $21, $22,
                $23, $24, $25,
                $26, $27, $28, $29)
    `, [
        year, month, money[0].moneyGiven, money[0].moneyGot, (+money[0].moneyGiven + +money[0].moneyGot + +money[0].grossProfit),
        money[0].grossProfit, (+money[0].grossProfit - +expenses[0].expenses),
        numNewPawns[0].totalPawns, pawns[0].moneyPawns, pawns[0].profitPawns,
        numNewSales[0].totalSales, sales[0].moneySales, sales[0].profitSales,
        numElectronicsPawn[0].numElectronicsPawns, electronicsPawn[0].moneyElectronicsPawns, electronicsPawn[0].profitElectronicsPawns,
        numGoldPawn[0].numGoldPawns, goldPawn[0].moneyGoldPawns, goldPawn[0].profitGoldPawns,
        numVehiclePawn[0].numVehiclePawns, vehiclePawn[0].moneyVehiclePawns, vehiclePawn[0].profitVehiclePawns,
        numWatchPawn[0].numWatchPawns, watchPawn[0].moneyWatchPawns, watchPawn[0].profitWatchPawns,
        numOtherPawn[0].numOtherPawns, otherPawn[0].moneyOtherPawns, otherPawn[0].profitOtherPawns, SHOP_ID
    ])

    return {passed: true, message: 'Успешно внесен месечен извештај'}
}

// Pure function to fetch gold price
let goldPriceCache = {
  price: null,
  timestamp: null
};
const CACHE_DURATION = 60 * 60 * 1000; // 1 hour in milliseconds

async function fetchGoldPriceLive() {
    try {
        const now = Date.now();
        if (goldPriceCache.price && goldPriceCache.timestamp && (now - goldPriceCache.timestamp < CACHE_DURATION)) {
            return goldPriceCache.price;
        }

        const apiKey = process.env.VITE_GOLD_API_KEY;
        
        const response = await fetch("https://goldpricez.com/api/rates/currency/eur/measure/gram", {
            headers: {
                "X-API-KEY": apiKey,
                "Content-Type": "application/json",
            }
        });

        const text = await response.text();
        let data = JSON.parse(text);
        
        // Handle double-encoded JSON
        if (typeof data === 'string') {
            data = JSON.parse(data);
        }
        
        if (!data.gram_in_eur) {
            return goldPriceCache.price || null;
        }

        const pricePerGram = parseFloat(data.gram_in_eur).toFixed(2);
        
        goldPriceCache = {
            price: pricePerGram,
            timestamp: now
        };

        return pricePerGram;
        
    } catch (err) {
        console.error("Error fetching gold price:", err);
        return goldPriceCache.price || null;
    }
}



module.exports = {
    getAllPawns,
    getPawn,
    continuePawn,
    addNewPawn,
    updatePawn,
    closePawn,
    changePawnToSale,
    getAllSales,
    getSale,
    closeSale,
    addNewSale,
    getAllClientsAutocomplete,
    getAllClients,
    getClientPawnsAndTransactions,
    getCashRegister,
    insertIntoCashRegister,
    removeFromCashRegister,
    getAllExpenses,
    getExpense,
    insertExpense,
    getAllTransactions,
    getDailyReport,
    getPeriodReport,
    getAllMonthlyReports,
    getMonthlyReport,
    generateNewMonthReport,
    fetchGoldPriceLive
}