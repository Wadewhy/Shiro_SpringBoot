/**   
 * Copyright © 2020 eSunny Info. Tech Ltd. All rights reserved.
 * 
 * @Package: xyz.wadewhy.common 
 * @author: 钟子豪   网站wadewhy.xyz
 * @date: 2020年3月12日 下午4:19:44 
 */
package xyz.wadewhy.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.wadewhy.domain.User;

/**
* @author 钟子豪
* 作者 E-mail:wadewhy@yeah.net
* @version 创建时间：2020年3月12日 下午4:19:44
* 类说明
*/
/** 
* @ClassName: ActiverUser 
* @Description: TODO
* @author: wadewhy
* @date: 2020年3月12日 下午4:19:44  
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private User user;
    private List<String> roles;
    private List<String> permissions;

}
