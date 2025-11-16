
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
                       active_workout_plan_id BIGINT,
                       generation_attempts_today INT DEFAULT 0,
                       last_generation_attempt_date DATE,
                       activity_level VARCHAR(50) NOT NULL DEFAULT 'LIGHT'
                           CHECK (activity_level IN ('SEDENTARY', 'LIGHT', 'MODERATE', 'ACTIVE', 'VERY_ACTIVE'))
);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_users_username_trgm ON users
    USING GIN (username gin_trgm_ops);

CREATE UNIQUE INDEX idx_provider_id ON users (auth_provider, provider_id)
    WHERE auth_provider != 'local' AND provider_id IS NOT NULL;

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
                               goal VARCHAR(50) CHECK (goal IN ('reduction', 'bulk', 'maintenance')),
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

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          barcode VARCHAR(100) UNIQUE,
                          external_api_id VARCHAR(255),
                          brand VARCHAR(255),
                          user_id BIGINT,
                          calories_per_100g FLOAT NOT NULL CHECK (calories_per_100g >= 0),
                          protein_per_100g FLOAT NOT NULL CHECK (protein_per_100g >= 0),
                          carbs_per_100g FLOAT NOT NULL CHECK (carbs_per_100g >= 0),
                          fat_per_100g FLOAT NOT NULL CHECK (fat_per_100g >= 0),
                          default_unit VARCHAR(20) NOT NULL DEFAULT 'g',

                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_products_name_trgm ON products
    USING GIN (name gin_trgm_ops);
CREATE INDEX idx_products_external_api_id ON products (external_api_id);


CREATE TABLE recipes (
                         id SERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         servings FLOAT NOT NULL DEFAULT 1.0 CHECK (servings > 0),

                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE recipe_products (
                                 id SERIAL PRIMARY KEY,
                                 recipe_id BIGINT NOT NULL,
                                 product_id BIGINT NOT NULL,
                                 quantity FLOAT NOT NULL CHECK (quantity > 0),
                                 unit VARCHAR(50) NOT NULL DEFAULT 'g',

                                 FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                                 FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TYPE meal_type_enum AS ENUM ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK');


CREATE TABLE daily_food_log (
                                id SERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                date DATE NOT NULL DEFAULT CURRENT_DATE,
                                meal_type meal_type_enum NOT NULL,

                                product_id BIGINT,
                                recipe_id BIGINT,

                                quantity FLOAT NOT NULL CHECK (quantity > 0),
                                unit VARCHAR(50) NOT NULL, -- 'g', 'ml', 'porcja'

                                calories_eaten FLOAT NOT NULL CHECK (calories_eaten >= 0),
                                protein_eaten FLOAT NOT NULL CHECK (protein_eaten >= 0),
                                carbs_eaten FLOAT NOT NULL CHECK (carbs_eaten >= 0),
                                fat_eaten FLOAT NOT NULL CHECK (fat_eaten >= 0),

                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
                                FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE SET NULL,

                                CONSTRAINT chk_log_entry_type CHECK (
                                    (product_id IS NOT NULL AND recipe_id IS NULL) OR
                                    (product_id IS NULL AND recipe_id IS NOT NULL)
                                    )
);

CREATE INDEX idx_daily_food_log_user_date ON daily_food_log (user_id, date);


CREATE TABLE weight_log (
                            id SERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            date DATE NOT NULL DEFAULT CURRENT_DATE,
                            weight FLOAT NOT NULL CHECK (weight > 0),

                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                            UNIQUE(user_id, date)
);

CREATE INDEX idx_weight_log_user_date_desc ON weight_log (user_id, date DESC);

ALTER TABLE users
    ADD CONSTRAINT fk_users_active_workout_plan
        FOREIGN KEY (active_workout_plan_id) REFERENCES workout_plans(id) ON DELETE SET NULL;