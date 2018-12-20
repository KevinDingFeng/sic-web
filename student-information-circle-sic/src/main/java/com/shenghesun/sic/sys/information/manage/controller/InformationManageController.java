package com.shenghesun.sic.sys.information.manage.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shenghesun.sic.core.constant.Authorized;
import com.shenghesun.sic.core.constant.Presentation;
import com.shenghesun.sic.core.constant.RequestMethods;
import com.shenghesun.sic.core.constant.UnauthorizedMessage;
import com.shenghesun.sic.core.constant.UploadFileSubpath;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.model.InformationCondition;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.InformationTypeService;
import com.shenghesun.sic.information.service.RichTextEditorService;
import com.shenghesun.sic.sso.model.LoginInfo;

import lombok.extern.slf4j.Slf4j;

/**
  * @ClassName: InformationController 
  * @Description: 信息 即 新闻等文章或图文内容
  * @author: yangzp
  * @date: 2018年11月6日 下午3:02:00  
  */
@Slf4j
@Controller
@RequestMapping("/sys/sys_new")
public class InformationManageController {
	
	@Autowired
	private InformationService informationService;
	
	@Autowired
	private RichTextEditorService richTextEditorService;
	
	@Autowired
	private InformationTypeService informationTypeService;
	
	/**
	 * 上传文件根路径
	 */
	@Value("${file.path.upload}")
	private String uploadFilePath;
	
	/**
	 * 设置允许自动绑定的属性名称
	 * 
	 * @param binder
	 * @param req
	 */
	@InitBinder(Presentation.KEY_ENTITY)
	private void initBinder(ServletRequestDataBinder binder, HttpServletRequest req) {
		List<String> fields = new ArrayList<String>(Arrays.asList("context", "title", "cost", "used", "verified"
				,"deleteImageFlag1","deleteImageFlag2","deleteImageFlag3","types"));
		switch (req.getMethod()) {
		case RequestMethods.POST: // 新增
			binder.setAllowedFields(fields.toArray(new String[fields.size()]));
			break;
		case RequestMethods.PUT: // 修改
			binder.setAllowedFields(fields.toArray(new String[fields.size()]));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 预处理，一般用于新增和修改表单提交后的预处理
	 * 
	 * @param id
	 * @param req
	 * @return
	 */
	@ModelAttribute(Presentation.KEY_ENTITY)
	public Information prepare(@RequestParam(value = Presentation.KEY_ID, required = false) Long id,
			HttpServletRequest req) {
		String method = req.getMethod();
		if (id != null && id > 0 && RequestMethods.POST.equals(method)) {// 修改表单提交后数据绑定之前执行
			return informationService.findOne(id);
		} else if (RequestMethods.POST.equals(method)) {// 新增表单提交后数据绑定之前执行
			return new Information();
		} else {
			return null;
		}
	}
	
	/**
	 * @Title: form 
	 * @Description: 编辑新增入口 
	 * @param model
	 * @param information
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月8日下午1:48:12
	 **/ 
	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, 
			@RequestParam(value = Presentation.KEY_ID, required = false) Information information) {
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_CREATE)) {
    		throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_CREATE);
    	}
		if(information == null) {
			information = new Information();
		}
		
		model.addAttribute("types",informationTypeService.findByActiveAndRemoved(true, false));
		
		model.addAttribute(Presentation.KEY_ENTITY, information);
		return "news/form";
	}
	
	/**
	 * @Title: view 
	 * @Description: 查看 
	 * @param model
	 * @param information
	 * @return  String 
	 * @author yangzp
	 * @date 2018年12月3日上午11:51:00
	 **/ 
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(Model model, 
			@RequestParam(value = Presentation.KEY_ID, required = true) Information information) {
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_VIEW)) {
    		throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_VIEW);
    	}
		if(information == null) {
			information = new Information();
		}
		model.addAttribute(Presentation.KEY_ENTITY, information);
		return "news/view";
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create (
			@RequestParam(value = "informationImg1", required = false) MultipartFile informationImg1,
			@RequestParam(value = "informationImg2", required = false) MultipartFile informationImg2,
			@RequestParam(value = "informationImg3", required = false) MultipartFile informationImg3,
			@Validated Information information, BindingResult result, Model model) {
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_CREATE)) {
    		throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_CREATE);
    	}
		
		if (result.hasErrors()) {
			return "news/form";
		}
		
		try {
			
			Subject subject = SecurityUtils.getSubject();
			LoginInfo info = (LoginInfo)subject.getPrincipal();
			setImages(information, informationImg1, informationImg2, informationImg3);
			information.setSysUserId(info.getId());
			information.setUserName(info.getName());
			
			setImages(information, informationImg1, informationImg2, informationImg3);
            
			information.setUuid(UUID.randomUUID().toString().replace("-", ""));
			informationService.save(information);
		} catch (Exception e) {
			log.error("create:"+e.getMessage());
			return "news/form";
		}
		
		return "redirect:/sys/sys_new/list";
	}
	
	/**
	 * @Title: update 
	 * @Description: 修改 
	 * @param id 新闻id
	 * @param informationImg1
	 * @param informationImg2
	 * @param informationImg3
	 * @param information
	 * @param result
	 * @param model
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月6日下午6:02:48
	 **/ 
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update (@RequestParam(value = Presentation.KEY_ID, required = true) Long id,
			@RequestParam(value = "informationImg1", required = false) MultipartFile informationImg1,
			@RequestParam(value = "informationImg2", required = false) MultipartFile informationImg2,
			@RequestParam(value = "informationImg3", required = false) MultipartFile informationImg3,
			@Validated @ModelAttribute(Presentation.KEY_ENTITY)Information information, BindingResult result, Model model) {
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_UPDATE)) {
    		LoginInfo info = (LoginInfo)sub.getPrincipal();
    		//登陆用户id
    		Long loginUserId = info.getId();
    		//新闻提交者id
    		Long sysUserId = information.getSysUserId();
    		if(loginUserId == null || !loginUserId.equals(sysUserId)) {//非自己创建的
    			throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_UPDATE);
    		}
    	}
		if (result.hasErrors()) {
			return "news/form";
		}
		
		try {
			setImages(information, informationImg1, informationImg2, informationImg3);
			information.setLastModified(new Timestamp(System.currentTimeMillis()));
			informationService.save(information);
		} catch (Exception e) {
			log.error("create:"+e.getMessage());
			return "news/form";
		}
		
		return "redirect:/sys/sys_new/list";
	}
	
	/**
	 * @Title: setImages 
	 * @Description: 图片上传或者修改 
	 * @param information
	 * @param informationImg1
	 * @param informationImg2
	 * @param informationImg3  void 
	 * @author yangzp
	 * @throws IOException 
	 * @date 2018年11月8日下午6:06:17
	 **/ 
	private void setImages(Information information, MultipartFile informationImg1, MultipartFile informationImg2, MultipartFile informationImg3) throws IOException {
		//获取之前图片信息
		String images = information.getImgs();
		if(StringUtils.isEmpty(images)) {
			images = " , , ";
		}
		String [] imagesArry = images.split(",");
		if (informationImg1 != null &&
		        !informationImg1.isEmpty() &&
		        !"".equals(informationImg1.getOriginalFilename())) {
			String uploadPath1 = richTextEditorService.uploadFile(informationImg1.getOriginalFilename(), informationImg1.getInputStream(), uploadFilePath + UploadFileSubpath.INFORMATION_IMAGE);
			if (StringUtils.isNotEmpty(uploadPath1)) {
				imagesArry[0] = UploadFileSubpath.INFORMATION_IMAGE + uploadPath1;
            }
		}
		
		if (informationImg2 != null &&
		        !informationImg2.isEmpty() &&
		        !"".equals(informationImg2.getOriginalFilename())) {
			String uploadPath2 = richTextEditorService.uploadFile(informationImg2.getOriginalFilename(), informationImg2.getInputStream(), uploadFilePath + UploadFileSubpath.INFORMATION_IMAGE);
			if (StringUtils.isNotEmpty(uploadPath2)) {
				imagesArry[1] = UploadFileSubpath.INFORMATION_IMAGE + uploadPath2;
            }
		}
		
		if (informationImg3 != null &&
		        !informationImg3.isEmpty() &&
		        !"".equals(informationImg3.getOriginalFilename())) {
			String uploadPath3 = richTextEditorService.uploadFile(informationImg3.getOriginalFilename(), informationImg3.getInputStream(), uploadFilePath + UploadFileSubpath.INFORMATION_IMAGE);
			if (StringUtils.isNotEmpty(uploadPath3)) {
				imagesArry[2] = UploadFileSubpath.INFORMATION_IMAGE + uploadPath3;
            }
		}
		
		//删除图片
        if(StringUtils.isNotEmpty(information.getDeleteImageFlag1())) {
        	imagesArry[0] = " ";
        }
        
        if(StringUtils.isNotEmpty(information.getDeleteImageFlag2())) {
        	imagesArry[1] = " ";
        }
        
        if(StringUtils.isNotEmpty(information.getDeleteImageFlag3())) {
        	imagesArry[2] = " ";
        }
		
        information.setImgs(StringUtils.join(imagesArry, ","));
	}
	
	/**
	 * @Title: uploadFile 
	 * @Description: 富文本图片保存 
	 * @param uploadFile
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年11月7日上午10:46:22
	 **/ 
	@ResponseBody
	@RequestMapping("/upload_file")
	public Object uploadFile(@RequestParam(value = "uploadFile", required = true) MultipartFile uploadFile) {
		if (uploadFile.getSize() != 0L) {
			try {
				String uploadPath = richTextEditorService.uploadFile(uploadFile.getOriginalFilename(), uploadFile.getInputStream(), uploadFilePath + UploadFileSubpath.RICHTEXT);
				String showPath = 
						richTextEditorService.getShowFilePath() + 
						UploadFileSubpath.RICHTEXT + 
						uploadPath;
				//String result = "{\"success\": 1, \"message\":\"上传成功\",\"url\":\"" + showPath + "\"}";
				return showPath;
			} catch (IOException e) {
				return "0";
			}
		}
		return null;
	}
	
	/**
	 * @Title: list 
	 * @Description: 新闻列表 
	 * @param pageable
	 * @param condition
	 * @param result
	 * @param model
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月7日下午5:03:27
	 **/ 
	@RequestMapping(value = "/list", method = {RequestMethod.POST,RequestMethod.GET})
	public String list(
			@PageableDefault(page = 0, value = Presentation.DEFAULT_PAGE_SIZE, sort = {
					Presentation.DEFAULT_ORDER_FIELD }, direction = Direction.DESC) Pageable pageable,
			 @ModelAttribute(Presentation.KEY_CONDITION ) InformationCondition condition, BindingResult result,
			 Model model) {
		Subject sub = SecurityUtils.getSubject();
		//非管理员和审核员,只能看自己的
    	if (!(sub.isPermitted(Authorized.INFORMATION_VERIFY) || 
    			sub.isPermitted(Authorized.INFORMATION_ROOT))) {
    		LoginInfo info = (LoginInfo)sub.getPrincipal();
    		condition.setSysUserId(info.getId());
    	}
		Page<Information> page = informationService.findByConditions(condition, pageable);
		
		model.addAttribute(Presentation.KEY_PAGE, page);
		
		return "news/list";
	}
	
	/**
	 * @Title: remove 
	 * @Description: 新闻删除
	 * @param id
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年11月8日下午12:15:39
	 **/ 
	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	@ResponseBody
	public Object remove(@RequestParam(value = Presentation.KEY_ID, required = true) String id) {
		
		Information information = informationService.findOne(Long.parseLong(id));
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_REMOVED)) {
    		LoginInfo info = (LoginInfo)sub.getPrincipal();
    		//登陆用户id
    		Long loginUserId = info.getId();
    		//新闻提交者id
    		Long sysUserId = information.getSysUserId();
    		if(loginUserId == null || !loginUserId.equals(sysUserId)) {//非自己创建的
    			//throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_REMOVED);
    			return "抱歉，您没有“"+UnauthorizedMessage.INFORMATION_REMOVED+"”操作权限，请联系管理员添加权限～";
    		}
    	}
		
		if(information != null) {
			information.setRemoved(true);
			informationService.save(information);
			return Presentation.KEY_SUCCESS;
		}
		
		return Presentation.KEY_FAILURE;
	}
	
}
