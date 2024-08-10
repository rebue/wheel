-- 时间滑动窗口限流器
local size_key      = KEYS[1]        -- 时间窗口大小（秒）的 key
local limit_key     = KEYS[2]        -- 时间窗口内允许的最大请求数的 key
local window_key    = KEYS[3]        -- 时间窗口的 key
local expires_key   = KEYS[4]        -- 到期秒数的 key
local blacklist_key = KEYS[5]        -- 黑名单的 key

local expires = tonumber(redis.call("GET", expires_key))
if expires == nil then
    expires = 3600                  -- 到期时间默认30分钟
    redis.call("SETEX", expires_key, expires, expires)
else
    redis.call("EXPIRE", expires_key, expires)
end

local size = tonumber(redis.call("GET", size_key))
if size == nil then
    size = 60                       -- 时间窗口大小默认1分钟
    redis.call("SETEX", size_key, expires, size)
else
    redis.call("EXPIRE", size_key, expires)
end

local limit = tonumber(redis.call("GET", limit_key))
if limit == nil then
    limit = 2000                    -- 默认单位时间内最多处理2000条请求
    redis.call("SETEX", limit_key, expires, limit)
else
    redis.call("EXPIRE", limit_key, expires)
end

-- 获取当前时间（秒）
local now = redis.call("TIME")
local current_time = tonumber(now[1])

-- 删除超出时间的时间戳
redis.call("ZREMRANGEBYSCORE", window_key, 0, current_time - size)

-- 获取当前时间窗口内的请求数量
local request_count = redis.call("ZCARD", window_key)

if request_count < limit then
    -- 将当前请求的时间戳添加到有序集合中
    redis.call("ZADD", window_key, current_time, current_time)
    redis.call("EXPIRE", window_key, expires)
    return 1 -- 允许请求
else
    redis.call("EXPIRE", window_key, expires)
    return 0 -- 拒绝请求
end