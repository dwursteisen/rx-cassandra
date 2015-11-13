
CREATE KEYSPACE demo WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 };

USE demo;

CREATE TABLE demo_table (
    user_id text,
    timestamp timeuuid,
    data text,

    PRIMARY KEY ((user_id), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

