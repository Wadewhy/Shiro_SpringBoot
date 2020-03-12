/**   
 * Copyright © 2020 eSunny Info. Tech Ltd. All rights reserved.
 * 
 * @Package: xyz.wadewhy.realm 
 * @author: 钟子豪   网站wadewhy.xyz
 * @date: 2020年3月12日 下午4:18:01 
 */
package xyz.wadewhy.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import xyz.wadewhy.common.UserVo;
import xyz.wadewhy.domain.User;
import xyz.wadewhy.service.PermissionService;
import xyz.wadewhy.service.RoleService;
import xyz.wadewhy.service.UserService;

/**
* @author 钟子豪
* 作者 E-mail:wadewhy@yeah.net
* @version 创建时间：2020年3月12日 下午4:18:01
* 类说明
*/
/** 
* @ClassName: UserRealm 
* @Description: TODO
* @author: wadewhy
* @date: 2020年3月12日 下午4:18:01  
*/
public class UserRealm extends AuthorizingRealm {
    @Autowired
    @Lazy // 只有使用时才加载
    private UserService userService;

    @Autowired
    @Lazy
    private PermissionService permissionService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        // 得到用户名
        String username = token.getPrincipal().toString();
        // 根据用户名查询用户
        User user = userService.queryUserByUserName(username);
        if (null != user) {
            // 存在该用户
            // 查询权限
            List<String> permissions = permissionService.queryPermissionByUserId(user.getUserid());
            // 查询角色
            List<String> roles = roleService.queryRolesByUserId(user.getUserid());

            // 构造UserVo,保存用户对象，角色，权限
            UserVo userVo = new UserVo(user, roles, permissions);
            // 创建加密盐【名字+地址组合】
            ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername() + user.getAddress());
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userVo, user.getUserpwd(),
                    credentialsSalt, this.getName());
            /**
             * 注意：在return前，会调用到doCredentialsMatch方法，而这个方法就是我们在shiro配置文件中的凭证匹配配置里的方法
             *
             <bean id="credentialsMatcher" class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
             <property name="hashAlgorithmName" value="md5"></property>
             <property name="hashIterations" value="2"></property>
             </bean>
             *  这个方法将token里的密码【用户界面登录的密码，未加密】通过配置的MD5加密算法加密后
             *  ，和authenticationInfo【数据库里加密的密码】相比较。
             doCredentialsMatch(token,authenticationInfo);
             */
            return authenticationInfo;
        } else {
            throw new UnknownAccountException();
        }

    }

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserVo uservo = (UserVo) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> roles = uservo.getRoles();
        List<String> permissions = uservo.getPermissions();
        if (null != roles && roles.size() > 0) {
            info.addRoles(roles);
        }
        if (null != permissions && permissions.size() > 0) {
            info.addStringPermissions(permissions);
        }
        return info;
    }

}
