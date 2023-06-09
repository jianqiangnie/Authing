package com.zut.admin.security.handle;


import cn.hutool.json.JSONUtil;
import com.zut.admin.security.util.JwtTokenUtil;
import com.zut.admin.security.util.RedisUtil;
import com.zut.common.result.AjaxResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Resource
	private JwtTokenUtil jwtTokenUtil;

	@Resource
	private RedisUtil redisUtil;

	/**
	 * 登录成功
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = response.getOutputStream();

		// 获取登录用户信息
		UserDetails principal = (UserDetails)authentication.getPrincipal();  //用户信息

		// 生成token
		String token = jwtTokenUtil.createToken(principal.getUsername());

		// // redis中的token
		// String redisToken = redisUtil.get(principal.getUsername());
		//
		// if (redisToken!=null){
		// 	token=redisToken;
		// }else {
		// 	// 将token保存到redis中
		// 	redisUtil.set(principal.getUsername(),token);
		// }

		Map map=new HashMap();
		map.put("token",token);

		AjaxResult result = AjaxResult.ok(map);   //往前端返回token

		outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));

		outputStream.flush();
		outputStream.close();

	}
}
