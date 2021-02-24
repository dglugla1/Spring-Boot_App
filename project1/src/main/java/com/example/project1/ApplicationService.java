package com.example.project1.Service;



import com.example.project1.App;
import com.example.project1.Person;
import com.example.project1.Repository.AppRepository;
import com.example.project1.Repository.ConnectionRepository;
import com.example.project1.Repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

@Service
@Data
public class ApplicationService {

    @Autowired
    public PersonRepository personRepository;

    @Autowired
    public AppRepository appRepository;

    @Autowired
    public ConnectionRepository connectionRepository;

    public List<Person> peopleList;
    public List<App> appList;

    public ApplicationService() throws IOException{
        this.peopleList = new ArrayList<>();
        this.appList = new ArrayList<>();

        /*String root = System.getProperty("user.dir");
        String FileName="Persons.csv";
        String fileInPeople = root+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+FileName;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileInPeople));
        String line = null;

        List<String[]> valuesPerson = new ArrayList<>();

        while ((line = bufferedReader.readLine()) != null) {
            String[] tempP = line.split(",");
            valuesPerson.add(tempP);
        }
        bufferedReader.close();

        valuesPerson.remove(0);

        for(String[] s : valuesPerson){
            Person person = new Person(s[1],s[2],s[3],s[4],s[5],s[6]);
            //person.setId(Integer.parseInt(s[0]));
            //person.setName(s[1]);
            //person.setSurname(s[2]);
            //person.setEmail(s[3]);
            //person.setCountry(s[4]);
            //person.setPassword(s[5]);
            //person.setUsername(s[6]);
            personRepository.save(person);
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
            //appList.add(app);
        }*/


        for(int i=0;i<appList.size();i++){
            Random random = new Random();
            int n = random.nextInt((peopleList.size()-1) - 0) + 0;
            for(int j=0; j<n; j++){
                appList.get(i).users.add(peopleList.get(j));
                peopleList.get(j).applications.add(appList.get(i));
            }
        }

    }


    //Person-------------------------------------------------------------------------

    public Integer findIdOfPerson(int id){
        int index= -1;
        for(int i=0;i<this.peopleList.size();i++){
            if(this.peopleList.get(i).id.equals(id)){
                index=i;
            }
        }
        return index;
    }

    public Person findByIDPerson(Integer id){
        for (Person p : peopleList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public static int getIndexOfPeople(List<Person> list, Integer id) {
        int pos = 0;

        for(Person p : list) {
            if(id.equals(p.id))
                return pos;
            pos++;
        }
        return -1;
    }

    public boolean checkIfIdExistsPeople(Integer id){
        for (Person p : peopleList) {
            if (p.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addPerson(Person p){
        //p.id=peopleList.size()+1;
        //while(checkIfIdExistsPeople(p.id)){
        //p.id++;
        //}
        //peopleList.add(p);
        personRepository.save(p);

    }

    public void deletePerson(Integer id){
        personRepository.deleteById(id);
    }

    public void updatePerson(Person p){
        //int index = getIndexOfPeople(peopleList,id);
        this.personRepository.save(p);
    }


//App-------------------------------------------------------------------------

    public Integer findIdOfApplication(int id){
        int index= -1;
        for(int i=0;i<this.appList.size();i++){
            if(this.appList.get(i).id.equals(id)){
                index=i;
            }
        }
        return index;
    }


    public static int getIndexOfApps(List<App> list, Integer id) {
        int pos = 0;

        for(App a : list) {
            if(id.equals(a.id))
                return pos;
            pos++;
        }
        return -1;
    }

    public boolean checkIfIdExistsApps(Integer id){
        for (App a : appList) {
            if (a.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public App findByIDApp(Integer id){
        for (App a : appList) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }


    public void addApp(App a){
        //a.id = appList.size()+1;
        //while(checkIfIdExistsApps(a.id)){
        // a.id++;
        //}
        //appList.add(a);
        appRepository.save(a);
    }

    public void addAppU(App a){
        //a.id = appList.size()+1;
        //while(checkIfIdExistsApps(a.id)){
        //   a.id++;
        //}
        //appList.add(a);
        appRepository.save(a);
    }

    public void deleteApp(Integer id){
        appList.removeIf(element -> element.getId().equals(id));
        for(int i=0;i<peopleList.size();i++){
            for(int j=0;j<peopleList.get(i).applications.size();i++){
                if((peopleList.get(i).applications.get(j).id).equals(id))
                    peopleList.get(i).applications.remove(j);
            }
        }
        appRepository.deleteById(id);
    }

    public void updateApp(Integer id, App a){
        //int index = getIndexOfApps(appList,id);
        //appList.get(index).setName(a.getName());
        //appList.get(index).setDomain(a.getDomain());
        this.appRepository.save(a);
    }

    public boolean checkIfUserExists(Integer idAp, Integer idU){
        for(Person p : appList.get(idAp).users){
            if(p.id.equals(idU)){
                return true;
            }
        }
        return false;
    }


}
