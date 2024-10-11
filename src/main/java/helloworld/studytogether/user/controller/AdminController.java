package helloworld.studytogether.user.controller;

import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


  @RestController
  @RequestMapping("/admin")
  public class AdminController {

    @Autowired
    private AdminService adminService;

    // 관리자 정보 수정
    @PutMapping("/admin/{adminId}")
    public User updateAdmin(@PathVariable Long adminId,
        @RequestParam(required = false) String newPassword,
        @RequestParam(required = false) String newNickname) {
      return adminService.updateAdmin(adminId, newPassword, newNickname);
    }

    // 관리자 삭제
    @DeleteMapping("/admin/{adminId}")
    public String deleteAdmin(@PathVariable Long adminId) {
      adminService.deleteAdmin(adminId);
      return "관리자가 삭제되었습니다.";
    }
  }
