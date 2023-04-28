package vincentcorp.vshop.Authenticator.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameExistResponse {
   private boolean exist; 
}
