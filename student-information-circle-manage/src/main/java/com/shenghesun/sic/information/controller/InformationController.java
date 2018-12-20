package com.shenghesun.sic.information.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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

import com.shenghesun.sic.core.constant.Presentation;
import com.shenghesun.sic.core.constant.RequestMethods;
import com.shenghesun.sic.core.constant.UploadFileSubpath;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.model.InformationCondition;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.RichTextEditorService;

import lombok.extern.slf4j.Slf4j;

/**
  * 信息 即 新闻等文章或图文内容
  * @ClassName: InformationController 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年11月6日 下午3:02:00  
  */
@Slf4j
@Controller
@RequestMapping("/sys/sys_new")
public class InformationController {
	
	@Autowired
	private InformationService informationService;
	
	@Autowired
	private RichTextEditorService richTextEditorService;
	
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
				,"deleteImageFlag1","deleteImageFlag2","deleteImageFlag3"));
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
	 * 编辑新增入口
	 * @Title: form 
	 * @Description: TODO 
	 * @param model
	 * @param information
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月8日下午1:48:12
	 **/ 
	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, 
			@RequestParam(value = Presentation.KEY_ID, required = false) Information information) {
		if(information == null) {
			information = new Information();
		}
		model.addAttribute(Presentation.KEY_ENTITY, information);
		return "news/form";
	}
	
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(Model model, 
			@RequestParam(value = Presentation.KEY_ID, required = true) Information information) {
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
		if (result.hasErrors()) {
			return "news/form";
		}
		
		try {
			setImages(information, informationImg1, informationImg2, informationImg3);
            
			information.setSysUserId(1L);
			information.setUserName("任强");;
			
			information.setUuid(UUID.randomUUID().toString().replace("-", ""));
			informationService.save(information);
		} catch (Exception e) {
			log.error("create:"+e.getMessage());
			return "news/form";
		}
		
		return "redirect:/sys_new/list";
	}
	
	/**
	 * 修改
	 * @Title: create 
	 * @Description: TODO 
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
		if (result.hasErrors()) {
			return "news/form";
		}
		
		try {
			setImages(information, informationImg1, informationImg2, informationImg3);
			information.setSysUserId(1L);
			
			information.setLastModified(new Timestamp(System.currentTimeMillis()));
			informationService.save(information);
		} catch (Exception e) {
			log.error("create:"+e.getMessage());
			return "news/form";
		}
		
		return "redirect:/sys_new/list";
	}
	
	/**
	 * 图片上传或者修改
	 * @Title: setImages 
	 * @Description: TODO 
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
	 * 富文本图片保存
	 * @Title: uploadFile 
	 * @Description: TODO 
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
	 * 
	 * @Title: list 
	 * @Description: TODO 
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
		
		Page<Information> page = informationService.findByConditions(condition, pageable);
		
		model.addAttribute(Presentation.KEY_PAGE, page);
		
		return "news/list";
	}
	
	/**
	 * 新闻删除
	 * @Title: remove 
	 * @Description: TODO 
	 * @param id
	 * @return  Object 
	 * @author yangzp
	 * @date 2018年11月8日下午12:15:39
	 **/ 
	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	@ResponseBody
	public Object remove(@RequestParam(value = Presentation.KEY_ID, required = true) String id) {
		
		Information information = informationService.findOne(Long.parseLong(id));
		
		if(information != null) {
			information.setRemoved(true);
			informationService.save(information);
			return Presentation.KEY_SUCCESS;
		}
		
		return Presentation.KEY_FAILURE;
	}
	
}
