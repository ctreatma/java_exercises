/*  Charles Treatman      assign1.sql  */
/* Output formatting... */
set pagesize 50;
set linesize 140;
column action format a6;
column quantity heading QTY format 9990;
column quantitytype heading QTYPE format a6;
column amount format 9990;
column person format a16;
column item format a19;

/* -1- */
SELECT * FROM jdonalds.ledger WHERE actiondate LIKE '%OCT%';

/* -2- */
SELECT * FROM jdonalds.ledger WHERE quantity * rate != amount;

/* -3- */
SELECT UNIQUE person FROM jdonalds.ledger ORDER BY person ASC;

/* -4- */
SELECT UNIQUE item FROM jdonalds.ledger WHERE action LIKE 'SOLD' ORDER BY item ASC;

/* -5- */
SELECT actiondate, action, item FROM jdonalds.ledger WHERE action LIKE 'SOLD' AND person LIKE 'GEN%' ORDER BY actiondate DESC;

/* -6- */
/* This looks for transactions involving Donald Rollo, and displays the actiondate, action and person */
select actiondate, action, person from jdonalds.ledger where person like 'DONALD ROLLO';

/* -7- */
/* List small transactions  */
select * from jdonalds.ledger where quantity < 10;