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

import org.json.JSONObject;

/**
 * Servlet implementation class GetAccessTokenServlet
 */
@WebServlet("/user-login")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /*
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		
		JSONObject ret = new JSONObject();
		ret.putOpt("token", "2HNWCMS3GJLM27MNP5LQ1DNYCLNX38BH");
		try (OutputStream os = response.getOutputStream()) {
			os.write(ret.toString().getBytes("UTF-8"));
		}
    }
    */
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("UserLoginServlet: (POST)");
		JSONObject jsonobj = MyHelper.getJsonObjectFromRequestBody(request);
		String userId = jsonobj.optString("userId");
		String password = jsonobj.optString("password");
		String userToken = jsonobj.optString("token");
		
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		JSONObject ret;
		
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
		if (!ret.optString("result").equals("success")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		try (OutputStream os = response.getOutputStream()) {
			os.write(ret.toString().getBytes("UTF-8"));
		}
	}
}
