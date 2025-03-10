package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MyHelper;
import model.Operation;

import java.io.OutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class EnvDataUploadMultiServlet
 */
@WebServlet("/envdata-upload-multi/*")
public class EnvDataUploadMultiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String deviceName = MyHelper.getDeviceNameFromURI(request.getRequestURI(), "/envdata-upload-multi/");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		
		String token = jsonobj.optString("token");
		JSONArray data_array = jsonobj.optJSONArray("data_array", null);
		if (data_array == null) {
			throw new ServletException("no data");
		}
		
		try {
			JSONObject ret = Operation.registNewEnvDataArray(deviceName, token, data_array);
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
