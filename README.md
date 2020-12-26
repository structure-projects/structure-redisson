# structure-redission
一个对redission的封装,redission要比redis功能更强大
## redisson 的使用 ##
### POM依赖###
```xml
        <dependency>
            <groupId>cn.structured</groupId>
            <artifactId>structure-redission-starter</artifactId>
            <version>${last.version}</version>
        </dependency>
```
### redisson的使用 ###
#### 使用redissonClient ####
```java
     @Resource
     private RedissonClient redissonClient;
```
#### 使用RedissonClient 存储对象 ####
```java
    RBucket<String> test = redissonClient.getBucket("test");
    test.set("test redissonClient");
    System.out.println("test = " + test.get());
```
#### 使用RedissonClient Map List 对象结构请到redisson官网查看 ####
[redisson Github地址](https://github.com/redisson/redisson)
### redisson锁的使用 ###
```java
    @RequestMapping("/testLock")
    @Lock(keys = "#key")
    public void testLock(@RequestParam("key") String key) throws InterruptedException {
        System.out.println("key = " + key);
        Thread.sleep(10000L);
    }

    @RequestMapping("/testLock2")
    @Lock(keys = "#testVO.id")
    public void testLockObject(@RequestBody TestVO testVO ) throws InterruptedException {
        System.out.println("key = " + testVO.getId());
        Thread.sleep(10000L);
    }
```
### 缓存结构说明 ###
#### 1.  @WCache 写缓存注解
写缓存注解是对要要执行的方法返回的结果写入缓存中
```java
	@WCache(key = "#giftId",isObjCache = false,
			map = @CMap(isMap = true ,mapKey = "ACCOUNT-GIFT:_#accountId"),
			time = @CTime(isTime = true,time = 4,timeType = TimeUnit.HOURS))
```
@WCache 支持map缓存策略 list缓存策略，时间缓存策略，是否为对象缓存
#### 2.  @RCache 读缓存注解 
读对象缓存注解通过代理实现无则写入，有则读取如果读取成功不会执行要代理的方法只有在写入缓存是会指定要代理的方法取得返回结果写入缓存中

```java
    @RCache(key = "GIFT-TYPE:_#giftType",time = 1)
```
#### 3. @RListCache集合缓存注解 
集合缓存注解是对redisList存储结构封装list缓存注解 可以搭配 map结构和对象结构混合使用
#### 4. @RCacheMa pMap缓存注解 
Map缓存注解 是对redis-Map存储结构封装map缓存注解 可以搭配 list结构和对象结构混合使用
```java
    @RCacheMap(mapKey = "ACCOUNT-GIFT:_#accountId",key = "#giftId",isTime = true,time = 4,timeType = TimeUnit.HOURS)
```
读取存储在map中的单条数据
#### 5.  @RMapAllCache 读全部map缓存注解
```java
    @RMapAllCache(mapKey = "ACCOUNT-GIFT:_#accountId",keyName = "giftId",time = @CTime(isTime = true,time = 4,timeType = TimeUnit.HOURS))
```
读取map中全部的数据
### redisson缓存的使用 ###
```java
    /**
     * 写入缓存,同时构建 object 对象和 集合对象,以及map对象
     * @param testVO
     * @return
     */
    @RequestMapping(value = "cache")
    @WCache(key = "#testVO.id",isObjCache = true
            ,list = @CList(listKeyName = "test-list",isList = true,size = 100,time = @CTime(isTime = true,time = 10))
            ,map = @CMap(mapKey = "test-map",isMap = true,time = @CTime(isTime = true,time = 100))
    )
    public TestVO cache(TestVO testVO){
        System.out.println("testVO = " + testVO);
        return testVO;
    }

    /**
     * 读缓存 如果没有读到则更新缓存 object
     * @param id
     * @return
     */
    @RCache(key = "#id")
    @RequestMapping("/getCache")
    public TestVO getCache(@RequestParam("id") String id){
        TestVO testVO = new TestVO();
        testVO.setId(id);
        testVO.setName("没有读取到");
        System.out.println("需要写入缓存,如果缓存中有值则不会写入缓存");
        return testVO;
    }

    /**
     * map和list配合使用
     * @param testVO
     * @return
     */
    @RequestMapping(value = "cacheMapList")
    @WCache(key = "#testVO.id",isObjCache = false
            ,list = @CList(listKeyName = "cache-list",isList = true,size = 100
            ,time = @CTime(isTime = true,time = 10),mapKey = "cache-map",value = CList.ListType.MAP)
            ,map = @CMap(mapKey = "cache-map",isMap = true,time = @CTime(isTime = true,time = 100))
    )
    public TestVO cacheMapList(TestVO testVO){
        System.out.println("testVO = " + testVO);
        return testVO;
    }

    /**
     * list配合object使用
     * @param testVO
     * @return
     */
    @RequestMapping(value = "cacheList")
    @WCache(key = "#testVO.id",isObjCache = true
            ,list = @CList(listKeyName = "cache-list-key",isList = true,size = 100
            ,time = @CTime(isTime = true,time = 10),value = CList.ListType.KEY))
    public TestVO cacheList(TestVO testVO){
        System.out.println("testVO = " + testVO);
        return testVO;
    }

    /**
     * list和map关联的数据结构
     * 该结构需要手动补偿缓存值
     * @return
     */
    @RListCache(key = "cache-list",mapKey = "cache-map",value = CList.ListType.MAP)
    public List<TestVO> getMapList(){
        return new ArrayList<>();
    }

```

