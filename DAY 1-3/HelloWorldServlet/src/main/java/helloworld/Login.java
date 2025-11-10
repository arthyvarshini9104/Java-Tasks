package helloworld;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String VALID_USERNAME = "admin";
    
    // The bcrypt hashed password (no plain text password in source)
    private static final String HASHED_PASSWORD = "$2a$12$eXampleHashedPasswordFromGeneratorTool";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (VALID_USERNAME.equals(username) && BCrypt.checkpw(password, HASHED_PASSWORD)) {
            response.sendRedirect("home.jsp");
        } else {
            response.sendRedirect("error.jsp");
        }
    }
}
