package cn.structure.starter.redisson.aop;

import cn.structure.starter.redisson.anno.*;
import cn.structure.starter.redisson.properties.CacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.*;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

import static cn.structure.starter.redisson.utils.StringUtil.getValueBySpelKey;

/**
 * <p>
 *     Redis缓存Aop实现
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Slf4j
@Aspect
public class RedisCacheAop {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CacheProperties cacheProperties;

    /**
     * @Title  writeCache
     * @Description 写入缓存
     * @author chuck
     * @updateAuthor chuck
     * @Date 2020/4/7 14:26
     * @param proceedingJoinPoint
     * @param wCache
     * @version v1.0.0
     * @exception
     * @throws
     * @return java.lang.Object
     **/
    @Around("@annotation(wCache)")
    public Object writeCache(ProceedingJoinPoint proceedingJoinPoint, WCache wCache) throws Throwable {
        //获取参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        //获取参数值
        Object[] args = proceedingJoinPoint.getArgs();
        //方法名
        String methodName = proceedingJoinPoint.getSignature().getName();
        //className
        String className = proceedingJoinPoint.getTarget().getClass().toString();
        //1.0 获取Redis中的key
        String key = getKey(getValueBySpelKey(wCache.key(),parameterNames,args));
        //判断key是否无效
        if (key == null ||  key.isEmpty() || key.length() <= 0 ) {
            log.error("写入缓存->{},->{}, 存在无效的KEY",className,methodName);
            return null;
        }
        log.info("写入缓存->{},->{}, key = {}",className,methodName,key);
        //从方法中拿到返回结果
        Object proceed = proceedingJoinPoint.proceed();
        if (wCache.isObjCache()) {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            //判断是否有时效
            if (null != wCache.time() && wCache.time().isTime()) {
                bucket.set(proceed,wCache.time().time(),wCache.time().timeType());
            }else {
                //放入redis中
                bucket.set(proceed);
            }
        }
        //判断是是否有list类表更新list列表
        if (null != wCache.list() && wCache.list().isList()) {
            //集合的key
            String listKeyName = getValueBySpelKey(getKey(wCache.list().listKeyName()), parameterNames, args);
            log.info("写入缓存集合的KEY = {}, type = {}",listKeyName,wCache.list().value());
            Object data = proceed;
            //更新集合key
            if (wCache.list().value() == CList.ListType.KEY) {
                data = key;
            }else if (wCache.list().value() == CList.ListType.MAP) {
                data =  getValueBySpelKey(wCache.map().mapKey(), parameterNames, args);
            }
            updateCacheList(listKeyName,data,wCache.list().size(),wCache.list().time());
        }
        //判断存储map
        if (null != wCache.map() && wCache.map().isMap()) {
            //mapKey
            String mapKey = getKey(getValueBySpelKey(wCache.map().mapKey(), parameterNames, args));
            String subMapKey = getValueBySpelKey(wCache.key(), parameterNames, args);
            //更新map緩存
            updateMapCache(mapKey,subMapKey,proceed,wCache.map().time());
        }
        log.info("wCache - > END");
        return proceed;
    }
    /**
     * @Title  updateMapCache
     * @Description 更新map缓存
     * @author chuck
     * @updateAuthor chuck
     * @Date 2020/4/10 11:20
     * @param mapKey
     * @param key
     * @param data
     * @param time
     * @version v1.0.0
     * @exception
     * @throws
     * @return void
     **/
    private void updateMapCache(String mapKey, String key,Object data, CTime time){
        log.info("updateMapCache - > mapKey = {},key = {},data = {}",mapKey,key,data);
        RMap<String, Object> map = redissonClient.getMap(mapKey);
        if (time.isTime()) {
            map.expire(time.time(),time.timeType());
        }
        map.put(key,data);
    }

    /**
     * @Title  updateCacheList
     * @Description 更新缓存列表
     * @author chuck
     * @updateAuthor chuck
     * @Date 2020/4/7 15:23 
     * @param listKey
     * @param data
     * @param size
     * @param time
     * @version v1.0.0
     * @exception 
     * @throws   
     * @return void
     **/
    private void updateCacheList(String listKey, Object data, int size, CTime time) {
        log.info("updateCacheList - > listKey = {},data = {}",listKey,data);
        //从redis中获取
        RList<Object> keyList = redissonClient.getList(listKey);
        if (time.isTime()) {
            keyList.expire(time.time(),time.timeType());
        }
        if (!keyList.contains(data)) {
            keyList.add(data);
            //判断溢出出队
            if (keyList.size() >= size) {
                keyList.remove(0);
            }
        }
    }

    /**
     * @Title  readListCache
     * @Description 读列表缓存
     * @author chuck
     * @updateAuthor chuck
     * @Date 2020/4/7 14:27
     * @param proceedingJoinPoint
     * @param rListCache
     * @version v1.0.0
     * @exception
     * @throws
     * @return java.lang.Object
     **/
    @Around("@annotation(rListCache)")
    public Object readListCache(ProceedingJoinPoint proceedingJoinPoint, RListCache rListCache) throws Throwable {
        //获取参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        //获取参数值
        Object[] args = proceedingJoinPoint.getArgs();
        //key
        String key = getKey(getValueBySpelKey(rListCache.key(),parameterNames,args));
        //page
        int page = Integer.parseInt(getValueBySpelKey(rListCache.page(),parameterNames,args));
        //size
        int size = Integer.parseInt(getValueBySpelKey(rListCache.size(),parameterNames,args));
        log.info("readListCache -> key = {},page = {},size = {}",key,page,size);
        //获取redis
        if (rListCache.value() == CList.ListType.KEY) {
            RList<String> list = redissonClient.getList(key);
            //获取队列
            if (list != null && list.size() >= (page * size)) {
                int start = page * size - size;
                List<String> keys = list.readSortAlpha(rListCache.sort(), start, size);
                RBuckets buckets = redissonClient.getBuckets();
                String [] bKeys = new String [keys.size()];
                for (int i = 0 ;i < bKeys.length ; i++) {
                    bKeys[i] = keys.get(i);
                }
                Map<String, Object> objectMap = buckets.get(bKeys);
                List<Object> objectList = new ArrayList<>();
                for (String mapKey:objectMap.keySet()) {
                    objectList.add(objectMap.get(mapKey));
                }
                log.info("readListCache -> KEY END");
                return objectList;
            }
        }else if (rListCache.value() == CList.ListType.DATA) {
            RList<Object> list = redissonClient.getList(key);
            if (null != list && list.size() > (page * size)) {
                int start = page * size - size;
                List<Object> objectList = list.readSort(rListCache.sort(), start, size);
                log.info("readListCache -> DATA END");
                return objectList;
            }
        }else if (rListCache.value() == CList.ListType.MAP) {
            RList<String> list = redissonClient.getList(key);
            if (null != list && list.size() > (page * size)) {
                int start = page * size - size;
                List<String> keys = list.readSortAlpha(rListCache.sort(), start, size);
                String mapKey = getValueBySpelKey(rListCache.mapKey(),parameterNames,args);
                RMap<Object, Object> map = redissonClient.getMap(mapKey);
                List<Object> objectList = new ArrayList<>();
                for (String objKey :keys) {
                    objectList.add(map.get(objKey));
                }
                log.info("readListCache -> MAP END");
                return objectList;
            }
        }
        //队列的补偿操作请写在原方法上
        Object proceed = proceedingJoinPoint.proceed();
        log.info("readListCache -> db END");
        return proceed;
    }

    @Around("@annotation(rCache)")
    public Object readCache(ProceedingJoinPoint proceedingJoinPoint, RCache rCache) throws Throwable {
        //获取参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        //获取参数值
        Object[] args = proceedingJoinPoint.getArgs();
        //key
        String key = getKey(getValueBySpelKey(rCache.key(),parameterNames,args));
        log.info("readCache -> key ={}",key);
        RBucket<Object> bucket = redissonClient.getBucket(key);
        //从redis获取对象
        Object obj = bucket.get();
        //判断是否为空
        if (null == obj) {
            //如果空从方法中获取对象
            obj = proceedingJoinPoint.proceed();
            log.info("readCache - > db");
            //并且存储到redis中 默认时长为 1 天
            bucket.set(obj,rCache.time(), rCache.timeType());
        }
        log.info("readCache - > END");
        return obj;
    }

    @Around("@annotation(rMapCache)")
    public Object readMapCache(ProceedingJoinPoint proceedingJoinPoint, RCacheMap rMapCache) throws Throwable {
        //获取参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        //获取参数值
        Object[] args = proceedingJoinPoint.getArgs();
        //mapKey
        String mapKey = getKey(getValueBySpelKey(rMapCache.mapKey(),parameterNames,args));
        //map
        String key = getValueBySpelKey(rMapCache.key(),parameterNames,args);
        log.info("readMapCache -> mapKey ={}, key = {}",mapKey,key);
        RMap<Object, Object> map = redissonClient.getMap(mapKey);
        Object object = map.get(key);
        if (null == object) {
            if (rMapCache.isTime()) {
                map.expire(rMapCache.time(),rMapCache.timeType());
            }
            Object proceed = proceedingJoinPoint.proceed();
            map.put(key,proceed);
            if (rMapCache.list().isList()) {
                //获取listKey
                String listKey = getKey(getValueBySpelKey(rMapCache.list().listKeyName(),parameterNames,args));
                //更新list
                updateCacheList(listKey,key,rMapCache.list().size(),rMapCache.list().time());
            }
            log.info("readMapCache -> db - > END");
            return proceed;
        }
        log.info("readMapCache -> redis -> END");
        return object;
    }

    @Around("@annotation(rMapAllCache)")
    public Object readMapAllCache(ProceedingJoinPoint proceedingJoinPoint, RMapAllCache rMapAllCache) throws Throwable {
        //获取参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        //获取参数值
        Object[] args = proceedingJoinPoint.getArgs();
        //mapKey
        String mapKey = getKey(getValueBySpelKey(rMapAllCache.mapKey(),parameterNames,args));
        log.info("readMapAllCache -> mapKey ={}, key = {}",mapKey);
        RMap<String, Object> map = redissonClient.getMap(mapKey);
        if (map.size() <= 0) {
            log.info("readMapAllCache - > db");
            if (rMapAllCache.time().isTime()) {
                map.expire(rMapAllCache.time().time(),rMapAllCache.time().timeType());
            }
            Object proceed = proceedingJoinPoint.proceed();
            Map<String,Object> addMap = new HashMap<>();
            if (proceed instanceof List) {
                List list = (List) proceed;
                for (Object obj : list) {
                    Field declaredField = obj.getClass().getDeclaredField(rMapAllCache.keyName());
                    declaredField.setAccessible(true);
                    Object key = declaredField.get(obj);
                    addMap.put(key.toString(),obj);
                }
            }
            map.putAll(addMap);
            return proceed;
        }
        List<Object> mapList = new ArrayList<>();
        //不是读部分的时候执行
        if (!rMapAllCache.keys().equals("")){
            Set<String> keys = new HashSet<>();
            for (int i = 0; i < parameterNames.length; i++) {
                if (parameterNames[i].equals(rMapAllCache.keys())){
                    keys.addAll((Collection)args[i]);
                }
            }
            Map<String, Object> mapAll = map.getAll(keys);
            for (String key:mapAll.keySet()) {
                mapList.add(mapAll.get(key));
            }
        }else {
            for (String key: map.keySet()) {
                mapList.add(map.get(key));
            }
        }
        log.info("readMapAllCache - > redis");
        return mapList;
    }

    /**
     * @Title  getKey
     * @Description 获取队列的KEY
     * @author chuck
     * @updateAuthor chuck
     * @Date 2020/4/7 15:47
     * @param key
     * @version v1.0.0
     * @exception
     * @throws
     * @return java.lang.String
     **/
    public String getKey(String key){
        StringBuffer stringBuffer = new StringBuffer();
        String groupName = cacheProperties.getKeyGroupName();
        if (groupName != null && groupName.length() > 0 ) {
            stringBuffer.append(groupName);
            stringBuffer.append(":");
        }
        stringBuffer.append(key);
        return stringBuffer.toString();
    }
}
