package com.shenghesun.sic.auth.support;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.config.CustomConfig;
import com.shenghesun.sic.system.entity.SysPermission;
import com.shenghesun.sic.system.entity.SysRole;
import com.shenghesun.sic.system.entity.SysUser;
import com.shenghesun.sic.system.service.SysUserService;
import com.shenghesun.sic.utils.HttpHeaderUtil;
import com.shenghesun.sic.utils.JWTUtil;
import com.shenghesun.sic.utils.RedisUtil;
import com.shenghesun.sic.utils.TokenUtil;

/**
 * 为用户登录做服务支持 支持登录用户 token 的获取 请求 token 的验证 登录用户权限的获取和判断
 * 将来可能扩展到为登出做服务支持，因为登出时，有很多缓存等信息需要清空，目前都使用过期时间的机制，暂时不会有影响
 * 
 * @author kevin
 *
 */
@Service
public class LoginService {

	@Autowired
	private SysUserService userService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 为登录成功的用户做服务支持 获取登录对应的 token 获取登录用户的 角色和权限信息 将用户的 token 和 角色权限信息放入 redis 缓存 返回
	 * token
	 * 
	 * @param userId
	 * @param account
	 * @return
	 */
	public String login(Long userId, String account) {
		String token = TokenUtil.create(userId, account);
		// 存入缓存
		redisUtil.set(CustomConfig.REDIS_TOKEN_PREFIX + token, token, CustomConfig.EXPIRE_TIME_SECOND);
		return token;
	}
	/**
	 * 为信息完整的授权用户提供一个特殊的 token ，用户表明该用户信息完整，可以进行高阶的操作
	 * @param userId
	 * @param account
	 * @return
	 */
	public String register(Long userId, String account) {		
		String token = TokenUtil.create(userId, account);
		// 存入缓存
		redisUtil.set(CustomConfig.REDIS_REGISTER_TOKEN_PREFIX + token, token, CustomConfig.EXPIRE_TIME_SECOND);
		return token;
	}
	
	/**
	 * 为登录成功的用户做服务支持 获取登录对应的 token 获取登录用户的 角色和权限信息 将用户的 token 和 角色权限信息放入 redis 缓存 返回
	 * token
	 * 
	 * @param userId
	 * @param account
	 * @return
	 */
	public String loginWithPerm(Long userId, String account) {
		String token = TokenUtil.create(userId, account);
		// 缓存用户的权限和角色
		JSONObject rolesAndPermsJson = this.getJSONRolesAndPerms(account);
		// 存入缓存
		redisUtil.set(CustomConfig.REDIS_TOKEN_PREFIX + token, rolesAndPermsJson, CustomConfig.EXPIRE_TIME_SECOND);
		return token;
	}

	/**
	 * 返回 JSONObject 格式的 角色和权限信息，格式： {
	 * roles:'{"ROLE1":"ID1"[,"ROLE2":"ID2"..."ROLE3":"ID3"]}'
	 * perms:'{"PERM1":"ID1"[,"PERM2":"ID2"..."PERM3":"ID3"]}' } 其中 roles 和 perms 在
	 * account 为空或用户不存在角色和权限信息时不存在。
	 * 
	 * @param account
	 * @return
	 */
	public JSONObject getJSONRolesAndPerms(String account) {
		JSONObject json = new JSONObject();
		if (StringUtils.isEmpty(account)) {
			return json;
		}
		SysUser user = userService.findByAccount(account);
		Set<SysRole> roles = user.getRoles();
		if (roles != null && roles.size() > 0) {
			Iterator<SysRole> roleIts = roles.iterator();
			JSONObject rolesObj = null;
			JSONObject permsObj = null;
			while (roleIts.hasNext()) {
				if (rolesObj == null) {
					rolesObj = new JSONObject();
				}
				SysRole role = roleIts.next();
				rolesObj.put(role.getName(), role.getId());// 添加角色信息

				Set<SysPermission> perms = role.getPermissions();
				Iterator<SysPermission> permIts = perms.iterator();
				while (permIts.hasNext()) {
					if (permsObj == null) {
						permsObj = new JSONObject();
					}
					SysPermission sysPermission = permIts.next();
					permsObj.put(sysPermission.getPerm(), sysPermission.getId());// 添加权限信息
				}
			}

			if (rolesObj != null) {
				json.put("roles", rolesObj);// {"ROLE1":"ID1"[,"ROLE2":"ID2"..."ROLE3":"ID3"]}
			}
			if (permsObj != null) {
				json.put("perms", permsObj);// {"PERM1":"ID1"[,"PERM2":"ID2"..."PERM3":"ID3"]}
			}
		}
		return json;
	}

	public JSONObject getJSONRolesAndPermsByToken(String token) {
		String userInfoId = redisUtil.get(CustomConfig.REDIS_TOKEN_PREFIX + token);// 根据业务需求，修改缓存的内容
		// System.out.println(userInfoId);//
		// {"roles":{"二级角色":1,"六级角色":2},"perms":{"role:update":3,"role:create":2,"role:view":1}}
		return JSONObject.parseObject(userInfoId);
	}

	public boolean hasRole(String token, String roleName) {
		return this.hasOne(this.getJSONRolesAndPermsByToken(token).getJSONObject("roles"), roleName);
	}

	public boolean hasOneRoles(String token, String[] roleNames) {
		return this.hasOne(this.getJSONRolesAndPermsByToken(token).getJSONObject("roles"), roleNames);
	}

	public boolean hasAllRoles(String token, String[] roleNames) {
		return this.hasAll(this.getJSONRolesAndPermsByToken(token).getJSONObject("roles"), roleNames);
	}

	public boolean hasPerms(String token, String perm) {
		return this.hasOne(this.getJSONRolesAndPermsByToken(token).getJSONObject("perms"), perm);
	}

	public boolean hasOnePerms(String token, String[] perms) {
		return this.hasOne(this.getJSONRolesAndPermsByToken(token).getJSONObject("perms"), perms);
	}

	public boolean hasAllPerms(String token, String[] perms) {
		return this.hasAll(this.getJSONRolesAndPermsByToken(token).getJSONObject("perms"), perms);
	}

	private boolean hasOne(JSONObject resource, String cell) {
		return resource == null ? false : resource.get(cell) != null;// 存在，即返回成功
	}

	private boolean hasOne(JSONObject resource, String[] cells) {
		if (resource == null) {
			return false;
		}
		if (cells == null || cells.length < 1) {
			return false;
		}
		for (String cell : cells) {
			if (resource.get(cell) != null) {
				return true;// 只要有一个存在，则返回成功
			}
		}
		return false;
	}

	private boolean hasAll(JSONObject resource, String[] cells) {
		if (resource == null) {
			return false;
		}
		if (cells == null || cells.length < 1) {
			return false;
		}
		for (String cell : cells) {
			if (resource.get(cell) == null) {
				return false;// 只要有一个不存在，则返回失败
			}
		}
		return true;
	}
	public boolean checkToken(HttpServletRequest req) {
		String token = HttpHeaderUtil.getToken(req);
		if(StringUtils.isEmpty(token)) {
			return false;
		}
		Map<String, Object> userInfoMap = TokenUtil.decode(token);
		if(userInfoMap.get(JWTUtil.ERR_MSG) != null) {
			return false;
		}
		String tokenVal = redisUtil.get(CustomConfig.REDIS_TOKEN_PREFIX + token);
		if(StringUtils.isEmpty(tokenVal)) {
			return false;
		}
		//更新有效时长
		redisUtil.set(CustomConfig.REDIS_TOKEN_PREFIX + token, tokenVal, CustomConfig.EXPIRE_TIME_SECOND);// 缓存token 到 redis ，使用配置中的时长
		return true;
	}
}
