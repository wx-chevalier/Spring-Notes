CREATE TABLE IF NOT EXISTS var_table
(
    name          VARCHAR(255) NOT NULL,
    ts            BIGINT,
    integer_val   INTEGER,
    long_val      BIGINT,
    double_val    DOUBLE,
    boolean_val   TINYINT,
    datetime_val  DATETIME,
    date_val      DATE,
    timestamp_val TIMESTAMP
);