package henu.xmh.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import henu.xmh.util.ApplicationContextUtil;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RedisCache implements Cache {
    private String id;

    public RedisCache(String id) {
        this.id = id;
    }

    //配置redis相关
    private RedisTemplate getRedisTemplate() {
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtil.getBean("redisTemplate");
        System.out.println("redisCache:" + redisTemplate);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<Object> keySerializer = new Jackson2JsonRedisSerializer<>(Object.class);//json序列化方式对象
        ObjectMapper objectMapper = new ObjectMapper();//配置json序列化的方式
        //设置json反序列化成功：序列化成功后将对象的类型存在json对象中以便反序列化
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        keySerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(keySerializer);
        //设置hash的相关序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(keySerializer);
        return redisTemplate;
    }

    //id为全类名
    @Override
    public String getId() {
        return this.id;
    }

    //添加缓存
    @Override
    public void putObject(Object key, Object value) {
        getRedisTemplate().opsForHash().put(id.toString(), key.toString(), value);
    }

    //读取缓存
    @Override
    public Object getObject(Object key) {
        return getRedisTemplate().opsForHash().get(id.toString(), key.toString());
    }

    //不会用到的方法
    @Override
    public Object removeObject(Object key) {
        getRedisTemplate().opsForHash().delete(id.toString(), key.toString());
        return null;
    }

    // 清空缓存
    @Override
    public void clear() {
        getRedisTemplate().delete(id.toString());
    }

    //获取缓存的长度，命中率的计算表
    @Override
    public int getSize() {
        return getRedisTemplate().opsForHash().size(id.toString()).intValue();
    }

    //读写锁   ReadWriteLock   写写互斥  读写不互斥 读读不互斥  synchronized  读 读 互斥  读写 互斥
    @Override
    public ReadWriteLock getReadWriteLock() {
        return new ReentrantReadWriteLock();
    }
}
