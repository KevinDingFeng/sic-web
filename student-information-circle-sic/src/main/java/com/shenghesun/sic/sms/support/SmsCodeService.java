package com.shenghesun.sic.sms.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.shenghesun.sic.ehcache.support.EhcacheService;
import com.shenghesun.sic.utils.RandomUtil;

/**
 * 短信支持
 * 	发短信
 * 	校验短信
 * @author Kevin
 *
 */
@Service
@Transactional(readOnly = true)
public class SmsCodeService {
	
	@Value("${sms.app.key}")
	private String smsAppKey;

	@Value("${sms.app.secret}")
	private String smsAppSecret;
	
	@Value("${sms.template.code}")
	private String smsTemplateCode;
	
	@Value("${sms.sign}")
	private String smsSign;
	
	@Autowired
	private EhcacheService ehcacheService;
	
	
	public boolean sendSmsCode(String number) {
		//获取随机验证码
		String code = RandomUtil.randomNum();
		String msg;
		try {
			msg = this.sendSmsCode(number, code);//调用发送方法，返回发送的结果
		} catch (ClientException e) {
			e.printStackTrace();
			return false;
		}
		//确认发送成功并存入缓存后，返回结果
		if("success".equals(msg) && ehcacheService.smsCodePut(number, code)) {
			ehcacheService.smsCodePut(EhcacheService.SMS_CODE_TIME_KEY_PRE + number, 
				String.valueOf(System.currentTimeMillis()));
			return true;
		}
		return false;
	}
	
	public boolean check(String number, String code) {
		String cacheCode = ehcacheService.smsCodeGet(number);
		return code.equals(cacheCode);
	}
	
	
	
	
	public String sendSmsCode(String number, String code) throws ClientException {
		return this.sendSmsCode(number, code, smsTemplateCode);
	}

	/**
	 * 发送验证码到手机号
	 * 
	 * @author hao.tan
	 * @param number
	 *            手机号码
	 * @param code
	 *            验证码
	 * @return
	 * @throws ClientException
	 */
	public String sendSmsCode(String number, String code,String templateCode) throws ClientException {
		System.out.println("code:" + code);// TODO 正式发布时取消
		// 设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		// 初始化ascClient需要的几个参数
		final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
		// 替换成你的AK
		final String accessKeyId = smsAppKey;// 你的accessKeyId,参考本文档步骤2
		final String accessKeySecret = smsAppSecret;// 你的accessKeySecret，参考本文档步骤2
		// 初始化ascClient,暂时不支持多region
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		// 组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		// 使用post提交
		request.setMethod(MethodType.POST);
		// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		request.setPhoneNumbers(number);
		// 必填:短信签名-可在短信控制台中找到
//		request.setSignName("禾秱网");
		request.setSignName(smsSign);
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(templateCode);
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
		request.setTemplateParam("{\"no\":\"" + code + "\"}");
		// 可选-上行短信扩展码(无特殊需求用户请忽略此字段)
		// request.setSmsUpExtendCode("90997");
		// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		// request.setOutId("yourOutId");
		// 请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		System.out.println(sendSmsResponse.getCode());
		if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			//将验证码存储到缓存中(短信发送成功后再将 验证码存储到缓存中)
			System.out.println("验证码：" + code + "--" + "手机号：" + number); // TODO 正式发布时取消
			return "success";
		} else {
			return sendSmsResponse.getMessage();
		}
	}

	/**
	 * 缓存中对应的手机号，在一分钟之内存在记录，表示该手机号在一分钟之内发送了验证码，不重复发送
	 * 	不存在缓存 或 距上一次发送超过了一分钟，返回 true
	 * @param cellphone
	 * @return
	 */
	public boolean timeOutCheck(String cellphone) {
		String timeLong = ehcacheService.smsCodeGet(EhcacheService.SMS_CODE_TIME_KEY_PRE + cellphone);
		//一分钟校验
		return timeLong == null || System.currentTimeMillis() - Long.parseLong(timeLong) >= 60000L;
	}
	
}
