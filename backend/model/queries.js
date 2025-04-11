const pool = require('./pool');

async function getAllPawns(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "") {
    const { rows } = await pool.query(`
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Electronics' AS "Category", 
        ep.id AS "Id",
        ep.brand || ' ' || ep.year || ' ' || ep.description AS "About", 
        to_char(ep.date_to, 'YYYY-MM-DD') AS "Valid Until", 
        EXTRACT(DAY FROM (ep.date_to - CURRENT_TIMESTAMP)) AS "Days Left",  
        ep.price_pawned * (ep.provision / 100) AS "Provision", 
        ep.price_pawned AS "Item Cost",
        ep.total_days AS "Total Days"
    FROM client c
    INNER JOIN electronics_pawn ep
        ON c.id = ep.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Gold' AS "Category", 
        gp.id AS "Id",
        gp.weight || 'g ' || gp.carats || 'k ' || gp.type || ' ' || gp.description AS "About", 
        to_char(gp.date_to, 'YYYY-MM-DD') AS "Valid Until", 
        EXTRACT(DAY FROM (gp.date_to - CURRENT_TIMESTAMP)) AS "Days Left", 
        gp.price_pawned * (gp.provision / 100) AS "Provision", 
        gp.price_pawned AS "Item Cost",
        gp.total_days AS "Total Days"
    FROM client c
    INNER JOIN gold_pawn gp
        ON c.id = gp.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Other' AS "Category", 
        op.id AS "Id",
        op.description AS "About", 
        to_char(op.date_to, 'YYYY-MM-DD') AS "Valid Until", 
        EXTRACT(DAY FROM (op.date_to - CURRENT_TIMESTAMP)) AS "Days Left", 
        op.price_pawned * (op.provision / 100) AS "Provision", 
        op.price_pawned AS "Item Cost",
        op.total_days AS "Total Days"
    FROM client c
    INNER JOIN other_pawn op
        ON c.id = op.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Vehicle' AS "Category", 
        vp.id AS "Id",
        vp.brand || ' ' || vp.model || ' ' || vp.year || ' ' || vp.description AS "About", 
        to_char(vp.date_to, 'YYYY-MM-DD') AS "Valid Until", 
        EXTRACT(DAY FROM (vp.date_to - CURRENT_TIMESTAMP)) AS "Days Left",
        vp.price_pawned * (vp.provision / 100) AS "Provision", 
        vp.price_pawned AS "Item Cost",
        vp.total_days AS "Total Days"
    FROM client c
    INNER JOIN vehicle_pawn vp
        ON c.id = vp.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Watch' AS "Category", 
        wp.id AS "Id",
        wp.brand || ' ' || wp.year || ', ' || wp.description AS "About", 
        to_char(wp.date_to, 'YYYY-MM-DD') AS "Valid Until", 
        EXTRACT(DAY FROM (wp.date_to - CURRENT_TIMESTAMP)) AS "Days Left",
        wp.price_pawned * (wp.provision / 100) AS "Provision", 
        wp.price_pawned AS "Item Cost",
        wp.total_days AS "Total Days"
    FROM client c
    INNER JOIN watch_pawn wp
        ON c.id = wp.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    
    ORDER BY "${orderBy}" ${orderDirection}
    LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel]);
    return rows;
}

async function getPawn(clientId, category, pawnId) {
    let pawnTable = "";
    switch (category) {
        case "Electronics": pawnTable = "electronics_pawn";break;
        case "Gold": pawnTable = "gold_pawn";break;
        case "Vehicle": pawnTable = "vehicle_pawn";break;
        case "Watch": pawnTable = "watch_pawn";break;
        case "Other": pawnTable = "other_pawn";break;
    }

    const { rows: pawnRows } = await pool.query(`SELECT *, to_char(date_from, 'YYYY-MM-DD') AS date_from, to_char(date_to, 'YYYY-MM-DD') AS date_to FROM ${pawnTable} WHERE id = $1`, [pawnId]);
    const pawn = pawnRows[0];
    const { rows: clientRows } = await pool.query(`SELECT * FROM client WHERE id = $1`, [clientId]);
    const client = clientRows[0];
    return { pawn, client };
}

async function closePawn(id, tableName, priceClosed) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in REMOVE PAWN");
    }

    const query = await pool.query(`SELECT provision, price_pawned, client_id FROM ${tableName} WHERE id = $1;`, [id])
    let gold_grams = 0;
    if (tableName === "gold_pawn") {
        const result = await pool.query(`SELECT weight FROM gold_pawn WHERE id = $1;`, [id]);
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
        DELETE FROM ${tableName}
        WHERE id = $1;
    `, [id])


    let transactionCategory = tableName === "electronics_pawn" ? "Electronics" :
                    tableName === "gold_pawn" ? "Gold" :
                    tableName === "vehicle_pawn" ? "Vehicle" :
                    tableName === "watch_pawn" ? "Watch" : "Other";
    let transactionDescription = "Closed pawn";

    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, $4, $5, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription, pawnMoney, priceClosed-pawnMoney])


    //Update cash register with money inserted, decrease numPawns, decrease moneyPawns, update quantity of gold in grams and update average provision for pawns
    await pool.query(`
        UPDATE cash_register 
        SET
            average_provision = 
                CASE 
                    WHEN num_pawns > 1 THEN (average_provision * num_pawns - $4) / (num_pawns - 1)
                    ELSE 0
                END,
            num_pawns = num_pawns - 1,
            money_pawns = money_pawns - $1,
            register_money = register_money + $2,
            gold_grams = gold_grams - $3,
            last_updated = NOW();
    `, [pawnMoney, priceClosed, gold_grams, provision])

    await pool.query(`COMMIT;`)
}

async function continuePawn(id, tableName, provision) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in CONTINUE PAWN");
    }

    let query = await pool.query(`SELECT (price_pawned * (provision / 100)) AS provision_money, client_id FROM ${tableName} WHERE id = $1;`, [id])
    let clientId = null;
    if (query.rows.length > 0) {
        clientId = query.rows[0].client_id;
    } else
        throw new Error("Cant find information in pawn table to CONTINUE PAWN");

    await pool.query(`BEGIN;`)

    //Update table with new date until pawn is valid
    await pool.query(`
        UPDATE ${tableName}
        SET date_to = date_to + total_days*INTERVAL '1 day'
        WHERE id = $1;
    `, [id])


    let transactionCategory = tableName === "electronics_pawn" ? "Electronics" :
        tableName === "gold_pawn" ? "Gold" :
            tableName === "vehicle_pawn" ? "Vehicle" :
                tableName === "watch_pawn" ? "Watch" : "Other";
    let transactionDescription = "Continued pawn";

    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, 0, $4, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription, provision])

    //Update cash register with money inserted
    await pool.query(`
        UPDATE cash_register 
        SET
            register_money = register_money + $1,
            last_updated = NOW();
    `, [provision])

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

    //Insert a new client if name, embg and telephone arent already a combination in another client
    await pool.query(`
        INSERT INTO client (name, embg, telephone, city)
        VALUES ($1, $2, $3, $4)
        ON CONFLICT (name, embg, telephone) DO NOTHING;
    `, [clientObj.name, clientObj.embg, clientObj.telephone, clientObj.city]);

    const query = await pool.query(`SELECT id FROM client WHERE name = $1 AND embg = $2 AND telephone = $3;`, [clientObj.name, clientObj.embg, clientObj.telephone]);
    let clientId = null;
    if (query.rows.length > 0) {
        clientId = query.rows[0].id;
    } else
        throw new Error("Cant find information in client table to ADD PAWN");

    console.log(pawnObj)
    //Insert pawn in pawn table
    switch (pawnCategory) {
        case 'electronics_pawn': await pool.query(`
            INSERT INTO electronics_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $9::DATE, $9::DATE + $7 * INTERVAL '1 day', $7, $8);
        `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'gold_pawn': await pool.query(`
            INSERT INTO gold_pawn (client_id, weight, carats, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description, type)
            VALUES ($1, $2, $3, $4, $5, $6, $10::DATE, $10::DATE + $7 * INTERVAL '1 day', $7, $8, $9);
        `, [clientId, pawnObj.weight, pawnObj.carats, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.type, pawnObj.date]);
            break;
        case 'other_pawn': await pool.query(`
            INSERT INTO other_pawn (client_id, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $7::DATE, $7::DATE + $5 * INTERVAL '1 day', $5, $6);
        `, [clientId, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'vehicle_pawn': await pool.query(`
            INSERT INTO vehicle_pawn (client_id, brand, model, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $10::DATE, $10::DATE + $8 * INTERVAL '1 day',  $8, $9);
        `, [clientId, pawnObj.brand, pawnObj.model, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.total_days, pawnObj.description, pawnObj.date]);
            break;
        case 'watch_pawn': await pool.query(`
            INSERT INTO watch_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
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
        VALUES ($1, $2, $3, $4, 0, 0, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription, pawnObj.price_pawned])

    //Update cash register with money taken, increase numPawns, increase moneyPawns, update quantity of gold in grams and update average provision for pawns
    await pool.query(`
        UPDATE cash_register 
        SET
            average_provision = (average_provision * num_pawns + $3) / (num_pawns + 1),
            num_pawns = num_pawns + 1,
            money_pawns = money_pawns + $1,
            register_money = register_money - $1,
            gold_grams = gold_grams + $2,
            last_updated = NOW();
    `, [pawnObj.price_pawned, gold_grams, pawnObj.provision]);
}

async function updatePawn(pawnTable, pawnId, pricePawned, provision, description, goldGramsDiff) {
    let oldPawn = null
    try {
        const { rows } = await pool.query(`SELECT price_pawned, client_id, provision FROM ${pawnTable} WHERE id = $1;`, [pawnId]);
        oldPawn = rows
    } catch (error) {
        console.error("Error executing query old pawn: " + error)
        throw new Error("Failed to fetch old pawn data")
    }
    const clientId = oldPawn[0].client_id;
    const oldPrice = parseInt(oldPawn[0].price_pawned);
    const oldProvision = parseInt(oldPawn[0].provision)
    console.log("Grams diff: ", goldGramsDiff)

    let s = ``
    if (oldPrice !== pricePawned)
        s += `Цена: ${oldPrice} во ${pricePawned}. `
    if (oldProvision !== provision)
        s += `Процент: ${oldProvision} во ${provision}. `
    if (goldGramsDiff !== 0)
        s += `Злато додадено: ${goldGramsDiff}гр. `

    //Insert a new transaction with money given
    try {
        await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, $4, 0, 0, CURRENT_TIMESTAMP);
    `, [clientId, 'Промена залог', s, pricePawned - oldPrice])
    } catch (error) {
        console.error("Error executing query transaction: " + error)
        throw new Error("Failed to modify transactions")
    }

    //Update cash register with balance of money in pawns, average percent, money removed
    try {
        await pool.query(`
        UPDATE cash_register 
        SET
            register_money = register_money - $1,
            money_pawns = money_pawns + $1,
            average_provision = (average_provision * num_pawns + $2) / num_pawns,
            gold_grams = gold_grams + $3,
            last_updated = NOW();
    `, [pricePawned - oldPrice, provision-oldProvision, goldGramsDiff])
    } catch (error) {
        console.error("Error executing query cash reg: " + error)
        throw new Error("Failed to modify cash reg")
    }

    let query = `
        UPDATE ${pawnTable}
        SET price_pawned = CAST($2 AS NUMERIC), 
            price_to_redeem = CAST($2 AS NUMERIC) + (CAST($2 AS NUMERIC) * CAST($3 AS REAL) / 100), 
            provision = CAST($3 AS REAL), 
            description = $4
    `;

    if (pawnTable === "gold_pawn") {
        query += `, weight = weight + CAST($5 AS NUMERIC)`;
    }

    query += ` WHERE id = $1 RETURNING *;`;

    const params = [pawnId, pricePawned, provision, description, goldGramsDiff];

    try {
        const { rows } = await pool.query(query, params);
        return rows[0];
    } catch (error) {
        console.error("Error executing query: ", error);
        throw error;
    }
}


async function changePawnToSale(id, pawnCategory) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(pawnCategory))
        throw new Error("Invalid pawn category in CHANGE PAWN TO SALE");

    let query = await pool.query(`SELECT provision, client_id, price_pawned FROM ${pawnCategory} WHERE id = $1;`, [id])
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
        case "electronics_pawn": query = await pool.query(`SELECT brand, year, description FROM electronics_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand} ${query.rows[0].year} ${query.rows[0].description}`;
            break;
        case "gold_pawn": query = await pool.query(`SELECT price_pawned, weight, carats, description FROM gold_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0){
                gold_grams = query.rows[0].weight;
                description = `${query.rows[0].weight}g ${query.rows[0].carats}k ${query.rows[0].price_pawned / query.rows[0].weight} per gram ${query.rows[0].description}`;
            }
            break;
        case "other_pawn": query = await pool.query(`SELECT description FROM other_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = query.rows[0].description;
            break;
        case "vehicle_pawn": query = await pool.query(`SELECT brand, model, year, description FROM vehicle_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand} ${query.rows[0].model} ${query.rows[0].year} ${query.rows[0].description}`;
            break;
        case "watch_pawn": query = await pool.query(`SELECT brand, year, description FROM watch_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand} ${query.rows[0].year} ${query.rows[0].description}`;
            break;
        default:
            throw new Error("Invalid pawn category in ADD SALE");
    }

    //Insert sale into sale table
    await pool.query(`
        INSERT INTO sale (client_id, price_bought, date_from, description)
        VALUES ($1, $2, CURRENT_TIMESTAMP, $3)
    `, [clientId, priceBought, description])

    //Delete pawn from pawn table
    await pool.query(`
        DELETE FROM ${pawnCategory}
        WHERE id = $1;
    `, [id])

    let transactionCategory = pawnCategory === "electronics_pawn" ? "Electronics" :
        pawnCategory === "gold_pawn" ? "Gold" :
            pawnCategory === "vehicle_pawn" ? "Vehicle" :
                pawnCategory === "watch_pawn" ? "Watch" : "Other";
    let transactionDescription = "Transferred pawn to sale";

    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, 0, 0, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription])

    //Update cash register with decrease numPawns, decrease moneyPawns, increase numSales, increase moneySales, update quantity of gold in grams and update average provision for pawns
    await pool.query(`
        UPDATE cash_register 
        SET
            average_provision = 
                CASE 
                    WHEN num_pawns > 1 THEN (average_provision * num_pawns - $3) / (num_pawns - 1)
                    ELSE 0
                END,
            num_pawns = num_pawns - 1,
            money_pawns = money_pawns - $1,
            num_sale_items = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            gold_grams = gold_grams - $2,
            last_updated = NOW();
    `, [priceBought, gold_grams, provision])
}

async function getAllSales(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByTel = "") {
    const { rows } = await pool.query(`
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        s.id AS "Id",
        s.description AS "About", 
        to_char(s.date_from, 'YYYY-MM-DD') AS "Date Bought",
        s.price_bought AS "Item Cost"
    FROM client c
    INNER JOIN sale s
        ON c.id = s.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND c.telephone LIKE $3 || '%'
    ORDER BY "${orderBy}" ${orderDirection}
    LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByTel])

    return rows;
}

async function getSale(clientId, saleId) {

    const { rows: saleRows } = await pool.query(`SELECT *, to_char(date_from, 'YYYY-MM-DD') AS date_from FROM sale WHERE id = $1`, [saleId]);
    const sale = saleRows[0];
    const { rows: clientRows } = await pool.query(`SELECT * FROM client WHERE id = $1`, [clientId]);
    const client = clientRows[0];
    return { sale, client };
}

async function closeSale(id, priceSold) {
    const query = await pool.query(`SELECT price_bought, client_id FROM sale WHERE id = $1;`, [id]);
    let priceBought = null;
    let clientId = null;
    if (query.rows.length > 0) {
        priceBought = query.rows[0].price_bought;
        clientId = query.rows[0].client_id;
    } else
        throw new Error("Cant find information in sale table to REMOVE SALE");


    await pool.query(`BEGIN;`)

    //Delete sale from sale table
    await pool.query(`
        DELETE FROM sale
        WHERE id = $1;
    `, [id])

    let transactionCategory = "Sale";
    let transactionDescription = "Closed sale";

    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, 0, $4, $5, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription, priceBought, priceSold-priceBought])

    //Update cash register with money inserted, decrease numSales and decrease moneySales
    await pool.query(`
        UPDATE cash_register 
        SET
            num_sale_items = num_sale_items - 1,
            money_sale_items = money_sale_items - $1,
            register_money = register_money + $2,
            last_updated = NOW();
    `, [priceBought, priceSold])

    await pool.query(`COMMIT;`)

    return priceSold;
}

async function addNewSale(saleObj, clientObj) {
    //Insert a new client if name, embg and telephone arent already a combination in another client
    await pool.query(`
        INSERT INTO client (name, embg, telephone, city)
        VALUES ($1, $2, $3, $4)
        ON CONFLICT (name, embg, telephone) DO NOTHING;
    `, [clientObj.name, clientObj.embg, clientObj.telephone, clientObj.city]);

    const query = await pool.query(`SELECT id FROM client WHERE name = $1 AND embg = $2 AND telephone = $3;`, [clientObj.name, clientObj.embg, clientObj.telephone]);
    let clientId = null;
    if (query.rows.length > 0) {
        clientId = query.rows[0].id;
    } else
        throw new Error("Cant find information in client table to ADD PAWN");

    //Insert a new sale into sale table
    await pool.query(`
        INSERT INTO sale (client_id, price_bought, date_from, description)
        VALUES ($1, $2, CURRENT_TIMESTAMP, $3)
    `, [clientId, saleObj.priceBought, saleObj.description])

    let transactionCategory = "Sale";
    let transactionDescription = "Added new sale";

    //Insert a new transaction with money given
    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES ($1, $2, $3, $4, 0, 0, CURRENT_TIMESTAMP);
    `, [clientId, transactionCategory, transactionDescription, saleObj.priceBought])

    //Update cash register with money given, increase numSales and increase moneySales
    await pool.query(`
        UPDATE cash_register 
        SET
            num_sale_items = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            register_money = register_money - $1,
            last_updated = NOW();
    `, [Number(saleObj.priceBought)])
}


async function getAllClients(limit, offset, search) {

    const { rows } = await pool.query(`
        SELECT * FROM client 
        WHERE LOWER(name) LIKE $1 
           OR embg LIKE $1 
           OR telephone LIKE $1
        LIMIT ${limit} OFFSET ${offset};
    `, [`${search}%`]);
    return rows;
}


async function getCashRegister() {
    const { rows } = await pool.query(`SELECT * FROM cash_register`);

    return rows;
}

async function insertIntoCashRegister(amount, description) {

    await pool.query(`
        UPDATE cash_register
        SET
            register_money = register_money + $1,
            last_updated = NOW()
    `, [amount])

    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, 'Insert', $1, 0, $2, 0, CURRENT_TIMESTAMP);
    `, [description, amount])
}

async function removeFromCashRegister(amount, description) {

    await pool.query(`
        UPDATE cash_register
        SET
            register_money = register_money - $1,
            last_updated = NOW()
    `, [amount])

    await pool.query(`
        INSERT INTO transaction (client_id, category, description, money_given, money_got, profit, date)
        VALUES (0, 'Remove', $1, $2, 0, 0, CURRENT_TIMESTAMP);
    `, [description, amount])
}


async function getAllExpenses(limit, offset, orderBy, orderDirection, searchByMonth = "", searchByYear = "") {
    const { rows } = await pool.query(`
    SELECT 
        e.year AS "Year",
        e.month AS "Month",
        e.rent AS "Rent",
        e.salaries AS "Salaries",
        e.other AS "Other",
        e.description AS "Description"
    FROM expense e
    WHERE ($1 = '' OR e.year = $1::INT) AND ($2 = '' OR e.month = $2::INT)
    ORDER BY "${orderBy}" ${orderDirection}
    LIMIT ${limit} OFFSET ${offset};
    `, [searchByYear, searchByMonth])

    return rows;
}

async function insertExpense(year, month, rent, salaries, other, description) {
    const { rows : hasExpense } = await pool.query(`SELECT * FROM expense WHERE year = $1::INT AND month = $2::INT`, [year, month])
    if (hasExpense.length > 0)
        return `Имаш веќе внесено расходи за месец: ${month}-${year}`

    await pool.query(`INSERT INTO expense VALUES ($1, $2, $3, $4, $5, $6)`, [year, month, rent, salaries, other, description])
    return 'Успешно внесен расход'
}


async function getAllTransactions(limit, offset, orderBy, orderDirection, searchByName = "", searchByEmbg = "", searchByDate = "") {
    const { rows } = await pool.query(`
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        c.embg AS "Embg",
        t.id AS "Id",
        t.money_given AS "Given",
        t.money_got AS "Got",
        t.profit AS "Profit",
        t.date AS "Date",
        t.category AS "Category",
        t.description AS "Description"
    FROM client c
    INNER JOIN transaction t
        ON c.id = t.client_id
    WHERE LOWER(c.name) LIKE $1 || '%' AND c.embg LIKE $2 || '%' AND ($3::DATE IS NULL OR t.date = $3::DATE)
    ORDER BY "${orderBy}" ${orderDirection}
    LIMIT ${limit} OFFSET ${offset};
    `, [searchByName, searchByEmbg, searchByDate])

    return rows;
}

async function getDailyReport(date) {
    // Get total money given, total profit, and total turnover
    const { rows: totalRows } = await pool.query(`
        SELECT 
            COUNT(*) AS "numTransactions",
            SUM(money_given) AS "moneyGiven",
            SUM(profit) AS "profit", 
            SUM(money_given + money_got) AS "turnover"
        FROM transaction 
        WHERE DATE(date) = $1 AND (category = 'Electronics' OR category = 'Watch' OR category = 'Gold' OR category = 'Vehicle' OR category = 'Other' OR category = 'Sale');
    `, [date]);

    // Get the number of transactions excluding 'Sale' category
    const { rows: numPawnsRows } = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction 
        WHERE DATE(date) = $1 AND description = 'Added new pawn' AND (category = 'Electronics' OR category = 'Watch' OR category = 'Gold' OR category = 'Vehicle' OR category = 'Other');
    `, [date]);

    // Second query: Get the number of transactions excluding 'Sale' category
    const { rows: numSaleRows } = await pool.query(`
        SELECT COUNT(*) AS "numTransactions"
        FROM transaction 
        WHERE DATE(date) = $1 AND category = 'Sale' AND (description = 'Added new sale' OR description = 'Transferred pawn to sale');
    `, [date]);

    // Get count and sums grouped by category
    const { rows: categoryRows } = await pool.query(`
        SELECT 
            category,
            COUNT(*) AS "numTransactions", 
            SUM(money_given) AS "moneyGiven",
            SUM(profit) AS "profit"
        FROM transaction 
        WHERE DATE(date) = $1 AND (category = 'Electronics' OR category = 'Watch' OR category = 'Gold' OR category = 'Vehicle' OR category = 'Other' OR category = 'Sale')
        GROUP BY category
    `, [date]);

    // Combine all the results into one object
    return {
        total: totalRows[0],
        numPawns: numPawnsRows[0],
        numSales: numSaleRows[0],
        categories: categoryRows
    };
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
    getAllClients,
    getCashRegister,
    insertIntoCashRegister,
    removeFromCashRegister,
    getAllExpenses,
    insertExpense,
    getAllTransactions,
    getDailyReport
}