<%@ page contentType="text/html; charset=UTF-8" %>
<meta charset="UTF-8">

<!-- /WEB-INF/views/error.jsp -->
<!DOCTYPE html>
<html>
<head>
    <title>Error - 페이지 오류</title>
</head>
<body>
    <h2>에러가 발생했습니다.</h2>
    <p>요청하신 페이지를 찾을 수 없습니다.</p>

    <!-- 추가 오류 정보 -->
    <hr>
    <p><strong>오류 코드:</strong> ${statusCode}
        <c:choose>
            <c:when test="${statusCode == 400}">- 잘못된 요청입니다.<br></c:when>
            <c:when test="${statusCode == 401}">- 인증이 필요합니다.<br></c:when>
            <c:when test="${statusCode == 403}">- 접근이 거부되었습니다.<br></c:when>
            <c:when test="${statusCode == 404}">- 페이지를 찾을 수 없습니다.<br></c:when>
            <c:when test="${statusCode == 500}">- 서버에 오류가 발생했습니다.<br></c:when>
            <c:otherwise>- 알 수 없는 오류입니다.<br></c:otherwise>
        </c:choose>
    </p>
    <p><strong>오류 메시지:</strong> ${message}</p>
    <p><strong>예외 타입:</strong> ${exception}</p>
    <p><strong>요청된 URI:</strong> ${requestURI}</p>
</body>
</html>