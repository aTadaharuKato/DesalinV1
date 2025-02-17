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
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class DataUploadMultiServlet
 */
@WebServlet("/data-upload-multi/*")
public class DataUploadMultiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String deviceName = MyHelper.getDeviceNameFromURI(request.getRequestURI(), "/data-upload-multi/");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		
		String token = jsonobj.optString("token");
		JSONArray data_array = jsonobj.optJSONArray("data_array", null);
		if (data_array == null) {
			throw new ServletException("no data");
		}
		
		try {
			JSONObject ret = Operation.registNewDataArray(deviceName, token, data_array);
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
	
		
		out.println("<p>");
		out.println("getServletPath:");
		out.println(request.getServletPath());
		out.println("</p>");
		
		out.println("</body>");
		out.println("</html>");
		*/
	}
}
