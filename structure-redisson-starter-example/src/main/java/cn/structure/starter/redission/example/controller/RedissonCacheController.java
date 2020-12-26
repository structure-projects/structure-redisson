package cn.structure.starter.redission.example.controller;

import cn.structure.starter.redisson.anno.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *      缓存测试
 * </p>
 *
 * @author chuck
 * @version V1.0.0
 * @since 2020/12/26 15:11
 */
@RestController
public class RedissonCacheController {

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

}
