package com.octal.fsm.entities;

import com.octal.fsm.entities.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
 @Table(
        name = "technicians",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"})
)
public class Technician extends AbstractPersistable{
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String mobileNumber;

    @Column(name = "employee_id")
    private String employeeId;


    @Lob
    @Column(name="profile_picture")
    private String profilePicture;

    //@ManyToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "address_id")
    @Column(name="address")
    private String address;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Lob
    @Column(name = "jwt_token")
    private String token;

    @Column(name = "available")
    private boolean available;


}

