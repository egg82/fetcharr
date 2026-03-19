# Fetcharr

<img src=".doc-misc/fetcharr-temp-icon.png" width="60"/>

###### Icon is LLM-generated and temporary until one from an artist can be sourced.

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
- Lidarr
- Whisparr

### Huntarr? What?

[The Huntarr saga](https://www.reddit.com/r/selfhosted/comments/1rckopd/huntarr_your_passwords_and_your_entire_arr_stacks/) is an interesting one
if you're curious, but if you're not familiar with the history then here's the short of what Fetcharr does:

The idea is that you’ll occasionally want to go through all your media and make sure it’s the best quality available and that nothing’s missing.
New releases get published, remuxes sometimes fix issues, etc. This little CLI container goes through and periodically searches every *arr app you connect
it to, so you don’t have to sacrifice hours of your weekend doing (as much) manual hunting.

Now, it's worth mentioning that Sonarr, Radarr, etc have had a built-in system that does this for a while now, but I've never gotten
them to work reliably. Maybe it's just bad luck or some strange misconfiguration, but I've always had a need for apps like
Scoutarr (Upgradinatorr), Huntarr, etc. Considering the popularity of these apps it feels like I am not the only one.

Update to this: I learned that at *arr stack uses RSS feeds to scan for and fetch updates, so if your indexer doesn't support those
feeds or the feeds or too old (or a myriad of other issues that can come from this kind of system) then you won't get replacement content
even if it exists. This is why these kinds of "hunting", "fetching", "upgrading", etc systems work so well. They simply force the *arr
apps to periodically update their content through their configured indexer, regardless of RSS feed availability.

If the concept sounds interesting to you, give Fetcharr a try. See if it finds anything. If my experience while developing this is anything
to go by, you'll get some results almost immediately. Likely within a few hours, and maybe even within a few seconds. See if it helps
and if you want to add it to your stack.

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

| variable | type    | values | default  | description |
| -------- |---------| ------ |----------| ----------- |
| LOG_MODE | string | trace, debug, info, warn, error | info | Logging mode |
| DRY_RUN | boolean | true, false | false | Run in dry-run mode, which will list (but **not** perform) searches |
| CONFIG_DIR | directory | /any/directory/path | /app/config | Configuration storage directory |
| CACHE_DIR | directory | /any/directory/path | /app/cache | Cache storage directory |
| LOG_DIR | directory | /any/directory/path | /app/logs | Log storage directory |
| VERIFY_CERTS | boolean | true, false | true | Verify SSL certificates |
| SSL_PATH | file | /any/file/path.ext | /etc/ssl/certs/ca-bundle.crt | File path for SSL cert bundle |
| PROXY_HOST | string | \<URL\> | \<none\> | HTTP proxy host |
| PROXY_PORT| integer | 1-65534 | 80 | HTTP proxy port |
| CONNECT_TIMEOUT | integer | 0-2147483647 | 2500 | HTTP connection timeout in milliseconds |
| REQUEST_TIMEOUT | integer | 0-2147483647 | 120000 | HTTP request timeout in milliseconds |
| CONNECT_TTL | integer | 0-2147483647 | 300000 | HTTP connection TTL in milliseconds |
| USE_FILE_CACHE | tristate | auto, true, false | auto | Use file-based cache |
| USE_MEMORY_CACHE | tristate | auto, true, false | auto | Use in-memory cache |
| SHORT_CACHE_TIME | time | 5minutes, 3hours, etc | 65minutes | Expiration time for short-lived cached values |
| LONG_CACHE_TIME | time | 5minutes, 3hours, etc | 6hours | Expiration time for long-lived cached values |
| SEARCH_AMOUNT | integer | 0-2147483647 | 5 | Number of items to search at each run |
| SEARCH_INTERVAL | time | 5minutes, 3hours, etc | 1hour | How often to perform searches |
| MONITORED_ONLY | boolean | true, false | true | Select for monitored items |
| MISSING_ONLY | boolean | true, false | false | Select for missing items |
| USE_CUTOFF | boolean | true, false | false | Select for items that do not meet their profile cutoff |
| SKIP_TAGS | string | any,string,values | \<none\> | Comma-separated list of tags to skip searching |

Notes on caching:

With `USE_FILE_CACHE` set to "auto", Fetcharr will determine if the configured cache directory is writable.
If so, it will enable use of file caching. If not, it will disable file caching.

With `USE_MEMORY_CACHE` set to "auto", Fetcharr will determine if the file cache is usable.
If so, it will disable use of in-memory caching. If not, it will enable in-memory caching.

This means that, if file caching is available, it won't store cached objects in memory. If file caching is
not available, cached objects will be stored in memory. In-memory caching can consume a fair amount of memory,
so file caching is preferred when possible.

It's also possible to disable both so no caching is performed at all. Obviously, if you're using purely in-memory
caching and you restart the container your cache will be lost.

Since caching is just that - caching - then clearing or disabling the cache won't really affect the program
except to cause more API calls and a slightly slower run. Depending on your setup either tradeoff can be
perfectly acceptable. This is why there's configuration options, eh?

### Radarr overrides

Replace `X` with a number from 0 to 99. This allows for up to 100 instances to be configured.

| variable | type    | values | default  | description |
| -------- |---------| ------ |----------| ----------- |
| RADARR_X_URL | string | \<URL\> | \<none\> | Base URL |
| RADARR_X_API_KEY | string | \<key\> | \<none\> | API key |
| RADARR_X_SEARCH_AMOUNT | integer | 0-2147483647 | 5 | Number of items to search at each run |
| RADARR_X_SEARCH_INTERVAL | time | 5minutes, 3hours, etc | 1hour | How often to perform searches |
| RADARR_X_MONITORED_ONLY | boolean | true, false | true | Select for monitored items |
| RADARR_X_MISSING_ONLY | boolean | true, false | false | Select for missing items |
| RADARR_X_USE_CUTOFF | boolean | true, false | false | Select for items that do not meet their profile cutoff |
| RADARR_X_SKIP_TAGS | string | any,string,values | \<none\> | Comma-separated list of tags to skip searching |

### Sonarr overrides

Replace `X` with a number from 0 to 99. This allows for up to 100 instances to be configured.

| variable | type    | values | default  | description |
| -------- |---------| ------ |----------| ----------- |
| SONARR_X_URL | string | \<URL\> | \<none\> | Base URL |
| SONARR_X_API_KEY | string | \<key\> | \<none\> | API key |
| SONARR_X_SEARCH_AMOUNT | integer | 0-2147483647 | 5 | Number of items to search at each run |
| SONARR_X_SEARCH_INTERVAL | time | 5minutes, 3hours, etc | 1hour | How often to perform searches |
| SONARR_X_MONITORED_ONLY | boolean | true, false | true | Select for monitored items |
| SONARR_X_MISSING_ONLY | boolean | true, false | false | Select for missing items |
| SONARR_X_USE_CUTOFF | boolean | true, false | false | Select for items that do not meet their profile cutoff |
| SONARR_X_SKIP_TAGS | string | any,string,values | \<none\> | Comma-separated list of tags to skip searching |

### Lidarr overrides

Replace `X` with a number from 0 to 99. This allows for up to 100 instances to be configured.

| variable | type    | values | default  | description |
| -------- |---------| ------ |----------| ----------- |
| LIDARR_X_URL | string | \<URL\> | \<none\> | Base URL |
| LIDARR_X_API_KEY | string | \<key\> | \<none\> | API key |
| LIDARR_X_SEARCH_AMOUNT | integer | 0-2147483647 | 5 | Number of items to search at each run |
| LIDARR_X_SEARCH_INTERVAL | time | 5minutes, 3hours, etc | 1hour | How often to perform searches |
| LIDARR_X_MONITORED_ONLY | boolean | true, false | true | Select for monitored items |
| LIDARR_X_MISSING_ONLY | boolean | true, false | false | Select for missing items |
| LIDARR_X_USE_CUTOFF | boolean | true, false | false | Select for items that do not meet their profile cutoff |
| LIDARR_X_SKIP_TAGS | string | any,string,values | \<none\> | Comma-separated list of tags to skip searching |

### Whisparr overrides

Replace `X` with a number from 0 to 99. This allows for up to 100 instances to be configured.

| variable | type    | values | default  | description |
| -------- |---------| ------ |----------| ----------- |
| WHISPARR_X_URL | string | \<URL\> | \<none\> | Base URL |
| WHISPARR_X_API_KEY | string | \<key\> | \<none\> | API key |
| WHISPARR_X_SEARCH_AMOUNT | integer | 0-2147483647 | 5 | Number of items to search at each run |
| WHISPARR_X_SEARCH_INTERVAL | time | 5minutes, 3hours, etc | 1hour | How often to perform searches |
| WHISPARR_X_MONITORED_ONLY | boolean | true, false | true | Select for monitored items |
| WHISPARR_X_MISSING_ONLY | boolean | true, false | false | Select for missing items |
| WHISPARR_X_USE_CUTOFF | boolean | true, false | false | Select for items that do not meet their profile cutoff |
| WHISPARR_X_SKIP_TAGS | string | any,string,values | \<none\> | Comma-separated list of tags to skip searching |

### Wall of oddities

Note that this is *not* a wall of shame. Just some odd comments, funny moments, and other strange
things that I have seen around this project.

<img src=".doc-misc/Screenshot_20260318-132140.png" width="400"/>

It definitely feels like I won a contest that I wasn't aware I was in. Passed all 0 gates, too!

<img src=".doc-misc/screenshot-titel.png" width="650"/>

I'm terrible at naming these things, so I'm grateful to have someone who came up with the name "fetcharr".
A few days after it released. Glad *someone* is on the ball, at least!
