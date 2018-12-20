package com.shenghesun.sic.freemarker.tag;

import com.jagregory.shiro.freemarker.ShiroTags;

/**
 * 对 Shiro-Freemarker-Tag 的扩展
 * @author kevin
 *
 */
public class ShiroTagsExtend extends ShiroTags{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4158332525418452042L;

	public ShiroTagsExtend() {
		super();
		put("hasPermissionOne", new HasPermissionOneTag());
	}
}
