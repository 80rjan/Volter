const pool = require('./pool');

async function getAllPawns(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "") {
    const {rows} = await pool.query(`
        SELECT c.id                                                AS "Client Id",
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

        ORDER BY "${orderBy}" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel]);
    return rows;
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
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in REMOVE PAWN");
    }

    const query = await pool.query(`SELECT provision, price_pawned, client_id
                                    FROM ${tableName}
                                    WHERE id = $1;`, [id])
    let gold_grams = 0;
    if (tableName === "gold_pawn") {
        const result = await pool.query(`SELECT weight
                                         FROM gold_pawn
                                         WHERE id = $1;`, [id]);
        gold_grams = result.rows[0]?.weight;
    }
    let provision = null;
    let pawnMoney = null;
    let clientId = null;
    if (query.rows.length > 0) {
        provision = query.rows[0].provision;
        pawnMoney = query.rows[0].price_pawned;
        clientId = query.rows[0].client_id;
    } else
        throw new Error("Cant find information in pawn table to REMOVE PAWN");

    await pool.query(`BEGIN;`)

    //Delete pawn from pawn table
    await pool.query(`
        DELETE
        FROM ${tableName}
        WHERE id = $1;
    `, [id])


    let transactionCategory = tableName === "electronics_pawn" ? "Electronics" :
        tableName === "gold_pawn" ? "Gold" :
            tableName === "vehicle_pawn" ? "Vehicle" :
                tableName === "watch_pawn" ? "Watch" : "Other";
    let transactionDescription = `Затворен залог. ${description}`;

    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, $4, $5, $6);
    `, [clientId, transactionCategory, transactionDescription, pawnMoney, priceClosed - pawnMoney, UTC_TIME])


    //Update cash register with money inserted, decrease numPawns, decrease moneyPawns, update quantity of gold in grams and update total provision for pawns
    await pool.query(`
        UPDATE cash_register
        SET total_provision = total_provision - $4,
            num_pawns       = num_pawns - 1,
            money_pawns     = money_pawns - $1,
            register_money  = register_money + $2,
            gold_grams      = gold_grams - $3,
            last_updated    = $5;
    `, [pawnMoney, priceClosed, gold_grams, provision, UTC_TIME])

    await pool.query(`COMMIT;`)
}

async function continuePawn(id, tableName, provision, description, carryOverDays) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in CONTINUE PAWN");
    }

    let query = await pool.query(`SELECT (price_pawned * (provision / 100)) AS provision_money, client_id
                                  FROM ${tableName}
                                  WHERE id = $1;`, [id])
    let clientId = null;
    if (query.rows.length > 0) {
        clientId = query.rows[0].client_id;
    } else
        throw new Error("Cant find information in pawn table to CONTINUE PAWN");

    await pool.query(`BEGIN;`)

    //Update table with new date until pawn is valid
    // total_days + daysleft
    // SET date_to = date_to + total_days*INTERVAL '1 day'
    await pool.query(`
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

    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, 0, $4, $5);
    `, [clientId, transactionCategory, transactionDescription, provision, UTC_TIME])

    //Update cash register with money inserted
    await pool.query(`
        UPDATE cash_register
        SET register_money = register_money + $1,
            last_updated   = $2;
    `, [provision, UTC_TIME])

    await pool.query(`COMMIT;`)
}

async function addNewPawn(pawnCategory, pawnObj, clientObj) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(pawnCategory)) {
        throw new Error("Invalid pawn category in ADD NEW PAWN");
    }
    let gold_grams = 0;
    if (pawnCategory === "gold_pawn")
        gold_grams = pawnObj.weight;


    const clientCheck = await pool.query(
        `SELECT id
         FROM client
         WHERE name = $1
           AND embg = $2;`,
        [clientObj.name, clientObj.embg]
    );

    let clientId = null;
    if (clientCheck.rows.length > 0) {
        // Client found → update telephones
        clientId = clientCheck.rows[0].id;
        await pool.query(
            `UPDATE client
             SET telephone   = $1,
                 telephone_2 = $2
             WHERE id = $3;`,
            [clientObj.telephone, clientObj.telephone_2, clientId]
        );
    } else {
        // Insert new client since no match by name & embg
        const insertRes = await pool.query(
            `INSERT INTO client (name, embg, telephone, telephone_2, city)
             VALUES ($1, $2, $3, $4, $5)
             RETURNING id;`,
            [clientObj.name, clientObj.embg, clientObj.telephone, clientObj.telephone_2, clientObj.city]
        );
        clientId = insertRes.rows[0].id;
    }

    //Insert pawn in pawn table
    switch (pawnCategory) {
        case 'electronics_pawn':
            await pool.query(`
                INSERT INTO electronics_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision,
                                              date_from, date_to, total_days, description)
                VALUES ($1, $2, $3, $4, $5, $6, $9::DATE, $9::DATE + $7 * INTERVAL '1 day', $7, $8);
            `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'gold_pawn':
            await pool.query(`
                INSERT INTO gold_pawn (client_id, weight, carats, price_pawned, price_to_redeem, provision, date_from,
                                       date_to, total_days, description, type)
                VALUES ($1, $2, $3, $4, $5, $6, $10::DATE, $10::DATE + $7 * INTERVAL '1 day', $7, $8, $9);
            `, [clientId, pawnObj.weight, pawnObj.carats, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.type, pawnObj.date]);
            break;
        case 'other_pawn':
            await pool.query(`
                INSERT INTO other_pawn (client_id, price_pawned, price_to_redeem, provision, date_from, date_to,
                                        total_days, description)
                VALUES ($1, $2, $3, $4, $7::DATE, $7::DATE + $5 * INTERVAL '1 day', $5, $6);
            `, [clientId, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'vehicle_pawn':
            await pool.query(`
                INSERT INTO vehicle_pawn (client_id, brand, model, year, price_pawned, price_to_redeem, provision,
                                          date_from, date_to, total_days, description)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $10::DATE, $10::DATE + $8 * INTERVAL '1 day', $8, $9);
            `, [clientId, pawnObj.brand, pawnObj.model, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'watch_pawn':
            await pool.query(`
                INSERT INTO watch_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from,
                                        date_to, total_days, description)
                VALUES ($1, $2, $3, $4, $5, $6, $9::DATE, $9::DATE + $7 * INTERVAL '1 day', $7, $8);
            `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
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
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, $4, 0, 0, $5::DATE);
    `, [clientId, transactionCategory, transactionDescription, pawnObj.price_pawned, pawnObj.date])

    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Update cash register with money taken, increase numPawns, increase moneyPawns, update quantity of gold in grams and update total provision for pawns
    await pool.query(`
        UPDATE cash_register
        SET total_provision = total_provision + $3,
            num_pawns       = num_pawns + 1,
            money_pawns     = money_pawns + $1,
            register_money  = register_money - $1,
            gold_grams      = gold_grams + $2,
            last_updated    = $4;
    `, [pawnObj.price_pawned, gold_grams, pawnObj.provision, UTC_TIME]);
}

async function updatePawn(pawnTable, pawnId, pricePawned, provision, description, goldGramsDiff) {
    let oldPawn = null
    try {
        const {rows} = await pool.query(`SELECT price_pawned, client_id, provision
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
        s += `Цена: ${oldPrice} во ${pricePawned}. `
    if (oldProvision !== provision)
        s += `Месечна провизија: ${oldProvision} во ${provision}. `
    if (goldGramsDiff !== 0)
        s += `Злато додадено: ${Number(goldGramsDiff).toLocaleString("de-DE")}гр. `

    let query = `
        UPDATE ${pawnTable}
        SET price_pawned    = CAST($2 AS NUMERIC),
            price_to_redeem = CAST($2 AS NUMERIC) + CAST($3 AS NUMERIC),
            provision       = CAST($3 AS NUMERIC),
            description     = $4
    `;
    const params = [pawnId, pricePawned, provision, description];

    if (pawnTable === "gold_pawn") {
        query += `, weight = weight + CAST($5 AS NUMERIC)`;
        params.push(goldGramsDiff)
    }

    query += ` WHERE id = $1 RETURNING *;`;

    let result = null
    try {
        const {rows} = await pool.query(query, params);
        result = rows[0];
    } catch (error) {
        console.error("Error executing query: ", error);
        throw error;
    }

    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Insert a new transaction with money given
    try {
        await pool.query(`
            INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
            VALUES ($1, $2, $3, $4, 0, 0, $5);
        `, [clientId, category, s, pricePawned - oldPrice, UTC_TIME])
    } catch (error) {
        console.error("Error executing query transaction: " + error)
        throw new Error("Failed to modify transactions")
    }

    //Update cash register with balance of money in pawns, average percent, money removed
    try {
        await pool.query(`
            UPDATE cash_register
            SET register_money  = register_money - $1,
                money_pawns     = money_pawns + $1,
                total_provision = total_provision + $2,
                gold_grams      = gold_grams + $3,
                last_updated    = $4;
        `, [pricePawned - oldPrice, provision - oldProvision, goldGramsDiff, UTC_TIME])
    } catch (error) {
        console.error("Error executing query cash reg: " + error)
        throw new Error("Failed to modify cash reg")
    }

    return result;
}


async function changePawnToSale(id, pawnCategory) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(pawnCategory))
        throw new Error("Invalid pawn category in CHANGE PAWN TO SALE");

    let query = await pool.query(`SELECT provision, client_id, price_pawned
                                  FROM ${pawnCategory}
                                  WHERE id = $1;`, [id])
    let provision = null;
    let clientId = null;
    let priceBought = null;
    if (query.rows.length > 0) {
        provision = query.rows[0].provision;
        clientId = query.rows[0].client_id;
        priceBought = query.rows[0].price_pawned;
    } else {
        throw new Error("Cant find information in pawn table to CHANGE PAWN TO SALE");
    }

    let gold_grams = 0;

    let description = null;
    switch (pawnCategory) {
        case "electronics_pawn":
            query = await pool.query(`SELECT brand, year, description
                                      FROM electronics_pawn
                                      WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand}, ${query.rows[0].year}, ${query.rows[0].description}`;
            break;
        case "gold_pawn":
            query = await pool.query(`SELECT price_pawned, weight, carats, description
                                      FROM gold_pawn
                                      WHERE id = $1;`, [id])
            if (query.rows.length > 0) {
                gold_grams = query.rows[0].weight;
                description = `${query.rows[0].weight}g, ${query.rows[0].carats}k, ${query.rows[0].price_pawned / query.rows[0].weight} по грам, ${query.rows[0].description}`;
            }
            break;
        case "other_pawn":
            query = await pool.query(`SELECT description
                                      FROM other_pawn
                                      WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = query.rows[0].description;
            break;
        case "vehicle_pawn":
            query = await pool.query(`SELECT brand, model, year, description
                                      FROM vehicle_pawn
                                      WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand} ${query.rows[0].model}, ${query.rows[0].year}, ${query.rows[0].description}`;
            break;
        case "watch_pawn":
            query = await pool.query(`SELECT brand, year, description
                                      FROM watch_pawn
                                      WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand}, ${query.rows[0].year}, ${query.rows[0].description}`;
            break;
        default:
            throw new Error("Invalid pawn category in ADD SALE");
    }

    //Insert sale into sale table
    await pool.query(`
        INSERT INTO sale (price_bought, date_from, description)
        VALUES ($1, CURRENT_DATE, $2)
    `, [priceBought, description])

    //Delete pawn from pawn table
    await pool.query(`
        DELETE
        FROM ${pawnCategory}
        WHERE id = $1;
    `, [id])

    let transactionCategory = pawnCategory === "electronics_pawn" ? "Electronics" :
        pawnCategory === "gold_pawn" ? "Gold" :
            pawnCategory === "vehicle_pawn" ? "Vehicle" :
                pawnCategory === "watch_pawn" ? "Watch" : "Other";
    let transactionDescription = "Transferred pawn to sale";

    const now = new Date();
    // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, 0, 0, $4);
    `, [clientId, transactionCategory, transactionDescription, UTC_TIME])

    //Update cash register with decrease numPawns, decrease moneyPawns, increase numSales, increase moneySales, update quantity of gold in grams and update total provision for pawns
    await pool.query(`
        UPDATE cash_register
        SET total_provision  = total_provision - $3,
            num_pawns        = num_pawns - 1,
            money_pawns      = money_pawns - $1,
            num_sale_items   = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            gold_grams       = gold_grams - $2,
            last_updated     = $4;
    `, [priceBought, gold_grams, provision, UTC_TIME])
}

async function getAllSales(limit, offset, orderBy, orderDirection) {
    const {rows} = await pool.query(`
        SELECT s.id                               AS "Id",
               s.description                      AS "About",
               to_char(s.date_from, 'YYYY-MM-DD') AS "Date Bought",
               s.price_bought                     AS "Item Cost"
        FROM sale s
        ORDER BY "${orderBy}" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [])

    return rows;
}

async function getSale(saleId) {

    const {rows} = await pool.query(`SELECT *, to_char(date_from, 'YYYY-MM-DD') AS date_from
                                     FROM sale
                                     WHERE id = $1`, [saleId]);
    return rows[0];
}

async function closeSale(id, priceSold, description) {
    const query = await pool.query(`SELECT price_bought
                                    FROM sale
                                    WHERE id = $1;`, [id]);
    let priceBought = null;
    if (query.rows.length > 0) {
        priceBought = query.rows[0].price_bought;
    } else
        throw new Error("Cant find information in sale table to REMOVE SALE");


    await pool.query(`BEGIN;`)

    //Delete sale from sale table
    await pool.query(`
        DELETE
        FROM sale
        WHERE id = $1;
    `, [id])

    let transactionCategory = "Sale";
    let transactionDescription = `Затворена продажба. ${description}`;

    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));
    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, $1, $2, 0, $3, $4, $5);
    `, [transactionCategory, transactionDescription, priceBought, priceSold - priceBought, UTC_TIME])

    //Update cash register with money inserted, decrease numSales and decrease moneySales
    await pool.query(`
        UPDATE cash_register
        SET num_sale_items   = num_sale_items - 1,
            money_sale_items = money_sale_items - $1,
            register_money   = register_money + $2,
            last_updated     = $3;
    `, [priceBought, priceSold, UTC_TIME])

    await pool.query(`COMMIT;`)

    return priceSold;
}

async function addNewSale(priceBought, description) {

    //Insert a new sale into sale table
    await pool.query(`
        INSERT INTO sale (price_bought, date_from, description)
        VALUES ($1, CURRENT_DATE, $2)
    `, [priceBought, description])

    let transactionCategory = "Sale";
    let transactionDescription = "Added new sale";

    const now = new Date();
    // Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));

    //Insert a new transaction with money given
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, $1, $2, $3, 0, 0, $4);
    `, [transactionCategory, transactionDescription, priceBought, UTC_TIME])

    //Update cash register with money given, increase numSales and increase moneySales
    await pool.query(`
        UPDATE cash_register
        SET num_sale_items   = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            register_money   = register_money - $1,
            last_updated     = $2;
    `, [priceBought, UTC_TIME])
}


async function getAllClientsAutocomplete(limit, offset, search) {

    const {rows} = await pool.query(`
        SELECT *
        FROM client
        WHERE LOWER(name) LIKE $1
           OR embg LIKE $1
           OR telephone LIKE $1
           OR telephone_2 LIKE $1
        LIMIT ${limit} OFFSET ${offset};
    `, [`${search}%`]);
    return rows;
}

async function getAllClients(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "") {
    const {rows} = await pool.query(`
        SELECT c.id                                         AS "Id",
               c.name                                       AS "Name",
               c.telephone                                  AS "Telephone 1",
               c.telephone_2                                AS "Telephone 2",
               c.city                                       AS "City",
               (SELECT COUNT(*)
                FROM transaction t
                WHERE t.client_id = c.id
                  AND t.description = 'Added new pawn')     AS "Total Pawns",
               (SELECT COUNT(*)
                FROM (SELECT id
                      FROM gold_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT id
                      FROM electronics_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT id
                      FROM watch_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT id
                      FROM vehicle_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT id
                      FROM other_pawn
                      WHERE client_id = c.id) AS all_pawns) AS "Active Pawns",
               (SELECT SUM(price_pawned)
                FROM (SELECT price_pawned
                      FROM gold_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT price_pawned
                      FROM electronics_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT price_pawned
                      FROM watch_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT price_pawned
                      FROM vehicle_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT price_pawned
                      FROM other_pawn
                      WHERE client_id = c.id) AS all_pawns) AS "Money Pawns",
               (SELECT SUM(provision)
                FROM (SELECT provision
                      FROM gold_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT provision
                      FROM electronics_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT provision
                      FROM watch_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT provision
                      FROM vehicle_pawn
                      WHERE client_id = c.id
                      UNION ALL
                      SELECT provision
                      FROM other_pawn
                      WHERE client_id = c.id) AS all_pawns) AS "Money Provision"
        FROM client c
        WHERE c.name <> 'Admin'
          AND LOWER(c.name) LIKE $1 || '%'
          AND c.embg LIKE $2 || '%'
          AND (c.telephone LIKE $3 || '%'
            OR c.telephone_2 LIKE $3 || '%')
        ORDER BY "${orderBy}" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel]);
    return rows;
}


async function getCashRegister() {
    const {rows} = await pool.query(`SELECT *
                                     FROM cash_register`);

    return rows[0];
}

async function insertIntoCashRegister(amount, description) {
    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));

    await pool.query(`
        UPDATE cash_register
        SET register_money = register_money + $1,
            last_updated   = $2
    `, [amount, UTC_TIME])

    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, 'Insert', $1, 0, $2, 0, $3);
    `, [description, amount, UTC_TIME])
}

async function removeFromCashRegister(amount, description) {
    const now = new Date();
// Adjust Skopje time to UTC manually (Skopje is UTC+2 during regular time, UTC+1 during daylight saving time)
    const UTC_TIME = new Date(now.getTime() - (now.getTimezoneOffset() * 60000));

    await pool.query(`
        UPDATE cash_register
        SET register_money = register_money - $1,
            last_updated   = $2
    `, [amount, UTC_TIME])

    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, 'Remove', $1, $2, 0, 0, $3);
    `, [description, amount, UTC_TIME])
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
        ORDER BY ${orderBy} ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByYear, searchByMonth])

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
          AND year = $2::INT;
    `, [month, year]);

    if (expenseResult.rows.length === 0) {
        throw new Error("Expense not found");
    }

    const expense = expenseResult.rows[0];

    // Get all transactions matching category 'Expense' and same month/year
    const transactionsResult = await pool.query(`
        SELECT t.date        AS "Date",
               t.description AS "Description"
        FROM transaction t
        WHERE category = 'Expense'
          AND EXTRACT(MONTH FROM date) = $1
          AND EXTRACT(YEAR FROM date) = $2
    `, [month, year]);

    // Return both
    return {
        expense,
        transactions: transactionsResult.rows
    };
}

async function insertExpense(year, month, rent, salaries, bills, other, description) {
    let transaction_description = `${month}/${year}. `;
    if (rent !== 0) transaction_description += 'Кирија: ' + rent + '. '
    if (salaries !== 0) transaction_description += 'Плати: ' + salaries + '. '
    if (bills !== 0) transaction_description += 'Сметки: ' + bills + '. '
    if (other !== 0) transaction_description += 'Останато: ' + other + '. '
    if (description !== '' && transaction_description.length + description.length + "Опис: .".length < 200) transaction_description += 'Опис: ' + description + '.';

    const {rows: hasReport} = await pool.query(`SELECT *
                                                FROM monthly_report
                                                WHERE year = $1::INT
                                                  AND month = $2::INT`, [year, month])

    if (hasReport.length > 0) {
        return `Не може да се внесе расход, бидејќи веќе е внесен месечен извештај за ${month}/${year}. За внес контактирајте со одржувачот на системот.`
    }

    const {rows: hasExpense} = await pool.query(`SELECT *
                                                 FROM expense
                                                 WHERE year = $1::INT
                                                   AND month = $2::INT`, [year, month])
    if (hasExpense.length > 0)
        await pool.query(`
            UPDATE expense
            SET rent        = rent + $3,
                salaries    = salaries + $4,
                bills       = bills + $5,
                other       = other + $6,
                description = $7
            WHERE year = $1::INT
              AND month = $2::INT
        `, [year, month, rent, salaries, bills, other, '']) // no description in expense, descriptions are kept in transactions
    else {
        await pool.query(`
            INSERT INTO expense (year, month, rent, salaries, bills, other, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7)
        `, [year, month, rent, salaries, bills, other, '']) // no description in expense, descriptions are kept in transactions
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

    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, 'Expense', $1, $2, 0, 0, $3);
    `, [transaction_description, rent + salaries + bills + other, UTC_TIME_EXPENSE_DATE])

    if (rent + salaries + bills + other !== 0) {
        const UTC_TIME_TODAY = new Date(today.getTime() - (today.getTimezoneOffset() * 60000));
        await pool.query(`
            UPDATE cash_register
            SET register_money = register_money - $1,
                last_updated   = $2;
        `, [rent + salaries + bills + other, UTC_TIME_TODAY])
    }


    return 'Успешно внесен расход'
}


async function getAllTransactions(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByDate = "", searchByCategory = "") {

    const {rows} = await pool.query(`
        SELECT c.id          AS "Client Id",
               c.name        AS "Name",
               c.embg        AS "Embg",
               t.id          AS "Id",
               t.money_given AS "Given",
               t.money_got   AS "Got",
               t.profit      AS "Profit",
               t.date        AS "Date",
               t.category    AS "Category",
               t.description AS "Description"
        FROM client c
                 INNER JOIN transaction t
                            ON c.id = t.client_id
        WHERE LOWER(c.name) LIKE $1 || '%'
          AND c.embg LIKE $2 || '%'
          AND ($3 = '' OR $3 IS NULL OR t.date::DATE = $3::DATE)
          AND (
            $4 = ''
                OR ($4 = 'Change' AND t.description ILIKE '%Промена%')
                OR ($4 <> 'Change' AND t.category = $4)
            )
        ORDER BY "${orderBy}" ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByDate, searchByCategory])

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
          AND (category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale'));
    `, [date]);

    // Get the number of transactions from Pawn category
    const {rows: numPawnsRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) = $1
          AND description = 'Added new pawn';
    `, [date]);

    // Second query: Get the number of transactions from Sale category
    const {rows: numSaleRows} = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction
        WHERE DATE(date) = $1
          AND (description = 'Added new sale' OR description = 'Transferred pawn to sale');
    `, [date]);

    // Get count and sums grouped by category
    const {rows: categoryRows} = await pool.query(`
        SELECT category,
               COUNT(*)                          AS "numTransactions",
               SUM(money_given)                  AS "moneyGiven",
               SUM(profit)                       AS "profit",
               (SELECT COUNT(*)
                FROM transaction t2
                WHERE DATE(t2.date) = $1
                  AND t2.description = 'Added new pawn'
                  AND t2.category = t1.category) AS "numNew"
        FROM transaction t1
        WHERE DATE(t1.date) = $1
          AND t1.category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other', 'Sale')
        GROUP BY category;
    `, [date]);

    // Combine all the results into one object
    return {
        total: totalRows[0],
        numPawns: numPawnsRows[0],
        numSales: numSaleRows[0],
        categories: categoryRows
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
        ORDER BY ${orderByClause} ${orderDirection}
        LIMIT ${limit} OFFSET ${offset};
    `, [searchByYear, searchByMonth])

    return rows;
}

async function getMonthlyReport(year, month) {
    const {rows} = await pool.query(`
        SELECT *
        FROM monthly_report
        WHERE year = $1::INT
          AND month = $2::INT
    `, [year, month])

    if (rows.length === 0)
        return null
    else
        return rows[0]
}

async function generateNewMonthReport(year, month) {
    const {rows: hasReport} = await pool.query(`SELECT *
                                                FROM monthly_report
                                                WHERE year = $1::INT
                                                  AND month = $2::INT`, [year, month])
    if (hasReport.length > 0)
        return {passed: false, message: `Имаш веќе внесено извештај за месец: ${month}-${year}`}

    const {rows: hasExpense} = await pool.query(`SELECT *
                                                 FROM expense
                                                 WHERE year = $1::INT
                                                   AND month = $2::INT`, [year, month])
    if (hasExpense.length === 0)
        return {passed: false, message: `Внеси расходи за месец ${month}-${year}`}

    const {rows: money} = await pool.query(`
        SELECT SUM(money_given) AS "moneyGiven",
               SUM(money_got)   AS "moneyGot",
               SUM(profit)      AS "grossProfit"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND (category != 'Remove' AND category != 'Insert' AND category != 'Expense');
    `, [year, month])

    const {rows: numNewPawns} = await pool.query(`
        SELECT COUNT(*) AS "totalPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn';
    `, [year, month]);
    const {rows: pawns} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyPawns",
               SUM(profit)                  AS "profitPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND (category IN ('Electronics', 'Watch', 'Gold', 'Vehicle', 'Other'));
    `, [year, month]);

    const {rows: numNewSales} = await pool.query(`
        SELECT COUNT(*) AS "totalSales"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND (description = 'Added new sale' OR description = 'Transferred pawn to sale');
    `, [year, month]);
    const {rows: sales} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneySales",
               SUM(profit)                  AS "profitSales"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Sale';
    `, [year, month]);

    const {rows: numElectronicsPawn} = await pool.query(`
        SELECT COUNT(*) AS "numElectronicsPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Electronics';
    `, [year, month]);
    const {rows: electronicsPawn} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyElectronicsPawns",
               SUM(profit)                  AS "profitElectronicsPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Electronics';
    `, [year, month]);

    const {rows: numGoldPawn} = await pool.query(`
        SELECT COUNT(*) AS "numGoldPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Gold';
    `, [year, month]);
    const {rows: goldPawn} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyGoldPawns",
               SUM(profit)                  AS "profitGoldPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Gold';
    `, [year, month]);

    const {rows: numVehiclePawn} = await pool.query(`
        SELECT COUNT(*) AS "numVehiclePawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Vehicle';
    `, [year, month]);
    const {rows: vehiclePawn} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyVehiclePawns",
               SUM(profit)                  AS "profitVehiclePawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Vehicle';
    `, [year, month]);

    const {rows: numWatchPawn} = await pool.query(`
        SELECT COUNT(*) AS "numWatchPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Watch';
    `, [year, month]);
    const {rows: watchPawn} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyWatchPawns",
               SUM(profit)                  AS "profitWatchPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Watch';
    `, [year, month]);

    const {rows: numOtherPawn} = await pool.query(`
        SELECT COUNT(*) AS "numOtherPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND description = 'Added new pawn'
          AND category = 'Other';
    `, [year, month]);
    const {rows: otherPawn} = await pool.query(`
        SELECT SUM(money_got) + SUM(profit) AS "moneyOtherPawns",
               SUM(profit)                  AS "profitOtherPawns"
        FROM transaction
        WHERE EXTRACT(YEAR FROM date) = $1
          AND EXTRACT(MONTH FROM date) = $2
          AND category = 'Other';
    `, [year, month]);

    const {rows: expenses} = await pool.query(`
        SELECT rent + salaries + bills + other AS "expenses"
        FROM expense
        WHERE year = $1
          AND month = $2
    `, [year, month])

    await pool.query(`
        INSERT INTO monthly_report (year, month, money_given, money_got, total_turnover,
                                    gross_profit, net_profit,
                                    total_pawns, money_pawns, profit_pawns,
                                    total_sales, money_sales, profit_sales,
                                    num_electronics_pawns, money_electronics_pawns, profit_electronics_pawns,
                                    num_gold_pawns, money_gold_pawns, profit_gold_pawns,
                                    num_vehicle_pawns, money_vehicle_pawns, profit_vehicle_pawns,
                                    num_watch_pawns, money_watch_pawns, profit_watch_pawns,
                                    num_other_pawns, money_other_pawns, profit_other_pawns)
        VALUES ($1, $2, $3, $4, $5,
                $6, $7,
                $8, $9, $10,
                $11, $12, $13,
                $14, $15, $16,
                $17, $18, $19,
                $20, $21, $22,
                $23, $24, $25,
                $26, $27, $28)
    `, [
        year, month, money[0].moneyGiven, money[0].moneyGot, (+money[0].moneyGiven + +money[0].moneyGot + +money[0].grossProfit),
        money[0].grossProfit, (+money[0].grossProfit - +expenses[0].expenses),
        numNewPawns[0].totalPawns, pawns[0].moneyPawns, pawns[0].profitPawns,
        numNewSales[0].totalSales, sales[0].moneySales, sales[0].profitSales,
        numElectronicsPawn[0].numElectronicsPawns, electronicsPawn[0].moneyElectronicsPawns, electronicsPawn[0].profitElectronicsPawns,
        numGoldPawn[0].numGoldPawns, goldPawn[0].moneyGoldPawns, goldPawn[0].profitGoldPawns,
        numVehiclePawn[0].numVehiclePawns, vehiclePawn[0].moneyVehiclePawns, vehiclePawn[0].profitVehiclePawns,
        numWatchPawn[0].numWatchPawns, watchPawn[0].moneyWatchPawns, watchPawn[0].profitWatchPawns,
        numOtherPawn[0].numOtherPawns, otherPawn[0].moneyOtherPawns, otherPawn[0].profitOtherPawns
    ])

    return {passed: true, message: 'Успешно внесен месечен извештај'}
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
    getCashRegister,
    insertIntoCashRegister,
    removeFromCashRegister,
    getAllExpenses,
    getExpense,
    insertExpense,
    getAllTransactions,
    getDailyReport,
    getAllMonthlyReports,
    getMonthlyReport,
    generateNewMonthReport
}