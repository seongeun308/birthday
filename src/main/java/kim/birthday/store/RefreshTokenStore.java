package kim.birthday.store;

import kim.birthday.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenStore extends CrudRepository<RefreshToken, Long> {
}
