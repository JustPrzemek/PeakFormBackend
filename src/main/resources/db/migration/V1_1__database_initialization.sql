CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255),
                       username VARCHAR(100) UNIQUE NOT NULL,
                       profile_image_url VARCHAR(512),
                       profile_bio TEXT,
                       bio_title VARCHAR(255),
                       location VARCHAR(100),
                       gender VARCHAR(50) CHECK (gender IN ('MALE', 'FEMALE')),
                       date_of_birth DATE CHECK (date_of_birth < CURRENT_DATE),
                       weight FLOAT CHECK (weight > 0),
                       height FLOAT CHECK (height > 0),
                       goal VARCHAR(50) CHECK (goal IN ('reduction', 'bulk', 'maintenance')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       auth_provider VARCHAR(20) DEFAULT 'local',
                       provider_id VARCHAR(255),
                       refresh_token VARCHAR(255),
                       role VARCHAR(20) DEFAULT 'USER',
                       is_enabled BOOLEAN DEFAULT true,
                       email_verification_token VARCHAR(255),
                       is_email_verified BOOLEAN DEFAULT false,
                       password_reset_token VARCHAR(255),
                       password_reset_expires TIMESTAMP,
                       last_password_reset_request TIMESTAMP,
                       reset_attempts_today INT,
                       reset_attempts_date DATE,
                       active_workout_plan_id BIGINT

);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_users_username_trgm ON users
    USING GIN (username gin_trgm_ops);

CREATE UNIQUE INDEX idx_provider_id ON users (auth_provider, provider_id)
    WHERE auth_provider != 'local' AND provider_id IS NOT NULL;

CREATE TABLE meals (
                       id SERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       calories FLOAT CHECK (calories >= 0),
                       protein FLOAT CHECK (protein >= 0),
                       carbs FLOAT CHECK (carbs >= 0),
                       fat FLOAT CHECK (fat >= 0),
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE exercises (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           muscle_group VARCHAR(100) NOT NULL,
                           difficulty VARCHAR(50),
                           description TEXT,
                           video_url VARCHAR(512),
                           type VARCHAR(50) DEFAULT 'STRENGTH'
);

CREATE TABLE workout_plans (
                               id SERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               name VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               description TEXT,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE training_sessions (
                                   id SERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   plan_id BIGINT,
                                   start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   end_time TIMESTAMP,
                                   notes TEXT,
                                   day_identifier VARCHAR(50) NOT NULL DEFAULT 'A',
                                   duration BIGINT,
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                   FOREIGN KEY (plan_id) REFERENCES workout_plans(id) ON DELETE SET NULL
);

CREATE TABLE exercise_logs (
                               id SERIAL PRIMARY KEY,
                               session_id BIGINT NOT NULL,
                               exercise_id BIGINT NOT NULL,
                               set_number INT,
                               reps INT,
                               duration_minutes INT CHECK ( duration_minutes > 0 ),
                               distance_km FLOAT CHECK ( distance_km > 0 ),
                               weight FLOAT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (session_id) REFERENCES training_sessions(id) ON DELETE CASCADE,
                               FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);

CREATE TABLE workout_plan_exercises (
                                        id SERIAL PRIMARY KEY,
                                        workout_plan_id BIGINT NOT NULL,
                                        exercise_id BIGINT NOT NULL,
                                        sets INT CHECK (sets > 0),
                                        reps INT CHECK (reps > 0),
                                        duration_minutes INT CHECK ( duration_minutes > 0 ),
                                        distance_km FLOAT CHECK ( distance_km > 0 ),
                                        rest_time INT CHECK (rest_time >= 0),
                                        day_identifier VARCHAR(50) NOT NULL DEFAULT 'A',
                                        FOREIGN KEY (workout_plan_id) REFERENCES workout_plans(id) ON DELETE CASCADE,
                                        FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);


CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       content TEXT NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       media_url VARCHAR(512),
                       media_type VARCHAR(50),
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE post_likes (
                            id SERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            post_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            UNIQUE (user_id, post_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE followers (
                           id SERIAL PRIMARY KEY,
                           follower_id BIGINT NOT NULL,
                           followed_id BIGINT NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE (follower_id, followed_id),
                           FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE weight_entries (
                                id SERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                weight FLOAT NOT NULL CHECK (weight > 0),
                                recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users
    ADD CONSTRAINT fk_users_active_workout_plan
        FOREIGN KEY (active_workout_plan_id) REFERENCES workout_plans(id) ON DELETE SET NULL;