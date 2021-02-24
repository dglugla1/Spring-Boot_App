package com.example.project1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Connection")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE )
    private Person id_person;

    @ManyToOne
    @JoinColumn(name = "id_application", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE )
    private App id_application;

}
