<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Login Error</title>
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
        .error-container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(255, 255, 255, 0.2);
            width: 340px;
            border: 1px solid #ddd;
            color: #333;
            text-align: center;
        }
        h2 {
            font-weight: 700;
            margin-bottom: 20px;
            color: #d10000;
            letter-spacing: 1.5px;
        }
        p {
            margin-bottom: 18px;
            font-size: 1rem;
            color: #444;
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
    <div class="error-container">
        <h2>Invalid Credentials</h2>
        <p>Username or password is incorrect.<br>Try logging on back.</p>
        <form action="Login.jsp" method="get">
            <button type="submit">Back to Login</button>
        </form>
    </div>
</body>
</html>
