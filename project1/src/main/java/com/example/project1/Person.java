package com.example.project1;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name="Person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    @NotNull(message="Wprowadź imię")
    @Size(min = 2, message = "Imię powinno być dłuższe niż 2 znaki")
    @Column(name = "name")
    private String name;

    @NotNull(message="Wprowadź nazwisko")
    @Size(min = 2, message = "Nazwisko powinno być dłuższe niż 2 znaki")
    @Column(name = "surname")
    private String surname;


    @NotNull(message="Wprowadź e-mail")
    @Pattern(regexp = "[a-z0-9]*[@][a-z0-9]*[a-z.-]*",message = "Nieprawidłowy e-mail")
    @Column(name = "email")
    private String email;

    @Column(name = "country")
    private String country;

    @NotNull(message="Wprowadź nazwę")
    @Size(min = 1, message = "Nazwa użytkownika powinna być dłuższa niż 1 znak")
    @Column(name = "username")
    private String username;

    @NotNull(message="Wprowadź hasło")
    @Pattern(regexp = "[A-Za-z0-9]*.{5,}$",message = "Hasło powinno być dłuższe niż 5 znaków, zawierać co najmniej jedną małą i wielką literę oraz cyfrę")
    @Column(name = "password")
    private String password;

    @Transient
    public List<App> applications = new ArrayList<>();

    public Person(String name, String surname, String email, String country, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.country = country;
        this.username = username;
        this.password = password;
    }

    public Person(){}
}
