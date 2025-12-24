CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    balance BIGINT NOT NULL,
    version BIGINT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_accounts_user_id ON accounts(user_id);

CREATE TABLE IF NOT EXISTS payment_operation (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    failure_reason VARCHAR(128) NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_payment_operation_order_id ON payment_operation(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_operation_user_created_at ON payment_operation(user_id, created_at DESC);

CREATE TABLE IF NOT EXISTS outbox_message (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id UUID NOT NULL,
    type VARCHAR(64) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(16) NOT NULL,
    attempts INT NOT NULL,
    last_error TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    locked_at TIMESTAMPTZ NULL,
    sent_at TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created_at ON outbox_message(status, created_at);

CREATE TABLE IF NOT EXISTS inbox_message (
    message_id UUID PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    payload TEXT NOT NULL,
    received_at TIMESTAMPTZ NOT NULL,
    processed_at TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS idx_inbox_received_at ON inbox_message(received_at);
