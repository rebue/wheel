-- 时间滑动窗口限流器
-- 配置参数size、limit、expires: 使用 Hash 存储
-- 如果配置参数不存在，则用默认值创建配置并设置有效时间
-- 如果配置参数存在而未设置有效时间，更新时不要设置有效时间(这样避免手动设置永不过期的配置参数会因有效时间而丢失)
-- 请求记录: 使用 Sorted Set (ZADD/ZCARD/ZREMRANGEBYSCORE)
-- 请求记录的 score 使用秒为单位，方便统一清除

local config_key = KEYS[1]                              -- 配置 hash 的 key
local window_key = KEYS[2]                              -- 时间窗口的 key

local size                                              -- 时间窗口大小(秒)
local limit                                             -- 时间窗口内允许的最大请求数
local expires                                           -- 有效期(秒)

local config  = redis.call("HGETALL", config_key)

-- 如果配置不存在，则用默认值创建配置
if next(config) == nil then
    -- 设置默认值
    size    = 60                                        -- 时间窗口大小默认1分钟
    limit   = 2000                                      -- 时间窗口内允许的最大请求数默认2000
    expires = 1800                                      -- 到期秒数默认30分钟
    redis.call("HSET", config_key, "size", size, "limit", limit, "expires", expires)
    redis.call("EXPIRE", config_key, expires)           -- 设置 key 的有效期(秒)
else
    -- 如果配置存在，则获取配置
    size    = tonumber(config["size"])
    limit   = tonumber(config["limit"])
    expires = tonumber(config["expires"])

    -- 如果配置设置了过期时间，则更新过期时间
    local ttl = redis.call("TTL", config_key)
    if ttl > 0 then
        redis.call("EXPIRE", config_key, expires)
    end
end

-- 获取当前时间
local now = redis.call("TIME")
-- 当前时间(秒)
local current_time_seconds      = tonumber(now[1])
-- 当前时间(微秒)
local current_time_microseconds = current_time_seconds * 1000000 + tonumber(now[2])

-- 删除超出时间的时间戳(删除的范围是: 0~(当前时间-时间窗口大小) )
redis.call("ZREMRANGEBYSCORE", window_key, 0, current_time_seconds - size)

-- 获取当前时间窗口内的请求数量
local request_count = redis.call("ZCARD", window_key)

-- 如果请求数量小于限制，则允许请求
local allow_request
if request_count < limit then
    allow_request = 1                                               -- 允许请求
    -- 将当前请求的时间戳添加到有序集合中(current_time_seconds 是 score，current_time_microseconds 是 member，member 要求唯一)
    redis.call("ZADD", window_key, current_time_seconds, current_time_microseconds)
-- 否则拒绝请求
else
    allow_request = 0                                               -- 拒绝请求
end

-- 设置或延长window集合的有效期
redis.call("EXPIRE", window_key, expires)

return allow_request