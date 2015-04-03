package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.fts.CategoryIndex;
import com.gae.pidictcloud.fts.PhraseIndex;
import com.gae.pidictcloud.fts.QuestionIndex;
import com.gae.pidictcloud.util.Constant;

@SuppressWarnings("serial")
public class DeleteDataServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		try {
			req.getRequestDispatcher("/static/templates/deleteindex.html")
			.forward(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String accessCode = req.getParameter("code");
		if (Constant.ACCESS_CODE.equals(accessCode)) {
			String[] docIds = URLDecoder.decode(
					req.getParameter("docids_input"), "UTF-8").trim().split(";");
			String type = URLDecoder.decode(
					req.getParameter("type"), "UTF-8").trim();
			String res = "Deletion is failed";
			if(docIds.length>0)
			{
				if("P".equals(type))
				{
					res = PhraseIndex.Delete(docIds);
				}else if("C".equals(type))
				{
					res = CategoryIndex.Delete(docIds);
				}else if("Q".equals(type))
				{
					res = QuestionIndex.Delete(docIds);
				}
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println(res);
			}
			
		}else {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(Constant.ACCESS_DENIED);
		}
	}
}
