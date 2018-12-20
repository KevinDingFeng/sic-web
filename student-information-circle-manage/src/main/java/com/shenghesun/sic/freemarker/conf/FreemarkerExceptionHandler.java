package com.shenghesun.sic.freemarker.conf;

import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
/**
 * freemarker 异常处理，页面出现 freemarker 错误后，不再显示到页面
 * @author kevin
 *
 */
public class FreemarkerExceptionHandler implements TemplateExceptionHandler {

	@Override
	public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
//		te.printStackTrace();//把 异常信息 打印到控制台
//		 try {
//			out.write("[出错了，请联系网站管理员：");//会输出到页面出现 异常 的地方
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
