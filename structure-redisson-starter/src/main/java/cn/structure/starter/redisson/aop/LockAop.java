package cn.structure.starter.redisson.aop;

import cn.structure.starter.redisson.anno.Lock;
import cn.structure.starter.redisson.enumerate.LockModelEnum;
import cn.structure.starter.redisson.exception.LockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import java.util.concurrent.TimeUnit;

import static cn.structure.starter.redisson.utils.StringUtil.getValueBySpelKey;

/**
 * <p>
 *     分布式锁aop
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Aspect
@Slf4j
public class LockAop {

    private Long attemptTimeout = 10000L;

    private Long lockWatchdogTimeout = 30000L;

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(lock)")
    public void controllerAspect(Lock lock) {
    }

    @Around("controllerAspect(lock)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint, Lock lock) throws Throwable {
        String[] keys = lock.keys();
        if (keys.length == 0) {
            throw new RuntimeException("keys不能为空");
        }
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        Object[] args = proceedingJoinPoint.getArgs();

        long attemptTimeout = lock.attemptTimeout();

        long lockWatchdogTimeout = lock.lockWatchdogTimeout();

        if (attemptTimeout == 0) {
            attemptTimeout = this.attemptTimeout;
        }
        if (lockWatchdogTimeout == 0) {
            lockWatchdogTimeout = this.lockWatchdogTimeout;
        }

        LockModelEnum lockModel = lock.lockModel();


        if (lockModel.equals(LockModelEnum.AUTO)) {
            if (keys.length > 1) {
                lockModel = LockModelEnum.MULTIPLE;
            } else {
                lockModel = LockModelEnum.REENTRANT;
            }
        }
        if (!lockModel.equals(LockModelEnum.MULTIPLE) && !lockModel.equals(LockModelEnum.REDLOCK) && keys.length > 1) {
            throw new RuntimeException("LockAop -> 参数有多个,锁模式为-> " + lockModel.name() + ".无法锁定");
        }
        log.info("LockAop ->  :锁模式->{},等待锁定时间->{}秒.锁定最长时间->{}秒", lockModel.name(), attemptTimeout / 1000, lockWatchdogTimeout / 1000);
        boolean res = false;
        RLock rLock = null;
        //一直等待加锁.
        switch (lockModel) {
            case FAIR:
                rLock = redissonClient.getFairLock(getValueBySpelKey(keys[0], parameterNames, args));
                break;
            case REDLOCK:
                RLock[] locks = new RLock[keys.length];
                int index = 0;
                for (String key : keys) {
                    locks[index++] = redissonClient.getLock(getValueBySpelKey(key, parameterNames, args));
                }
                rLock = new RedissonRedLock(locks);
                break;
            case MULTIPLE:
                RLock[] locks1 = new RLock[keys.length];
                int index1 = 0;
                for (String key : keys) {
                    locks1[index1++] = redissonClient.getLock(getValueBySpelKey(key, parameterNames, args));
                }
                rLock = new RedissonMultiLock(locks1);
                break;
            case REENTRANT:
                rLock = redissonClient.getLock(getValueBySpelKey(keys[0], parameterNames, args));
                break;
            case READ:
                RReadWriteLock rwlock = redissonClient.getReadWriteLock(getValueBySpelKey(keys[0], parameterNames, args));
                rLock = rwlock.readLock();
                break;
            case WRITE:
                RReadWriteLock rwlock1 = redissonClient.getReadWriteLock(getValueBySpelKey(keys[0], parameterNames, args));
                rLock = rwlock1.writeLock();
                break;
            default:
                break;
        }
        //执行aop
        if (rLock != null) {
            try {
                if (attemptTimeout == -1) {
                    res = true;
                    //一直等待加锁
                    rLock.lock(lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                } else {
                    res = rLock.tryLock(attemptTimeout, lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                }
                if (res) {
                    Object obj = proceedingJoinPoint.proceed();
                    return obj;
                } else {
                    throw new LockException("获取锁失败");
                }


            } finally {
                if (res) {
                    rLock.unlock();
                }
            }
        }
        throw new LockException("获取锁失败");
    }
}
