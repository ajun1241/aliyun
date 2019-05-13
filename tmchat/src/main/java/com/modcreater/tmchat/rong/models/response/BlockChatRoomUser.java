package com.modcreater.tmchat.rong.models.response;

import io.rong.util.GsonUtil;

/**
 * 聊天室被封禁用户信息。
 * @author RongCloud
 */
public class BlockChatRoomUser {
	// 聊天室用户Id。
	String userId;
	// 加入聊天室时间。
	String time;
	
	public BlockChatRoomUser(String userId, String time) {
		this.userId = userId;
		this.time = time;
	}
	
	/**
	 * 设置userId
	 *
	 */	
	public io.rong.models.response.BlockChatRoomUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	/**
	 * 获取userId
	 *
	 * @return String
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * 设置time
	 *
	 */	
	public io.rong.models.response.BlockChatRoomUser setTime(String time) {
		this.time = time;
		return this;
	}
	
	/**
	 * 获取time
	 *
	 * @return String
	 */
	public String getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return GsonUtil.toJson(this, io.rong.models.response.BlockChatRoomUser.class);
	}
}
