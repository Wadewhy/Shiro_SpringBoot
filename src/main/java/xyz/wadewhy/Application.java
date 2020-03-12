/**   
 * Copyright © 2020 eSunny Info. Tech Ltd. All rights reserved.
 * 
 * @Package: xyz.wadewhy 
 * @author: 钟子豪   网站wadewhy.xyz
 * @date: 2020年3月12日 下午4:00:32 
 */
package xyz.wadewhy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
/**
* @author 钟子豪
* 作者 E-mail:wadewhy@yeah.net
* @version 创建时间：2020年3月12日 下午4:00:32
* 类说明
*/
/** 
* @ClassName: Application 
* @Description: TODO
* @author: wadewhy
* @date: 2020年3月12日 下午4:00:32  
*/

@SpringBootApplication
@MapperScan(basePackages = {"xyz.wadewhy.mapper"})
public class Application {
    // 在main方法中启动一个应用，即：这个应用的入口
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SpringApplication.run(Application.class, args);
    }
}