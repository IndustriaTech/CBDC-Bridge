package com.cbdc.industria.tech.bridge.services

const val TIMEOUT_MILLISECONDS = 60_000
const val AUTH_HEADER_KEY = "X-API-Key"
const val THREADS_COUNT = 5
const val X_ENV_ID = "x-env-id"
const val X_CURRENCY_ID = "x-currency-id"
const val X_PIP_ID = "x-pip-id"
const val CONTENT_TYPE = "Content-Type"
const val APPLICATION_JSON = "application/json"
const val X_REQUESTING_BANKING_ENTITY_ID = "x-requesting-banking-entity-id"
const val X_CONSENT_ID = "x-consent-id"
const val PAGE_INDEX_KEY = "pageIndex"
const val PAGE_SIZE_KEY = "pageSize"
const val MAX_PAGE_SIZE = 1000

val AUTH_TOKEN: String = System.getenv("BARCLAYS_AUTH_TOKEN")
const val HOST_URL = "https://cbdchackathon-dev.barclays.nayaone.com"

// Currencies
const val CURRENCIES = "currencies"

// CBDC accounts
const val CBDC_ACCOUNTS = "cbdc-ledger/accounts"
const val CBDC_ACCOUNTS_DEPOSIT = "deposit"
const val CBDC_ACCOUNTS_WITHDRAWAL = "withdrawal"

// Commercial Banks
const val COMMERCIAL_BANKS = "commercial-banks"
const val COMMERCIAL_BANKS_PARTIES = "parties"
const val COMMERCIAL_BANKS_ACCOUNTS = "accounts"
const val COMMERCIAL_BANKS_ACCOUNTS_WITHDRAWAL = "withdrawal"
const val COMMERCIAL_BANKS_ACCOUNTS_DEPOSIT = "deposit"

// Open Banking: Payment Initiation Service Provider (PISP)
const val PISP = "pisp"
const val DOMESTIC_PAYMENTS = "domestic-payments"
const val DOMESTIC_PAYMENT_CONSENTS = "domestic-payment-consents"

// Open Banking Account Information
const val OPEN_BANKING_ACCOUNT_INFORMATION = "open-banking/aisp"
const val ACCESS_CONSENTS = "account-access-consents"
const val ACCOUNTS = "accounts"
const val PARTY = "party"

//Ecosystem
const val ECOSYSTEMS = "domestic-payments"

// Payment Interface Providers (PIPs)
const val PIPS = "pips"
const val PIPS_PARTIES = "parties"
const val PIPS_ACCOUNTS = "accounts"
const val PIPS_ACCOUNTS_WITHDRAWAL = "withdrawal"
const val PIPS_ACCOUNTS_DEPOSIT = "deposit"