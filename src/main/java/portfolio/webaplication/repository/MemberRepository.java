package portfolio.webaplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.webaplication.entity.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberEmail(String memberEmail);
}
