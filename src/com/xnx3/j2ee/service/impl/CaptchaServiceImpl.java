package com.xnx3.j2ee.service.impl;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.xnx3.j2ee.service.CaptchaService;
import com.xnx3.j2ee.vo.BaseVO;
import com.xnx3.media.CaptchaUtil;

/**
 * 验证码
 * @author 管雷鸣
 */
public class CaptchaServiceImpl implements CaptchaService {
	
	/**
	 * 显示图片验证码，直接创建出jpg格式图片
	 * @param captchaUtil {@link CaptchaUtil} 可对其自定义验证码图片的属性、显示、格式等
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public void showImage(CaptchaUtil captchaUtil, HttpServletRequest request, HttpServletResponse response) throws IOException{
		//将验证码保存到Session中。
		HttpSession session = request.getSession();
		session.setAttribute("code", captchaUtil.getCode());

		//禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		response.setContentType("image/jpeg");

		//将图像输出到Servlet输出流中。
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(captchaUtil.createImage(), "jpeg", sos);
		sos.close();
	}
	
	/**
	 * 显示图片验证码，直接创建出jpg格式图片
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public void showImage(HttpServletRequest request, HttpServletResponse response) throws IOException{
		showImage(new CaptchaUtil(), request, response);
	}
	
	/**
	 * 用户输入的验证码，与系统存储的进行比较，返回结果
	 * @param inputCode 用户输入的验证码
	 * @param request HttpServletRequest 主要用于从其中获取Session
	 * @return {@link BaseVO}
	 */
	public BaseVO compare(String inputCode, HttpServletRequest request){
		BaseVO vo = new BaseVO();
		if(inputCode == null || inputCode.length() == 0){
			vo.setBaseVO(BaseVO.FAILURE, "请输入验证码");
			return vo;
		}
		
		//获取Session记录的验证码
		Object codeObj = request.getSession().getAttribute("code");
		if(codeObj == null){
			vo.setBaseVO(BaseVO.FAILURE, "出错，系统中没有存储您的验证码！");
			return vo;
		}
		String code = (String) codeObj;
		if(inputCode.equals(code)){
			return vo;
		}else{
			vo.setBaseVO(BaseVO.FAILURE, "验证码出错");
			return vo;
		}
	}
}