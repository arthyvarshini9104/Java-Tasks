<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Registration Page</title>
    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #000000;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: #333333;
        }
        .register-container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(255, 255, 255, 0.2);
            width: 340px;
            border: 1px solid #ddd;
            color: #333;
        }
        h2 {
            text-align: center;
            font-weight: 700;
            margin-bottom: 24px;
            color: #000;
            letter-spacing: 1.5px;
        }
        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            font-size: 0.9rem;
            color: #444;
        }
        input[type="text"], input[type="password"], input[type="number"] {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            margin-bottom: 18px;
            font-size: 1rem;
            transition: border-color 0.3s;
            background: #fff;
            color: #333;
        }
        select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            margin-bottom: 18px;
            font-size: 1rem;
            background: #fff;
            color: #333;
        }
        button {
            width: 100%;
            padding: 12px 0;
            border: none;
            border-radius: 8px;
            background-color: #000000;
            color: white;
            font-weight: 700;
            font-size: 1.1rem;
            cursor: pointer;
            transition: background-color 0.4s;
        }
        button:hover {
            background-color: #333333;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <h2>Register</h2>
        <form action="RegisterServlet" method="post">
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" placeholder="Enter your name" required />

            <label for="gender">Gender:</label>
            <select id="gender" name="gender" required>
                <option value="">Select gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
            </select>

            <label for="age">Age:</label>
            <input type="number" id="age" name="age" min="1" max="120" required />

            <label for="username">Username:</label>
            <input type="text" id="username" name="username" placeholder="Choose a username" required />

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" placeholder="Choose a password" required />

            <label for="role">Role:</label>
            <select id="role" name="role" required>
                <option value="">Select role</option>
                <option value="user">User</option>
                <option value="admin">Admin</option>
            </select>

            <button action="Login.jsp" type="submit">Register</button>
            <div style="text-align: center;">
            	<p>Already have an account?</p>
  				<a href="Login.jsp">Login</a>
			</div>
        </form>
    </div>
</body>
</html>
