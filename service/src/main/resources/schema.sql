CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    CONSTRAINT PK_CATEGORIES PRIMARY KEY (category_id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    CONSTRAINT PK_LOCATIONS PRIMARY KEY (location_id)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(50) NOT NULL,
    annotation VARCHAR(100) NOT NULL,
    category_id BIGINT REFERENCES categories (category_id) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(500) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    location_id BIGINT REFERENCES locations (location_id) ON DELETE CASCADE NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(20) NOT NULL,
    CONSTRAINT PK_EVENTS PRIMARY KEY (event_id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    event_id BIGINT REFERENCES events (event_id) ON DELETE CASCADE NOT NULL,
    requester BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP  NOT NULL,
    CONSTRAINT PARTICIPATION_REQUESTS_PK PRIMARY KEY (event_id, requester)
);

CREATE TABLE IF NOT EXISTS compilations
(
  compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  pinned BOOLEAN NOT NULL,
  title VARCHAR(200) NOT NULL,
  CONSTRAINT PK_COMPILATIONS PRIMARY KEY (compilation_id)
);

CREATE TABLE IF NOT EXISTS compilation_events (
  compilation_id BIGINT REFERENCES compilations (compilation_id) ON DELETE CASCADE NOT NULL,
  event_id BIGINT REFERENCES events (event_id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT COMPILATIONS_EVENTS_PK PRIMARY KEY (event_id, compilation_id)
);