package com.example.cssandjavascript1.user.userinfo;

import com.example.cssandjavascript1.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "userinfo")
public class UserInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonBackReference(value = "userinfo")
    @OneToOne(mappedBy = "userInfo",cascade = CascadeType.ALL)
    private User user;

    public User user() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "date_of_reg")
    private String date;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "address_line1")
    private String address_line1;

    @Column(name = "address_line2")
    private String address_line2;

    @Column(name = "country")
    private String country;

}
