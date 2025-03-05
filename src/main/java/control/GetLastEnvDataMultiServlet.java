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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class GetLastEnvDataMultiServlet
 */
@WebServlet("/get-last-envdata-multi")
public class GetLastEnvDataMultiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("GetLastEnvDataMultiServlet: (POST)");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		String userToken = jsonobj.optString("token");
		JSONArray devArray = jsonobj.optJSONArray("devices");
		
		String[] strDevArray = new String[devArray.length()];
		for (int i = 0; i < devArray.length(); i++) {
			strDevArray[i] = devArray.getString(i);
			System.out.println("[" + i + "] " + strDevArray[i]);
		}
		
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		JSONObject ret;
		try {
			ret = Operation.getLastEnvDataMulti(userToken, strDevArray);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("こまったちゃん");
		}
		/*
		
		try {
			if (!userToken.isEmpty()) {
				ret = Operation.userTokenLogin(userToken);
			} else if (!userId.isEmpty()) {
				ret = Operation.userLogin(userId, password);
			} else {
				throw new Exception("parameter Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("こまったちゃん");
		}
		*/
		
		if (!ret.optString("result").equals("success")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		try (OutputStream os = response.getOutputStream()) {
			os.write(ret.toString().getBytes("UTF-8"));
		}
	}

}
