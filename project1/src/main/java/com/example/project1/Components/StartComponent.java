package com.example.project1.Components;

import com.example.project1.App;
import com.example.project1.Connection;
import com.example.project1.Person;
import com.example.project1.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class StartComponent {

    @Autowired
    public ApplicationService applicationService;


    @PostConstruct
    public void comp() throws IOException {
        String root = System.getProperty("user.dir");
        String FileName = "Persons.csv";
        String fileInPeople = root + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + FileName;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInPeople));
        String line = null;

        List<String[]> valuesPerson = new ArrayList<>();

        while ((line = bufferedReader.readLine()) != null) {
            String[] tempP = line.split(",");
            valuesPerson.add(tempP);
        }
        bufferedReader.close();

        valuesPerson.remove(0);

        for (String[] s : valuesPerson) {
            Person person = new Person();
            person.setId(Integer.parseInt(s[0]));
            person.setName(s[1]);
            person.setSurname(s[2]);
            person.setEmail(s[3]);
            person.setCountry(s[4]);
            person.setPassword(s[5]);
            person.setUsername(s[6]);
            applicationService.personRepository.save(person);
        }

        String FileName2="Domains.csv";
        String fileInApps = root+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+FileName2;
        BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileInApps));
        String line2 = null;

        List<String[]> valuesApp = new ArrayList<>();

        while ((line2 = bufferedReader2.readLine()) != null) {
            String[] tempA = line2.split(",");
            valuesApp.add(tempA);
        }
        bufferedReader.close();

        valuesApp.remove(0);

        for(String[] s : valuesApp){
            App app = new App();
            app.setId(Integer.parseInt(s[0]));
            app.setName(s[1]);
            app.setDomain(s[2]);
            applicationService.appRepository.save(app);
        }

        int numberOfPeople = 0;
        int numberOfApps = 0;

        for(App a: applicationService.appRepository.findAll()){
            numberOfApps++;
        }
        for(Person p: applicationService.personRepository.findAll()){
            numberOfPeople++;
        }

        for(int i=1;i<numberOfApps;i++){
            Random random = new Random();
            int n = random.nextInt((10-1) - 0) + 0;
            for(int j=1; j<n; j++){
                Connection con = new Connection();
                //appList.get(i).users.add(peopleList.get(j));
                //peopleList.get(j).applications.add(appList.get(i));
                con.setId_application(applicationService.appRepository.findById(i).get());
                con.setId_person(applicationService.personRepository.findById(j).get());
                applicationService.connectionRepository.save(con);
            }
        }
    }

}
