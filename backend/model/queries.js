const pool = require('./pool');

async function getAllPawns(orderBy, orderDirection) {
    const { rows } = await pool.query(`
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Electronics' AS "Category", 
        ep.id AS "Id",
        ep.brand || ' ' || ep.year AS "About", 
        ep.date_to AS "Valid Until", 
        (ep.date_to - CURRENT_DATE) AS "Days Left",  
        ep.price_pawned * ep.provision / 100 AS "Provision", 
        ep.price_pawned AS "Item Cost"
    FROM client c
    INNER JOIN electronics_pawn ep
        ON c.id = ep.client_id
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Gold' AS "Category", 
        gp.id AS "Id",
        gp.weight || 'g ' || gp.carats || 'k ' || gp.type AS "About", 
        gp.date_to AS "Valid Until", 
        (gp.date_to - CURRENT_DATE) AS "Days Left", 
        gp.price_pawned * gp.provision / 100 AS "Provision", 
        gp.price_pawned AS "Item Cost"
    FROM client c
    INNER JOIN gold_pawn gp
        ON c.id = gp.client_id
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Other' AS "Category", 
        op.id AS "Id",
        op.description AS "About", 
        op.date_to AS "Valid Until", 
        (op.date_to - CURRENT_DATE) AS "Days Left", 
        op.price_pawned * op.provision / 100 AS "Provision", 
        op.price_pawned AS "Item Cost"
    FROM client c
    INNER JOIN other_pawn op
        ON c.id = op.client_id
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Vehicle' AS "Category", 
        vp.id AS "Id",
        vp.brand || ' ' || vp.model || ' ' || vp.year AS "About", 
        vp.date_to AS "Valid Until", 
        (vp.date_to - CURRENT_DATE) AS "Days Left",
        vp.price_pawned * vp.provision / 100 AS "Provision", 
        vp.price_pawned AS "Item Cost"
    FROM client c
    INNER JOIN vehicle_pawn vp
        ON c.id = vp.client_id
    
    UNION ALL
    
    SELECT 
        c.id AS "Client Id", 
        c.name AS "Name", 
        'Watch' AS "Category", 
        wp.id AS "Id",
        wp.brand || ' ' || wp.year AS "About", 
        wp.date_to AS "Valid Until", 
        (wp.date_to - CURRENT_DATE) AS "Days Left",
        wp.price_pawned * wp.provision / 100 AS "Provision", 
        wp.price_pawned AS "Item Cost"
    FROM client c
    INNER JOIN watch_pawn wp
        ON c.id = wp.client_id
    
    ORDER BY "${orderBy}" ${orderDirection};
    `);
    console.log(rows);
    return rows;
}

async function getAllSales() {
    const { rows } = await pool.query(`
    SELECT 
        c.id AS "Id", 
        c.name AS "Name", 
        'Sale' AS "Category", 
        s.description AS "About", 
        s.date_from AS "Date Bought",
        s.price_bought AS "Item Cost"
    FROM client c
    INNER JOIN sale s
        ON c.id = s.client_id
    ORDER BY "Date Bought" ASC;
    `)

    return rows;
}

async function continuePawn(id, tableName) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in CONTINUE PAWN");
    }

    let query = await pool.query(`SELECT (price_pawned * provision / 100) AS provision_money, client_id FROM ${tableName} WHERE id = $1;`, [id])
    let provisionMoney = null;
    let clientId = null;
    if (query.rows.length > 0) {
        provisionMoney = query.rows[0].provision_money;
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

    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, 0, 0, $2, CURRENT_DATE);
    `, [clientId, provisionMoney])

    //Update cash register with money inserted
    await pool.query(`
        UPDATE cash_register 
        SET
            register_money = register_money + $1,
            last_updated = NOW();
    `, [provisionMoney])

    await pool.query(`COMMIT;`)
}

async function addNewPawn(pawnCategory, pawnObj, clientObj) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(pawnCategory)) {
        throw new Error("Invalid pawn category in ADD NEW PAWN");
    }

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

    //Insert pawn in pawn table
    switch (pawnCategory) {
        case 'electronics_pawn': await pool.query(`
            INSERT INTO electronics_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10);
        `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date_from, pawnObj.date_to, pawnObj.total_days, pawnObj.description]);
            break;
        case 'gold_pawn': await pool.query(`
            INSERT INTO gold_pawn (client_id, weight, carats, price_per_gram, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description, type)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12);
        `, [clientId, pawnObj.weight, pawnObj.carats, pawnObj.price_per_gram, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date_from, pawnObj.date_to, pawnObj.total_days, pawnObj.description, pawnObj.type]);
            break;
        case 'other_pawn': await pool.query(`
            INSERT INTO other_pawn (client_id, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8);
        `, [clientId, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date_from, pawnObj.date_to, pawnObj.total_days, pawnObj.description]);
            break;
        case 'vehicle_pawn': await pool.query(`
            INSERT INTO vehicle_pawn (client_id, brand, model, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11);
        `, [clientId, pawnObj.brand, pawnObj.model, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date_from, pawnObj.date_to, pawnObj.total_days, pawnObj.description]);
            break;
        case 'watch_pawn': await pool.query(`
            INSERT INTO watch_pawn (client_id, brand, year, price_pawned, price_to_redeem, provision, date_from, date_to, total_days, description)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10);
        `, [clientId, pawnObj.brand, pawnObj.year, pawnObj.price_pawned, pawnObj.price_to_redeem, pawnObj.provision, pawnObj.date_from, pawnObj.date_to, pawnObj.total_days, pawnObj.description]);
            break;
        default:
            throw new Error("Invalid pawn table name to ADD PAWN");
    }

    //Insert a new transaction where money is given to client for pawn
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, $2, 0, 0, CURRENT_DATE);
    `, [clientId, pawnObj.price_pawned])

    //Update cash register with money taken, increase numPawns and increase moneyPawns
    await pool.query(`
        UPDATE cash_register 
        SET
            num_pawns = num_pawns + 1,
            money_pawns = money_pawns + $1,
            register_money = register_money - $1,
            last_updated = NOW();
    `, [pawnObj.price_pawned]);
}

async function closePawn(id, tableName) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(tableName)) {
        throw new Error("Invalid table name in REMOVE PAWN");
    }

    const query = await pool.query(`SELECT (price_pawned * provision / 100) AS provision_money, price_pawned, client_id FROM ${tableName} WHERE id = $1;`, [id])
    let provisionMoney = null;
    let pawnMoney = null;
    let clientId = null;
    if (query.rows.length > 0) {
        provisionMoney = query.rows[0].provision_money;
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

    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, 0, $2, $3, CURRENT_DATE);
    `, [clientId, pawnMoney, provisionMoney])

    //Update cash register with money inserted, decrease numPawns and decrease moneyPawns
    await pool.query(`
        UPDATE cash_register 
        SET
            num_pawns = num_pawns - 1,
            money_pawns = money_pawns - $1,
            register_money = register_money + $1 + $2,
            last_updated = NOW();
    `, [pawnMoney, provisionMoney])

    await pool.query(`COMMIT;`)
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

    //Insert a new transaction with cash inserted and profit made
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, 0, $2, $3, CURRENT_DATE);
    `, [clientId, priceBought, priceSold-priceBought])

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
}

async function changePawnToSale(id, pawnCategory) {
    const validTables = ["electronics_pawn", "gold_pawn", "vehicle_pawn", "other_pawn", "watch_pawn"];
    if (!validTables.includes(pawnCategory))
        throw new Error("Invalid pawn category in CHANGE PAWN TO SALE");

    let query = await pool.query(`SELECT (price_pawned * provision / 100) AS provision_money, client_id, price_pawned FROM ${pawnCategory} WHERE id = $1;`, [id])
    let clientId = null;
    let priceBought = null;
    let provisionMoney = null;
    if (query.rows.length > 0) {
        clientId = query.rows[0].client_id;
        priceBought = query.rows[0].price_pawned;
        provisionMoney = query.rows[0].provision_money;
    } else {
        throw new Error("Cant find information in pawn table to CHANGE PAWN TO SALE");
    }

    let description = null;
    switch (pawnCategory) {
        case "electronics_pawn": query = await pool.query(`SELECT brand, year, description FROM electronics_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].brand} ${query.rows[0].year} ${query.rows[0].description}`;
            break;
        case "gold_pawn": query = await pool.query(`SELECT weight, carats, price_per_gram, description FROM gold_pawn WHERE id = $1;`, [id])
            if (query.rows.length > 0)
                description = `${query.rows[0].weight}g ${query.rows[0].carats}k ${query.rows[0].price_per_gram} per gram ${query.rows[0].description}`;
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
        VALUES ($1, $2, CURRENT_DATE, $3)
    `, [clientId, priceBought, description])

    //Delete pawn from pawn table
    await pool.query(`
        DELETE FROM ${pawnCategory}
        WHERE id = $1;
    `, [id])

    //Insert a new transaction with profit made
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, 0, 0, $2, CURRENT_DATE);
    `, [clientId, provisionMoney])

    //Update cash register with money inserted, decrease numPawns, decrease moneyPawns, increase numSales and increase moneySales
    await pool.query(`
        UPDATE cash_register 
        SET
            num_pawns = num_pawns - 1,
            money_pawns = money_pawns - $1,
            num_sale_items = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            register_money = register_money + $2,
            last_updated = NOW();
    `, [priceBought, provisionMoney])
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
        VALUES ($1, $2, CURRENT_DATE, $3)
    `, [clientId, saleObj.priceBought, saleObj.description])

    //Insert a new transaction with money given
    await pool.query(`
        INSERT INTO transaction (client_id, money_given, money_got, profit, date)
        VALUES ($1, $2, 0, 0, CURRENT_DATE);
    `, [clientId, saleObj.priceBought])

    //Update cash register with money given, increase numSales and increase moneySales
    await pool.query(`
        UPDATE cash_register 
        SET
            num_sale_items = num_sale_items + 1,
            money_sale_items = money_sale_items + $1,
            register_money = register_money - $1,
            last_updated = NOW();
    `, [saleObj.priceBought])
}

module.exports = {
    getAllPawns,
    getAllSales,
    continuePawn,
    addNewPawn,
    closePawn,
    closeSale,
    changePawnToSale,
    addNewSale
}