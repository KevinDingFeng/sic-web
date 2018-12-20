package com.shenghesun.sic.information.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.utils.FileIOUtil;


/**
 * 保存富文本图片
 * @author zhanping.yang
 * @version 创建时间：2017年11月14日  上午11:30:59
 */
@Service
public class RichTextEditorService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Value("${file.path.show}")
	private String showFilePath;
	
	
	public String getShowFilePath() {
		return showFilePath;
	}

	private Set<String> fileAllowExt;
	
	@Value("${richEditor.uploadFileAllowExt:jpg jepg gif png flv mp4 mp3 swf}")
	public void setFileAllowExt(String str){
		if(StringUtils.isNotBlank(str)){
			fileAllowExt = new HashSet<String>(Arrays.asList(StringUtils.split(str, ' ' )));
		}
	}
	
	
	public Set<String> getFileAllowExt(){
		return fileAllowExt;
	}
	
	/**
	 * 保存上传文件
	 * @param filename the original filename in the client's filesystem.
	 * @param is 上传文件流
	 * @return web中的文件访问路径
	 */
	/**
	 * 保存富文本图片
	 * @param filename
	 * @param is
	 * @return
	 */
	public String uploadFile(String filename, InputStream is, String fileSavePath){
		return FileIOUtil.uploadFile(filename, is, fileSavePath, true);
	}
	
	/**
	 * 显示图片
	 * @param response
	 * @param fullFileName
	 *            图片文件全路径
	 * @throws IOException
	 */
	public  void showPicture(HttpServletResponse response,String fullFileName) {
		// 获得请求文件名
		// String filename = request.getParameter("filename");

		// 设置文件MIME类型
		// response.setContentType("image/jpg"); //设置返回的文件类型
		// 设置Content-Disposition
		// response.setHeader("Content-Disposition",
		// "attachment;filename=index_jyfxzz_icon_shen.png");
		// 读取目标文件，通过response将目标文件写到客户端
		// 读取文件
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(fullFileName);

			out = response.getOutputStream();
			// 写文件
			byte[] content = new byte[1024 * 1024];
			int b = 0;
			while ((b = in.read(content)) != -1) {
				out.write(content, 0, b);
			}
			out.flush();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}

	}
	
	/**
	 * 下载文件
	 * @author zhanping.yang
	 * @version 创建时间：2018年06月19日  上午11:45:19
	 * @param response
	 * @param basePath
	 * @param fullFileName
	 */
	public void fileDownload(HttpServletResponse response, String fullFileName) {
		// 获得请求文件名
		try {
			fullFileName = new String(fullFileName.getBytes("utf-8"), "ISO_8859_1");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		// 设置文件MIME类型
		response.setContentType("multipart/form-data");   //设置文件ContentType类型，这样设置，会自动判断下载文件类型  
		// 设置Content-Disposition
		 //设置文件头：最后一个参数是设置下载文件名
		 response.setHeader("Content-Disposition","attachment;filename="+fullFileName.substring(fullFileName.lastIndexOf("/")+1));
		// 读取目标文件，通过response将目标文件写到客户端
		// 读取文件
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(fullFileName);

			out = response.getOutputStream();
			// 写文件
			byte[] content = new byte[1024 * 1024];
			int b = 0;
			while ((b = in.read(content)) != -1) {
				out.write(content, 0, b);
			}
			out.flush();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
}
