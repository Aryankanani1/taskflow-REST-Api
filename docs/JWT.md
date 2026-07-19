# JWT (JSON Web Token) — How It Works

A **JWT** is a compact, self-contained token used to securely transmit identity
and claims between two parties. In this project it's used for **stateless
authentication**: after login, the server hands the client a signed token; the
client sends it back on every request, and the server trusts it *without storing
any session*.

---

## 1. Structure

A JWT is **three Base64URL-encoded parts joined by dots**:

```
JWT = HEADER . PAYLOAD . SIGNATURE
```

Example (shortened):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiZXhwIjoxNzIwMDAwMDAwfQ.dQw4w9WgXcQ...
└─────────── HEADER ───────────┘ └──────────── PAYLOAD ───────────┘ └── SIGNATURE ──┘
```

When sent in an HTTP request, it's placed in the header as a **Bearer token**:

```
Authorization: Bearer <JWT>
```

---

## 2. The Formula

```
encodedHeader  = Base64URL(header JSON)
encodedPayload = Base64URL(payload JSON)

signature      = HMAC_SHA256( encodedHeader + "." + encodedPayload , SECRET_KEY )

JWT = encodedHeader + "." + encodedPayload + "." + Base64URL(signature)
```

- `SECRET_KEY` — a **256-bit** secret, created and held **only by the server/developer**.
- `HMAC_SHA256` (HS256) — a keyed hash: same input + same key → same output, but
  you cannot forge it without the key.

---

## 3. The Three Parts

### HEADER — *how* the token is signed

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

- `alg` — signing algorithm (here HMAC-SHA256).
- `typ` — token type (`JWT`).

### PAYLOAD — *the claims* (who + metadata)

```json
{
  "sub": "john",
  "exp": 1720000000
}
```

- `sub` — subject (the user this token represents).
- `exp` — expiry as a **NumericDate** (seconds since 1970-01-01 UTC). After this
  time the token is invalid.
- Other common claims: `iat` (issued-at), `iss` (issuer), `roles`, etc.

> ⚠️ **The payload is NOT secret.** Anyone can decode it (see below). Never put
> passwords or sensitive data in it.

### SIGNATURE — *proof it wasn't tampered with*

```
signature = HMAC_SHA256(encodedHeader + "." + encodedPayload, SECRET_KEY)
```

The signature is what makes a JWT trustworthy: change one character of the header
or payload and the signature no longer matches, so the server rejects it.

---

## 4. Base64URL is Encoding, NOT Encryption

**Base64URL is just a text converter**, so any system can safely carry the bytes
as plain ASCII. It provides **no security** — it's fully reversible by anyone.

The alphabet is 64 characters (6 bits each):

| Range | Characters | Count |
|-------|-----------|-------|
| Uppercase | `A`–`Z` | 26 |
| Lowercase | `a`–`z` | 26 |
| Digits | `0`–`9` | 10 |
| Symbols (URL-safe) | `-` and `_` | 2 |

> **Base64 vs Base64URL:** standard Base64 uses `+` and `/` and `=` padding.
> **Base64URL** replaces `+` → `-`, `/` → `_`, and **drops** the `=` padding, so
> the token is safe inside a URL/HTTP header.

---

## 5. Worked Example — Encoding `"alg"`

Take the 3 characters `a`, `l`, `g` and turn them into Base64.

**Step 1 — ASCII codes:**

| Char | ASCII |
|------|-------|
| `a`  | 97 |
| `l`  | 108 |
| `g`  | 103 |

**Step 2 — 8-bit binary (each byte):**

```
a = 97  -> 0 1 1 0 0 0 0 1
l = 108 -> 0 1 1 0 1 1 0 0
g = 103 -> 0 1 1 0 0 1 1 1
```

*(reading a byte: 97 = 64 + 32 + 1 = 2^6 + 2^5 + 2^0 → 01100001)*

**Step 3 — concatenate into 24 bits, then REGROUP into 6-bit chunks:**

```
8-bit :  01100001 01101100 01100111
6-bit :  011000  010110  110001  100111
value :   24      22      49      39
```

**Step 4 — map each 6-bit value through the Base64 table:**

| 6-bit value | Base64 char |
|-------------|-------------|
| 24 | `Y` |
| 22 | `W` |
| 49 | `x` |
| 39 | `n` |

**Result:** `"alg"` → **`YWxn`**

That's the whole trick: **8-bit bytes are re-sliced into 6-bit groups**, and each
group becomes one Base64 character. The real header does this over the *entire*
JSON `{"alg":"HS256","typ":"JWT"}`, producing `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`.

The payload is built the exact same way, and the two encoded strings are joined
with a `.` before being signed.

---

## 6. How Verification Works (the important part)

The server does **not** "decrypt" the token. On each request it:

1. Splits the token into `header.payload.signature`.
2. Recomputes `HMAC_SHA256(header + "." + payload, SECRET_KEY)` using its own key.
3. **Compares** the recomputed signature to the one in the token.
   - Match → the token is authentic and untampered.
   - No match → reject (someone edited it or used the wrong key).
4. Checks `exp` — if expired, reject.

Because only the server knows `SECRET_KEY`, an attacker can *read* the payload but
**cannot produce a valid signature** for modified claims. That's the security model.

---

## 7. Why JWT? (Stateless Auth)

```
Client                          Server
  |  POST /login (email+pass)     |
  | ----------------------------> |  verify password (BCrypt.matches)
  |                               |  build + sign JWT
  |  <---------------------------- |  returns JWT
  |                               |
  |  GET /tasks                   |
  |  Authorization: Bearer <JWT>  |
  | ----------------------------> |  verify signature + exp
  |  <---------------------------- |  200 OK (no DB session lookup)
```

The server keeps **no session state** — everything it needs is inside the signed
token. This scales horizontally (any server instance can verify any token).

---

## 8. Key Points for Interviews

- A JWT has **three parts**: `header.payload.signature`, each Base64URL-encoded.
- **Base64URL is encoding, not encryption** — the payload is readable by anyone.
- **Security comes from the signature**, not from hiding the data.
- The **secret key never leaves the server**; it's what makes signatures unforgeable.
- **Never store secrets** (passwords, card numbers) in the payload.
- Always set an **`exp`** so tokens don't live forever.
- JWT enables **stateless authentication** — no server-side sessions.
- If the secret leaks, **every token can be forged** — rotate it and treat it like
  a password (kept in an env var, never committed).
