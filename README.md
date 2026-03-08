# Fetcharr

## What is it?

Since Huntarr died, I still needed an application that scanned and upgraded my media.
I tried a few different projects but I didn't like any of them for various reasons.

Since vibe-coding is a big deal these days, I decided to brush off my Java rust and try my hand at
a containerized project that used configuration similar to Unpackerr and did one thing and did it well.
The few portions with LLM assistance have been noted (search `ChatGPT`) and generated code was read and verified before use.

No use of opencode, claude code, cursor, etc in this project. Any LLM assistance was done via web UI.
Largely to ask questions about design, errors, or specific details I've forgotten since touching Java.
Readme also written by hand.

I think I hit that nail on the head.

Currently supports the following:
- Radarr
- Sonarr
- Whisparr

## How do I use it?

Docker, kubernetes, whatever container system you currently use.

```bash
docker run egg82/fetcharr:latest \
  -e VERIFY_CERTS=true \
  -e SSL_PATH=/etc/ssl/certs/ca-bundle.crt \
  -e SEARCH_AMOUNT=5 \
  -e SEARCH_INTERVAL=1hour \
  -e RADARR_0_URL=https://radarr.home.lab \
  -e RADARR_0_API_KEY=e8ea891d72ff973fa6db0d34369a60a7 \
  -e SONARR_0_URL=https://sonarr.home.lab \
  -e SONARR_0_API_KEY=71730b5dfaa4293fe0c050844c10df66 \
  -e SONARR_1_URL=https://anime.home.lab \
  -e SONARR_1_API_KEY=bdb84dc8e4b787c76be8aae2dfe9bd19 \
```

Or by immutable tag: https://hub.docker.com/r/egg82/fetcharr/tags

## Environment variables

### Common

- LOG_MODE: Logging mode
  - string: trace, debug, info, warn, error
  - default: info
- PROXY_HOST: HTTP proxy host
  - string: \<URL\>
  - default: null
- PROXY_PORT: HTTP proxy port
  - int: \<port\>
  - default: 0
- CONNECT_TIMEOUT: HTTP connection timeout in milliseconds
  - int: \<ms\>
  - default: 10,000
- REQUEST_TIMEOUT: HTTP request timeout in milliseconds
  - int: \<ms\>
  - default: 10,000
- CONNECT_TTL: HTTP connection TTL in milliseconds
  - int: \<ms\>
  - default: 10,000
- VERIFY_CERTS: Verify SSL certificates
  - bool: \<value\>
  - default: true
- USE_CACHE: Use internal caching mechanisms
  - bool: \<value\>
  - default: true
- SHORT_CACHE_TIME: Expiration time for short-lived cached values
  - string: \<time\>
  - default: 65minutes
- LONG_CACHE_TIME: Expiration time for long-lived cached values
  - string: \<time\>
  - default: 6hours
- DATA_DIR: Data storage directory
  - string: \<file\>
  - default: /data
- SSL_PATH: File path containing custom SSL certs
  - string: \<file\>
  - default: /etc/ssl/certs/ca-bundle.crt
- SEARCH_AMOUNT: Number of items to search at each run
  - int: \<amount\>
  - default: 5
- SEARCH_INTERVAL: How often to search
  - string: \<time\>
  - 1hour
- MONITORED_ONLY: True to select only monitored items, false to select all
  - bool: \<value\>
  - default: true
- SKIP_TAGS: Comma-separated list of tags to skip searching
  - string: \<tags\>
  - default: \<none\>

### Radarr

Replace `X` with a number. This allows for up to 100 instances to be configured.

- RADARR_X_URL: Base URL
  - string: \<url\>
  - default: null
- RADARR_X_API_KEY: API key
  - string: \<key\>
  - default: null
- RADARR_X_SEARCH_AMOUNT: Number of items to search at each run
    - int: \<amount\>
    - default: 5
- RADARR_X_SEARCH_INTERVAL: How often to search
    - string: \<time\>
    - 1hour
- RADARR_X_MONITORED_ONLY: True to select only monitored items, false to select all
    - bool: \<value\>
    - default: true
- RADARR_X_SKIP_TAGS: Comma-separated list of tags to skip searching
    - string: \<tags\>
    - default: \<none\>

### Sonarr

Replace `X` with a number. This allows for up to 100 instances to be configured.

- SONARR_X_URL: Base URL
    - string: \<url\>
    - default: null
- SONARR_X_API_KEY: API key
    - string: \<key\>
    - default: null
- SONARR_X_SEARCH_AMOUNT: Number of items to search at each run
    - int: \<amount\>
    - default: 5
- SONARR_X_SEARCH_INTERVAL: How often to search
    - string: \<time\>
    - 1hour
- SONARR_X_MONITORED_ONLY: True to select only monitored items, false to select all
    - bool: \<value\>
    - default: true
- SONARR_X_SKIP_TAGS: Comma-separated list of tags to skip searching
    - string: \<tags\>
    - default: \<none\>

### Whisparr

Replace `X` with a number. This allows for up to 100 instances to be configured.

- WHISPARR_X_URL: Base URL
  - string: \<url\>
  - default: null
- WHISPARR_X_API_KEY: API key
  - string: \<key\>
  - default: null
- WHISPARR_X_SEARCH_AMOUNT: Number of items to search at each run
  - int: \<amount\>
  - default: 5
- WHISPARR_X_SEARCH_INTERVAL: How often to search
  - string: \<time\>
  - 1hour
- WHISPARR_X_MONITORED_ONLY: True to select only monitored items, false to select all
  - bool: \<value\>
  - default: true
- WHISPARR_X_SKIP_TAGS: Comma-separated list of tags to skip searching
  - string: \<tags\>
  - default: \<none\>
