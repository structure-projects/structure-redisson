package cn.structure.starter.redisson.exception;

/**
 * <p>
 *     分布式锁异常
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
public class LockException extends RuntimeException{

    public LockException() {
    }
    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
