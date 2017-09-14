package com.amazonaws.lambda.demo.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.lambda.demo.dto.User;
import com.amazonaws.lambda.demo.dto.util.Constant;
import com.amazonaws.lambda.demo.service.IUserService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class UserServiceImpl implements IUserService {

	@Override
	public User login(Context context, String userName, String password) {
		LambdaLogger logger = context.getLogger();
		try {
			Class.forName(Constant.MYSQL_DRIVER);
			Connection conn = DriverManager.getConnection(Constant.MYSQL_URL, Constant.MYSQL_USER,
					Constant.MYSQL_PASSWORD);
			if (!conn.isClosed()) {
				logger.log("[" + new Date() + "]connect to database success.");
			}
			Statement statement = conn.createStatement();
			String sql = "select * from user where user_name='" + userName + "' and password='" + password + "'";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			Integer userId = rs.getInt("user_id");
			String userName1 = rs.getString("user_name");
			User user = new User();
			user.setUserId(userId);
			user.setUserName(userName1);
			logger.log("[" + new Date() + "]user:" + JSONObject.toJSONString(user));
			rs.close();
			conn.close();
			return user;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
