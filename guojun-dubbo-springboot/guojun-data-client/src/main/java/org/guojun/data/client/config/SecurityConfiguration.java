package org.guojun.data.client.config;

import java.util.ArrayList;
import java.util.List;

import org.guojun.data.client.security.CsrfSecurityRequestMatcher;
import org.guojun.data.client.security.LoginAuthenticationFailureHandler;
import org.guojun.data.client.security.LoginAuthenticationSuccesssHandler;
import org.guojun.data.client.security.MyAccessDecisionManager;
import org.guojun.data.client.security.MyAccessDeniedHandler;
import org.guojun.data.client.security.MyFilterSecurityInterceptor;
import org.guojun.data.client.security.MyInvocationSecurityMetadataSource;
import org.guojun.data.client.security.MyUserDetailsServicesImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * 
 * @Description Spring Security 配置
 * @author Guojun
 * @Date 2018年6月4日 下午11:39:48
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	//@Bean方法中参数传入其他Bean作为参数，spring会自动注入
	//直接调用@Bean注解的方法，spring会自动注入该方法生成的Bean

	/**
	 * 用户权限信息校验管理器
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(myUserDetailsService())
				.passwordEncoder(passwordEncoder());
	}

	/**
	 * 请求拦截
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			//CSRF拦截
			.csrf()
				.disable()
				.requestMatcher(csrfSecurityRequest())
			//异常处理
			.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler())
		.and()
			//登录配置
			.formLogin()
				.loginPage("/user/gotoLogin")
				.loginProcessingUrl("/user/doLogin")
				.failureHandler(loginAuthenticationFailureHandler())
				.successHandler(loginAuthenticationSuccesssHandler())
		.and()
			//退出配置
			.logout()
				.logoutUrl("/user/logout")
				.logoutSuccessUrl("/user/gotoLogin")
		.and()
			//自定义拦截器
			.addFilterAfter(myFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
		;
	}
	
	/**
	 * 定义自定义身份验证
	 * @return
	 */
	@Bean
	public MyUserDetailsServicesImpl myUserDetailsService() {
		return new MyUserDetailsServicesImpl();
	}
	
	/**
	 * 密码加密器
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 登录失败的处理
	 * @return
	 */
	@Bean
	public LoginAuthenticationFailureHandler loginAuthenticationFailureHandler() {
		return new LoginAuthenticationFailureHandler();
	}
	
	/**
	 * 登录成功的处理
	 * @return
	 */
	@Bean
	public LoginAuthenticationSuccesssHandler loginAuthenticationSuccesssHandler() {
		return new LoginAuthenticationSuccesssHandler();
	}
	
	/**
	 * 没有权限的处理
	 * @return
	 */
	@Bean
	public MyAccessDeniedHandler accessDeniedHandler() {
		return new MyAccessDeniedHandler();
	}
	
	/**
	 * 加载资源与权限的全部对应关系
	 * @return
	 */
	@Bean
	public MyInvocationSecurityMetadataSource securityMetadataSource() {
		return new MyInvocationSecurityMetadataSource();
	}
	
	/**
	 * 授权器，对登录的用户的权限信息、资源，获取资源所有权限来根据不同的授权策略判断用户是否有权限访问资源
	 * @return
	 */
	@Bean
	public MyAccessDecisionManager accessDecisionManager() {
		return new MyAccessDecisionManager();
	}
	
	/**
	 * 自定义拦截器
	 * @param authenticationManager
	 * @param securityMetadataSource
	 * @param accessDecisionManager
	 * @return
	 * @throws Exception 
	 */
	@Bean
	public MyFilterSecurityInterceptor myFilterSecurityInterceptor() throws Exception {
		MyFilterSecurityInterceptor myFilterSecurityInterceptor = new MyFilterSecurityInterceptor();
		myFilterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
		myFilterSecurityInterceptor.setSecurityMetadataSource(securityMetadataSource());
		myFilterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		
		return myFilterSecurityInterceptor;
	}

	/**
	 * 排除某系URL不进行csrf拦截
	 * @return
	 */
	@Bean
	public CsrfSecurityRequestMatcher csrfSecurityRequest() {
		CsrfSecurityRequestMatcher csrfSecurityRequestMatcher = new CsrfSecurityRequestMatcher();
		
		List<String> excludeURls = new ArrayList<>();
		excludeURls.add("/user/doLogin");
		excludeURls.add("/user/logout");
		csrfSecurityRequestMatcher.setExcludeURls(excludeURls);
		
		return csrfSecurityRequestMatcher;
	}
}