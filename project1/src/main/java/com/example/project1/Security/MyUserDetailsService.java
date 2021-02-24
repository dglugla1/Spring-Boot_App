package com.example.project1.Security;

import com.example.project1.Person;
import com.example.project1.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Person person = null;
        for(Person prsn: personRepository.findAll()){

            if(prsn.getUsername().equals(s)){
                person = prsn;
            }
        }
        if (person == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        return new MyUserDetails(person);
    }
}