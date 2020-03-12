/**   
 * Copyright © 2020 eSunny Info. Tech Ltd. All rights reserved.
 * 
 * @Package: xyz.wadewhy.config 
 * @author: 钟子豪   网站wadewhy.xyz
 * @date: 2020年3月12日 下午4:12:53 
 */
package xyz.wadewhy.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import lombok.Data;
import xyz.wadewhy.realm.UserRealm;

/**
* @author 钟子豪
* 作者 E-mail:wadewhy@yeah.net
* @version 创建时间：2020年3月12日 下午4:12:53
* 类说明
*/
/** 
* @ClassName: ShiroAutoConfiguration 
* @Description: TODO
* @author: wadewhy
* @date: 2020年3月12日 下午4:12:53  
*/
// 定义配置类
@Configuration
// Web应用环境下起效
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// 某个class位于类路径上，才会实例化一个Bean
@ConditionalOnClass(value = { SecurityManager.class })
// 参数设置或者值一致时起效
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroAutoConfiguration {
    // 用于thymeleaf和shiro标签配合使用
    private static final String SHIRO_DIALECT = "shiroDialect";
    private static final String SHIRO_FILTER = "shiroFilter";
    private String hashAlgorithmName = "md5";// 加密方式
    private int hashIterations = 2;// 散列次数
    private String loginUrl = "/index.html";// 默认的登录界面
    private String NoPermission = "/index.html";// 没有权限
    private String[] anonUrls;// 可以匿名访问的
    private String logOutUrl;// 登出路径
    private String[] authcUrls;// 需要有某种权限

    /**
     * 声明凭证匹配器,前端过来的密码加密后匹配
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        // 设置加密方式
        credentialsMatcher.setHashAlgorithmName(hashAlgorithmName);
        // 设置加密次数
        credentialsMatcher.setHashIterations(hashIterations);
        return credentialsMatcher;
    }

    /**
     * 声明userRealm
     *
     * @param credentialsMatcher
     * @return
     */
    @Bean
    public UserRealm userRealm(CredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        // 注入凭证匹配器
        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }

    /**
     * 配置securityManager,注入自定义realm，进行认证授权
     * SecurityManager管理着很多功能模块，SecurityManager是个接口
     *
     * @param userRealm
     * @return
     */
    @Bean
    public SecurityManager securityManager(UserRealm userRealm) {
        // 得到SecurityManager
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入userRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 过滤工厂类
     * 1. 定义 ShiroFilterFactoryBean
     * 2. 设置SecurityManager
     * 3. 配置拦截器
     * 4. 返回ShiroFilterFactoryBean
     *
     * @param securityManager
     * @return
     */
    @Bean(SHIRO_FILTER)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        // 设置未登录是要跳转的页面
        factoryBean.setLoginUrl(loginUrl);
        // 无权限时
        // factoryBean.setUnauthorizedUrl(NoPermission);

        // 保存路径
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        if (authcUrls != null && anonUrls.length > 0) {
            for (String anon : anonUrls) {
                filterChainDefinitionMap.put(anon, "anon");// 直接放行的路径
            }
        }
        // 保存登出路径
        if (null != logOutUrl) {
            filterChainDefinitionMap.put(loginUrl, "logout");
        }
        if (authcUrls != null && authcUrls.length > 0) {
            for (String authc : authcUrls) {
                filterChainDefinitionMap.put(authc, "authc");// 需要验证
            }
        }
        Map<String, Filter> filterMap = new HashMap<>();
        // 配置过滤器
        factoryBean.setFilters(filterMap);
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }

    /**
     * 相当于之前在web.xml中注册shiro的委托过滤器
     * 一个filter的代理
     *
     * @return
     */
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName(SHIRO_FILTER);
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;

    }

    /* 加入注解的使用，不加入这个注解不生效--开始 */
    /**
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    /* 加入注解的使用，不加入这个注解不生效--结束 */

    /**
     * 这里是为了能在html页面引用shiro标签，上面两个函数必须添加，不然会报错
     *
     * @return
     */
    @Bean(name = SHIRO_DIALECT)
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
