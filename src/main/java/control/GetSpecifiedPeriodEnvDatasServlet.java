package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MyHelper;
import model.Operation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetSpecifiedPeriodEnvDatasServlet
 */
@WebServlet("/get-specified-period-envdatas")
public class GetSpecifiedPeriodEnvDatasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("GetSpecifiedPeriodEnvDatasServlet: (POST)");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		String userToken = jsonobj.optString("token");
		String deviceName = jsonobj.optString("device");
		Date datetime_from = MyHelper.getDatefromDateTimeStringWithTZ(jsonobj.optString("from"));
		Date datetime_until = MyHelper.getDatefromDateTimeStringWithTZ(jsonobj.optString("until"));
		
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		HttpSession session = request.getSession();
		
		JSONObject ret;
		try {
			ret = Operation.getSpecifiedPeriodEnvDatas(userToken, deviceName, datetime_from, datetime_until, session);
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
		
		out.println("<p>deviceName:\"" + deviceName + "\"</p>");
		out.println("<p>ContentType:\"" + request.getContentType() + "\"</p>");
		out.println("<p>ContentLength:" + request.getContentLength() + "</p>");
		out.println("<p>userToken:" + userToken + "</p>");
	
		
		out.println("<p>");
		out.println("getServletPath:");
		out.println(request.getServletPath());
		out.println("</p>");
		
		out.println("</body>");
		out.println("</html>");
		*/
	}

}
