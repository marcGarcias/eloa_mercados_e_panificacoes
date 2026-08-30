package garcias.api.content.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "site_content")
public class SiteContentJpaEntity {

    @Id
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @Version
    private Long version;

    protected SiteContentJpaEntity() {
    }

    public static SiteContentJpaEntity create(Long id, String data) {
        SiteContentJpaEntity entity = new SiteContentJpaEntity();
        entity.id = id;
        entity.data = data;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getVersion() {
        return version;
    }
}
