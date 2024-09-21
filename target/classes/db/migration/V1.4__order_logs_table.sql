-- Order Logs Table
CREATE TABLE order_logs (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    processed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_log_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);