package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Operation;

import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/create-access-token/*")
public class IssueAccessTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// URL からデバイス名を取得します.
		// e.g. /DesalinV1/test-servlet/sakura ---> デバイス名 "sakura"
		String requestURI = request.getRequestURI();
		int lastIndex = requestURI.lastIndexOf("/create-access-token/");
		String deviceName = requestURI.substring(lastIndex + "/create-access-token/".length());
		System.out.println("🍉TestServlet#doGet: deviceName:" + deviceName);
		
		// リクエストボディのバイト列を取得.
		int contentLength = request.getContentLength();
		byte[] ba = new byte[contentLength];
		try (InputStream is = request.getInputStream()) {
			int rdsz;
			for (int rdpos = 0; rdpos < contentLength; rdpos += rdsz) {
				rdsz = is.read(ba, 0, contentLength - rdpos);
				if (rdsz == -1) {
					// 予期しない EOF を検出.
					throw new IOException("unexpected requestbody length!");
				}
			}
		}
			
		// リクエストボディのバイト列を文字列に変換.
		String instr = new String(ba, "UTF-8");
		System.out.println("instr:" + instr);
		
		// リクエストボディのバイト列より，ｌJSON オブジェクトを生成.
		JSONObject jsonobj = new JSONObject(instr);
		System.out.println("jsonobj:" + jsonobj);
		
		String password = jsonobj.optString("password");
		String refreshtoken = jsonobj.optString("refresh");
		
		if (!password.isEmpty() && !refreshtoken.isEmpty()) {
			throw new ServletException("Both \"password\" and \"refresh\" were specified.");
		} else if (password.isEmpty() && refreshtoken.isEmpty()) {
			throw new ServletException("Neither \"password\" nor \"refresh\" was specified.");
		}
		
		JSONObject ret;
		if (!password.isEmpty()) {
			try {
				ret = Operation.issueAccessTokenFromPassword(deviceName, password);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException("こまったちゃん");
			}
			response.setContentType("application/json; charset=UTF-8");
			if (!ret.optString("result").equals("success")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			try (OutputStream os = response.getOutputStream()) {
				os.write(ret.toString().getBytes("UTF-8"));
			}
		} else {
			try {
				ret = Operation.issueAccessTokenFromRefreshToken(deviceName, refreshtoken);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException("こまったちゃん");
			}
			response.setContentType("application/json; charset=UTF-8");
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
		//response.sendError(400, "おろかものめ");
	}
}
