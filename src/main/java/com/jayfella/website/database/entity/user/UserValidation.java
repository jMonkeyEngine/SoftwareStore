package com.jayfella.website.database.entity.user;

import com.jayfella.website.core.AccountValidationType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "UserValidation")
public class UserValidation {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false, length = 64)
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private long userId;
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    @Enumerated(EnumType.STRING)
    private AccountValidationType validationType;
    public AccountValidationType getValidationType() { return validationType; }
    public void setValidationType(AccountValidationType validationType) { this.validationType = validationType; }

    // this could be the new email address requested, the new password, or empty (if the user is just validating their account.)
    @Column(nullable = false, length = 257)
    private String value;
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date creationDate;
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public UserValidation() {

    }

    public UserValidation(long userId, AccountValidationType validationType, String value) {
        this.userId = userId;
        this.validationType = validationType;
        this.value = value;
    }

}


