package com.shenghesun.sic.freemarker.conf;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.shenghesun.sic.freemarker.tag.ShiroTagsExtend;

import cn.org.rapid_framework.freemarker.directive.BlockDirective;
import cn.org.rapid_framework.freemarker.directive.ExtendsDirective;
import cn.org.rapid_framework.freemarker.directive.OverrideDirective;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
/**
 *	使用 freeMarker 标签的设置 
 * @author kevin
 *
 */
@Component
public class FreeMarkerConfigExtend {

	
	@Autowired  
	private FreeMarkerConfigurer freeMarkerConfigurer;  
	  
	@PostConstruct  
	public void setSharedVariable()throws TemplateModelException {  
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
//		configuration.setSharedVariable("shiro", new ShiroTags());//Shiro-Freemarker-Tag包中自定义的标签
		configuration.setSharedVariable("shiro", new ShiroTagsExtend());//扩展Shiro-Freemarker-Tag包
		configuration.setSharedVariable("block", new BlockDirective());  
		configuration.setSharedVariable("override", new OverrideDirective());  
		configuration.setSharedVariable("extends", new ExtendsDirective());  
		
//		configuration.setClassicCompatible(true);
		configuration.setTemplateExceptionHandler(new FreemarkerExceptionHandler());//引入自定义的freemarker错误处理方式
	} 
//	@Override  
//	public void afterPropertiesSet() throws IOException, TemplateException {  
//        super.afterPropertiesSet();
//        Configuration cfg = this.getConfiguration();
//        cfg.setSharedVariable("shiro", new ShiroTags());//shiro标签
//        cfg.setNumberFormat("#");//防止页面输出数字,变成2,000
//        //可以添加很多自己的要传输到页面的[方法、对象、值]
//    } 
}
