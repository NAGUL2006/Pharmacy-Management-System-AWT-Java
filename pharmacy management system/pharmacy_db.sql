-- SQLite schema for Pharmacy Management System
-- Run automatically by the app at startup, but provided here for reference/manual setup

CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT UNIQUE NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medicines (
  med_id INTEGER PRIMARY KEY AUTOINCREMENT,
  med_name TEXT NOT NULL,
  company TEXT,
  quantity INTEGER NOT NULL DEFAULT 0,
  price REAL NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS customers (
  cust_id INTEGER PRIMARY KEY AUTOINCREMENT,
  cust_name TEXT NOT NULL,
  phone TEXT
);

CREATE TABLE IF NOT EXISTS bills (
  bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
  cust_id INTEGER,
  total REAL NOT NULL,
  bill_date TEXT DEFAULT (datetime('now')),
  FOREIGN KEY(cust_id) REFERENCES customers(cust_id)
);

CREATE TABLE IF NOT EXISTS bill_items (
  item_id INTEGER PRIMARY KEY AUTOINCREMENT,
  bill_id INTEGER NOT NULL,
  med_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  price REAL NOT NULL,
  FOREIGN KEY(bill_id) REFERENCES bills(bill_id),
  FOREIGN KEY(med_id) REFERENCES medicines(med_id)
);
