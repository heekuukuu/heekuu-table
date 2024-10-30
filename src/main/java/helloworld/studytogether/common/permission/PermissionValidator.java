package helloworld.studytogether.common.permission;

import helloworld.studytogether.common.util.SecurityUtil;
import helloworld.studytogether.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * 삭제 수행시, 사용자의 아이디, 권한(USER, ADMIN)을 확인하는 클래스
 */
@Component
@RequiredArgsConstructor
public class PermissionValidator {
  private final SecurityUtil securityUtil;

  public void validateDeletePermission(OwnedResource entity) {
    Long currentUserId = securityUtil.getCurrentUserId();
    Role currentUserRole = Role.valueOf(securityUtil.getCurrentUserRole());

    boolean isOwner = entity.getOwnerId().equals(currentUserId);
    boolean isAdmin = currentUserRole == Role.ADMIN;

    if (!isOwner && !isAdmin) {
      throw new AccessDeniedException("삭제 권한이 없습니다");
    }
  }
}
