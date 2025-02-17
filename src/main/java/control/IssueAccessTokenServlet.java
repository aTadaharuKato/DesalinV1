package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MyHelper;
import model.Operation;

import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/issue-access-token/*")
public class IssueAccessTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String deviceName = MyHelper.getDeviceNameFromURI(request.getRequestURI(), "/issue-access-token/");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		
		String password = jsonobj.optString("password");
		String refreshtoken = jsonobj.optString("refresh");
		
		if (!password.isEmpty() && !refreshtoken.isEmpty()) {
			throw new ServletException("Both \"password\" and \"refresh\" were specified.");
		} else if (password.isEmpty() && refreshtoken.isEmpty()) {
			throw new ServletException("Neither \"password\" nor \"refresh\" was specified.");
		}
		
		JSONObject ret;
		try {
			if (!password.isEmpty()) {
				ret = Operation.issueAccessTokenFromPassword(deviceName, password);
			} else {
				ret = Operation.issueAccessTokenFromRefreshToken(deviceName, refreshtoken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("こまったちゃん");
		}
		if (!ret.optString("result").equals("success")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		try (OutputStream os = response.getOutputStream()) {
			os.write(ret.toString().getBytes("UTF-8"));
		}

		/*
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>テスト</title>");
		out.println("</head>");
		out.println("<body>");
		
		out.println("<p>");
		out.println("getRequestURL:");
		out.println(new String(request.getRequestURL()));
		out.println("</p>");
		
		out.println("<p>");
		out.println("getRequestURI:");
		out.println(request.getRequestURI());
		out.println("</p>");
		
		out.println("<p>requestURL.lastIndexOf(\"/test-servlet/\") = " + lastIndex + "</p>");
		out.println("<p>deviceName:\"" + deviceName + "\"</p>");
		out.println("<p>ContentType:\"" + request.getContentType() + "\"</p>");
		out.println("<p>ContentLength:" + request.getContentLength() + "</p>");
		out.println("<p>password:" + password + "</p>");
		out.println("<p>refreshtoken:" + refreshtoken + "</p>");
	
		
		out.println("<p>");
		out.println("getServletPath:");
		out.println(request.getServletPath());
		out.println("</p>");
		
		out.println("</body>");
		out.println("</html>");
		*/
	}
}
