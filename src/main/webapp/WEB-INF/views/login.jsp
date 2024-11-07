<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>소셜 로그인</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .login-container {
            max-width: 400px;
            margin: auto;
            padding: 50px 20px;
            border: 1px solid #dee2e6;
            border-radius: 10px;
            background-color: white;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .social-btn {
            margin-top: 10px;
        }
        .social-btn .btn {
            width: 100%;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2 class="text-center">소셜 로그인</h2>
        <div class="text-center">
            <a href="/oauth2/authorization/google" class="btn btn-danger social-btn">
                <i class="fab fa-google"></i> Google로 로그인
            </a>
            <a href="/oauth2/authorization/kakao" class="btn btn-warning social-btn">
                <i class="fab fa-kakao"></i> Kakao로 로그인
            </a>
            <a href="/oauth2/authorization/naver" class="btn btn-success social-btn">
                <i class="fab fa-naver"></i> Naver로 로그인
            </a>
        </div>
        <p class="text-center mt-3">아직 계정이 없으신가요? <a href="/signup">회원가입</a></p>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>