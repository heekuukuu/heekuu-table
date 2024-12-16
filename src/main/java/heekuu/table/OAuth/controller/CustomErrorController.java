package heekuu.table.OAuth.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (status != null) {
      int statusCode = Integer.parseInt(status.toString());
      model.addAttribute("statusCode", statusCode);
      switch (statusCode) {
        case 404:
          model.addAttribute("errorMessage", "페이지를 찾을 수 없습니다.");
          break;
        case 500:
          model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다.");
          break;
        default:
          model.addAttribute("errorMessage", "예기치 않은 오류가 발생했습니다.");
          break;
      }
    }
    return "error";
  }
}
