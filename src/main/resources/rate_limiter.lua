-- KEYS[1] -> rate_limit:{clientId}
-- ARGV[1] -> capacity
-- ARGV[2] -> refillRatePerMillis
-- ARGV[3] -> currentTimeMillis

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local data = redis.call("HMGET", key, "tokens", "lastRefillTime")
local tokens = tonumber(data[1])
local lastRefill = tonumber(data[2])

if tokens == nil then
    tokens = capacity - 1
    lastRefill = now
else
    local elapsed = now - lastRefill
    local refill = math.floor(elapsed * refillRate)

    tokens = math.min(capacity, tokens + refill)

    if tokens < 1 then
        return 0
    end

    tokens = tokens - 1
    lastRefill = now
end

redis.call("HMSET", key,
    "tokens", tokens,
    "lastRefillTime", lastRefill
)

redis.call("EXPIRE", key, 120)

return 1
