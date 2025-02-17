package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MyHelper;
import model.Operation;
import java.io.IOException;
import java.io.OutputStream;
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
		String deviceName = MyHelper.getDeviceNameFromURI(request.getRequestURI(), "/data-upload/");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);

		String token = jsonobj.optString("token");
		String datetime_str = jsonobj.optString("datetime");
		System.out.println("datetime_str:" + datetime_str);
		Date datetime = MyHelper.getDatefromDateTimeStringWithTZ(jsonobj.optString("datetime"));
		if (datetime == null) {
			throw new ServletException("The datetime value is invalid.");
		}
		Double temperature = jsonobj.optDouble("temperature", Double.NaN);
		int heartbeat = jsonobj.optInt("heartbeat", Integer.MIN_VALUE);
		
		
		try {
			JSONObject ret = Operation.registNewData(deviceName, token, datetime, temperature, heartbeat);
			response.setContentType("application/json; charset=UTF-8");
			
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
	}

}
