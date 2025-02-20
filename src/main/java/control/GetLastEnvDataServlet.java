package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Operation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.json.JSONObject;

/**
 * Servlet implementation class GetLastEnvDataServlet
 */
@WebServlet("/get-last-envdata/*")
public class GetLastEnvDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLastEnvDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	public static String getDeviceNameFromURI(String requestURI, String pattern) {
		int lastIndex = requestURI.lastIndexOf(pattern);
		String deviceName = requestURI.substring(lastIndex + pattern.length());
		return deviceName;
	}

    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// リクエスト URL から，ユーザID, パスワード, デバイス名を取得
		String requestURI = request.getRequestURI();
		String pattern = "/get-last-envdata/";
		String userId = "";
		String password = "";
		String deviceName = "";
		try {
			int lastIndex0 = requestURI.lastIndexOf(pattern);
			String userInfo = requestURI.substring(lastIndex0 + pattern.length());
			int lastIndex1 = userInfo.lastIndexOf('/');
			deviceName = userInfo.substring(lastIndex1 + 1);
			int lastIndex2 = userInfo.lastIndexOf('/', lastIndex1 - 1);
			password = userInfo.substring(lastIndex2 + 1, lastIndex1);
			int lastIndex3 = userInfo.lastIndexOf('/', lastIndex2 - 1);
			userId = userInfo.substring(lastIndex3 + 1, lastIndex2);
		} catch (Exception ignore) {
		}
		
		
		try {
			JSONObject ret = Operation.getLastEnvData(userId, password, deviceName);
			response.setContentType("application/json; charset=UTF-8");
			//response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
			response.setHeader("Access-Control-Allow-Origin", "*");
			
			if (!ret.optString("result").equals("success")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

			try (OutputStream os = response.getOutputStream()) {
				os.write(ret.toString().getBytes("UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("こまったちゃん");
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
		
		out.println("<p>userId:\"" + userId + "\"</p>");
		out.println("<p>password:\"" + password + "\"</p>");
		out.println("<p>deviceName:\"" + deviceName + "\"</p>");
		out.println("<p>ContentType:\"" + request.getContentType() + "\"</p>");
		out.println("<p>ContentLength:" + request.getContentLength() + "</p>");
	
		
		out.println("<p>");
		out.println("getServletPath:");
		out.println(request.getServletPath());
		out.println("</p>");
		
		out.println("</body>");
		out.println("</html>");
		*/
	}

}
