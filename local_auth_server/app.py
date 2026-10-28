"""Local authentication API matching the Android AuthApiService DTOs."""

import hashlib
import os
from pathlib import Path
import secrets
import sqlite3
import time

from flask import Flask, g, jsonify, request
from werkzeug.security import check_password_hash, generate_password_hash


def create_app(database=None):
    app = Flask(__name__)
    app.config.update(
        DATABASE=str(database or Path(__file__).with_name("auth.sqlite3")),
        MAX_CONTENT_LENGTH=16 * 1024,
        ACCESS_TTL=15 * 60,
        REFRESH_TTL=30 * 24 * 60 * 60,
    )

    def db():
        if "db" not in g:
            g.db = sqlite3.connect(app.config["DATABASE"])
            g.db.row_factory = sqlite3.Row
            g.db.execute("PRAGMA foreign_keys = ON")
        return g.db

    @app.teardown_appcontext
    def close_db(_error):
        connection = g.pop("db", None)
        if connection is not None:
            connection.close()

    with app.app_context():
        db().executescript("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL
            );
            CREATE TABLE IF NOT EXISTS tokens (
                digest TEXT PRIMARY KEY,
                user_id INTEGER NOT NULL REFERENCES users(id),
                kind TEXT NOT NULL,
                expires_at INTEGER NOT NULL
            );
        """)

    def error(message, status):
        return jsonify(success=False, message=message), status

    def credentials():
        body = request.get_json(silent=True)
        if not isinstance(body, dict):
            return None
        email, password = body.get("email"), body.get("password")
        if not isinstance(email, str) or not isinstance(password, str):
            return None
        email = email.strip().lower()
        if not email or "@" not in email or not password:
            return None
        return email, password

    def digest(token):
        return hashlib.sha256(token.encode()).hexdigest()

    def issue_tokens(user_id):
        tokens = {}
        now = int(time.time())
        db().execute("DELETE FROM tokens WHERE expires_at <= ?", (now,))
        for kind, ttl in (("access", app.config["ACCESS_TTL"]),
                          ("refresh", app.config["REFRESH_TTL"])):
            token = secrets.token_urlsafe(32)
            db().execute(
                "INSERT INTO tokens VALUES (?, ?, ?, ?)",
                (digest(token), user_id, kind, now + ttl),
            )
            tokens[kind] = token
        return tokens

    @app.post("/auth/registration")
    def register():
        values = credentials()
        if values is None:
            return error("JSON email and non-empty password are required", 400)
        email, password = values
        password_hash = generate_password_hash(password)
        try:
            with db():
                user_id = db().execute(
                    "INSERT INTO users (email, password_hash) VALUES (?, ?)",
                    (email, password_hash),
                ).lastrowid
                tokens = issue_tokens(user_id)
        except sqlite3.IntegrityError:
            return error("Email is already registered", 409)
        return jsonify(userid=user_id, accessToken=tokens["access"],
                       refreshToken=tokens["refresh"]), 201

    @app.post("/auth/login")
    def login():
        values = credentials()
        if values is None:
            return error("JSON email and non-empty password are required", 400)
        email, password = values
        user = db().execute("SELECT * FROM users WHERE email = ?", (email,)).fetchone()
        if user is None or not check_password_hash(user["password_hash"], password):
            return error("Invalid email or password", 401)
        with db():
            tokens = issue_tokens(user["id"])
        return jsonify(user_id=user["id"], access_token=tokens["access"],
                       refresh_token=tokens["refresh"])

    @app.post("/auth/refresh")
    def refresh():
        token = request.form.get("refresh_token", "")
        if not token:
            return error("Form field refresh_token is required", 400)
        with db():
            # A single DELETE makes refresh tokens single-use, including concurrent requests.
            user = db().execute(
                "DELETE FROM tokens WHERE digest = ? AND kind = 'refresh' "
                "AND expires_at > ? RETURNING user_id",
                (digest(token), int(time.time())),
            ).fetchone()
            if user is None:
                return error("Invalid or expired refresh token", 401)
            tokens = issue_tokens(user["user_id"])
        return jsonify(accessToken=tokens["access"], refreshToken=tokens["refresh"])

    @app.get("/auth/check")
    def check():
        header = request.headers.get("Authorization", "").strip()
        parts = header.split()
        token = parts[1] if len(parts) == 2 and parts[0].lower() == "bearer" else header
        valid = db().execute(
            "SELECT 1 FROM tokens WHERE digest = ? AND kind = 'access' AND expires_at > ?",
            (digest(token), int(time.time())),
        ).fetchone() is not None
        # The Android repository checks the HTTP status, not the is_valid field.
        return jsonify(is_valid=valid, success=valid), 200 if valid else 401

    @app.post("/auth/recovery")
    def recovery():
        email = request.form.get("email", "").strip()
        if not email or "@" not in email:
            return error("Form field email is required", 400)
        # Development stub: no mail delivery or password modification.
        return "Local recovery stub: no email sent and password unchanged.", 200

    return app


if __name__ == "__main__":
    create_app().run(host=os.environ.get("HOST", "127.0.0.1"),
                     port=int(os.environ.get("PORT", "5000")), debug=False)
