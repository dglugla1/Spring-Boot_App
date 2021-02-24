package com.example.project1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="App")
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @NotNull(message="Wprowadź nazwę")
    @NotBlank(message="Wprowadź nazwę")
    @Column(name = "name")
    private String name;

    @NotNull(message="Wprowadź domenę")
    @Pattern(regexp="[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", message = "Nieprawidłowy format")
    @Column(name = "domain")
    private String domain;

    @Transient
    public List<Person> users = new ArrayList<>();

    @Transient
    public int numberOfUsers;

}
