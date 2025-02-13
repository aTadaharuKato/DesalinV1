package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MyHelper;

import java.io.IOException;
import java.io.PrintWriter;

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
		String deviceName = MyHelper.getDeviceNameFromURI(request.getRequestURI(), "/data-upload/");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);

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
	}
}
