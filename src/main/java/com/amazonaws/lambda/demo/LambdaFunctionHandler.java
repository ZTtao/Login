package com.amazonaws.lambda.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.lambda.demo.dto.RequestModel;
import com.amazonaws.lambda.demo.dto.User;
import com.amazonaws.lambda.demo.dto.util.BaseInfoRecorder;
import com.amazonaws.lambda.demo.dto.util.TokenUtil;
import com.amazonaws.lambda.demo.service.IUserService;
import com.amazonaws.lambda.demo.service.impl.UserServiceImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<RequestModel, String> {

	@Override
	public String handleRequest(RequestModel input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("[" + new Date() + "]Input: " + JSONObject.toJSONString(input));
		String userName = input.getUserName();
		String password = input.getPassword();
		logger.log("[" + new Date() + "]userName:" + userName + ",password:" + password);
		Map<String, Object> map = new HashMap<>();
		User user = null;
		if (userName != null && !userName.equals("") && password != null && !password.equals("")) {
			IUserService userService = new UserServiceImpl();
			user = userService.login(context, userName, password);
			if (user != null) {
				String token = TokenUtil.getToken(user);
				logger.log("[" + new Date() + "]login success,token:" + token);
				map.put("success", true);
				map.put("token", token);
			} else {
				logger.log("[" + new Date() + "]login failed:userName or password error.");
				map.put("success", false);
				map.put("message", "userName or password error.");
			}
		} else {
			map.put("success", false);
			map.put("message", "user name or password is null.");
		}
		logger.log("[" + new Date() + "]login:" + map);
		// 访问信息记录
		BaseInfoRecorder.record(user == null ? "unknow" : user.getUserId().toString(), input.getBaseInfo(),
				"[" + new Date() + "]login:" + JSONObject.toJSONString(map));
		return JSONObject.toJSONString(map);
	}

}
