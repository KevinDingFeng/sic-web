package com.shenghesun.sic.freemarker.tag;

import java.util.Map;

import org.apache.shiro.subject.Subject;

import com.alibaba.druid.util.StringUtils;
import com.jagregory.shiro.freemarker.PermissionTag;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
/**
 * 自定义的 Freemarker 使用的 Shiro Tag 
 * 拥有传入权限的其中一个
 * 	参数名是 name 
 * 	参数值是 “[]” 形式的数组 或 以“,（英文半角逗号）”分隔的字符串
 * @author kevin
 *
 */
public class HasPermissionOneTag extends PermissionTag {
    
	/**
	 * 覆盖是否显示标签内部信息的方法 2
	 */
	protected boolean showTagBody(String p) {
//        return isPermitted(p);//hasPermission 标签的判断是否用当前权限的逻辑
    	//下面自定义判断 在多个权限其中拥有一个 的逻辑
		if(StringUtils.isEmpty(p)) {
			return false;
		}
    	return hasPermittedOne(p.split(","));
    }
	/**
	 * 覆盖获取标签 name 属性值的方法 1
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected String getParam(Map params, String name) {
        Object value = params.get(name);
        if(value == null) {
        	return "";
        }
        if (value instanceof SimpleSequence) {
        	SimpleSequence ss = (SimpleSequence)value;
        	StringBuilder bu = new StringBuilder();
        	try {
        		bu.append(ss.get(0).toString());
        		for(int i = 1 ; i < ss.size() ; i++) {
        			bu.append(",");
        			bu.append(ss.get(i).toString());
        		}
        	}catch(TemplateModelException te) {
        		te.printStackTrace();
        		System.out.println("解析权限标签时出错");
        	}
 
//            return ((SimpleSequence)value).toString();//为了严格要求，所以使用了类型判断，其实只要是字符串，感觉都可以
        	return bu.toString();
        }
        return value.toString();
    }
	/**
	 * 自定义的判断是否拥有传入权限其中的一个，只要拥有其中的一个，即返回true
	 * @param ps
	 * @return
	 */
	private boolean hasPermittedOne(String[] ps) {
		if(getSubject() == null) {
			return false;
		}
		if(ps == null || ps.length < 1) {
			return false;
		}
		Subject s = getSubject();
		for(String p : ps) {
			if(s.isPermitted(p)) {
				return true;
			}
		}
        return false;
    }
}
