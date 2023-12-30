package vincentcorp.vshop.Authenticator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserPasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String reNewPassword;
}
