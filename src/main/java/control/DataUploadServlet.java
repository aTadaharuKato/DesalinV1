package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MyHelper;
import model.Operation;
import model.dao.TemperatureDAO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONObject;

/**
 * Servlet implementation class DataUploadServlet
 */
@WebServlet("/data-upload/*")
public class DataUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// URL からデバイス名を取得します.
		// e.g. /DesalinV1/test-servlet/sakura ---> デバイス名 "sakura"
		String requestURI = request.getRequestURI();
		int lastIndex = requestURI.lastIndexOf("/data-upload/");
		String deviceName = requestURI.substring(lastIndex + "/data-upload/".length());
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

		String token = jsonobj.optString("token");
		String datetime_str = jsonobj.optString("datetime");
		System.out.println("datetime_str:" + datetime_str);
		Date datetime;
		try {
			datetime = MyHelper.getDatefromDateTimeStringWithTZ(datetime_str);
		} catch (ParseException e) {
			throw new ServletException("The datetime value is invalid.");
		}
		Double temperature = jsonobj.optDouble("temperature", Double.NaN);
		int heartbeat = jsonobj.optInt("heartbeat", Integer.MIN_VALUE);
		
		
		JSONObject ret;
		try {
			ret = Operation.registNewData(deviceName, token, datetime, temperature, heartbeat);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("こまったちゃん");
		}
		response.setContentType("application/json; charset=UTF-8");
		try (OutputStream os = response.getOutputStream()) {
			os.write(ret.toString().getBytes("UTF-8"));
		}

		/*
		
		// アクセストークンが有効かチェックする.
		int ret = -1;
		try {
			ret = Operation.checkAccessToken(deviceName, token);
		} catch (Exception ignore) {
		}
		if (ret != Operation.SUCCESS_ACCESSTOKEN_CHECK) {
			throw new ServletException("Illegal Access Token. (ret:" + ret + ")");
		}
		
		
		
		
		if (temperature != Double.NaN) {
			try {
				TemperatureDAO.registNewData(deviceName, temperature, datetime);
			} catch (Exception e) {
				throw new ServletException("An attempt to write to the database failed.");
			}
		}
		
		
		
		
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
		
		out.println("<p>requestURL.lastIndexOf(\"/data-upload/\") = " + lastIndex + "</p>");
		out.println("<p>deviceName:\"" + deviceName + "\"</p>");
		out.println("<p>ContentType:\"" + request.getContentType() + "\"</p>");
		out.println("<p>ContentLength:" + request.getContentLength() + "</p>");
		
		out.println("<p>token:" + token + "</p>");
		out.println("<p>datetime:" + datetime + "</p>");
		out.println("<p>temperature:" + temperature + "</p>");
		out.println("<p>heartbeat:" + heartbeat + "</p>");
		
		out.println("<p>");
		out.println("getServletPath:");
		out.println(request.getServletPath());
		out.println("</p>");
		
		out.println("</body>");
		out.println("</html>");
		*/
	}

}
