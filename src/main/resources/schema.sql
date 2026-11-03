CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS seeker_account (
    seeker_account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bio VARCHAR(1000),
    CONSTRAINT uk_seeker_account_user UNIQUE (user_id),
    CONSTRAINT fk_seeker_account_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS provider_account (
    provider_account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bio TEXT,
    availability VARCHAR(255),
    portfolio TEXT,
    rating DECIMAL(10, 2),
    CONSTRAINT uk_provider_account_user UNIQUE (user_id),
    CONSTRAINT fk_provider_account_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS service_listings (
    service_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    rating DECIMAL(10, 2),
    CONSTRAINT fk_service_listings_provider
        FOREIGN KEY (provider_id) REFERENCES provider_account(provider_account_id)
);

CREATE TABLE IF NOT EXISTS booking (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL,
    seeker_id BIGINT NOT NULL,
    booking_date TIMESTAMP NOT NULL,
    booking_end_time TIMESTAMP NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    decline_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_booking_service
        FOREIGN KEY (service_id) REFERENCES service_listings(service_id),
    CONSTRAINT fk_booking_seeker
        FOREIGN KEY (seeker_id) REFERENCES seeker_account(seeker_account_id)
);

CREATE TABLE IF NOT EXISTS review (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    CONSTRAINT uk_review_booking UNIQUE (booking_id),
    CONSTRAINT fk_review_booking
        FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

CREATE TABLE IF NOT EXISTS conversation (
    conversation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_conversation_participants
        UNIQUE (seeker_id, provider_id),

    CONSTRAINT fk_conversation_seeker
        FOREIGN KEY (seeker_id) REFERENCES users(user_id),

    CONSTRAINT fk_conversation_provider
        FOREIGN KEY (provider_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS message (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversation(conversation_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(user_id)
);
