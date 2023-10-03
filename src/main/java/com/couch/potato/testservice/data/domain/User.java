package com.couch.potato.testservice.data.domain;

import com.couch.potato.testservice.data.DatabaseConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@Data
@Entity
@Table(name = DatabaseConstants.USER_TABLE, uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {

    private static final String GENERATOR = DatabaseConstants.USER_TABLE + DatabaseConstants.DEF_GENERATOR_POSTFIX;
    private static final String ID_SEQ = DatabaseConstants.USER_TABLE + DatabaseConstants.ID_PART + SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = ID_SEQ, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @ToString.Exclude
    @Column(name = "first_name")
    private String firstName;

    @ToString.Exclude
    @Column(name = "last_name")
    private String lastName;

    @ToString.Exclude
    @Column(name = "active", nullable = false)
    private boolean active;

    @ToString.Exclude
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @Version
    @ToString.Exclude
    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;

    @PrePersist
    private void onCreate() {
        created = LocalDateTime.now();
    }
}
