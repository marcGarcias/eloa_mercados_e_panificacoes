package garcias.api.content.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringSiteContentJpaRepository extends JpaRepository<SiteContentJpaEntity, Long> {
}
