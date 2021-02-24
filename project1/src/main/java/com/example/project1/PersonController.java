package com.example.project1;

import com.example.project1.Repository.AppRepository;
import com.example.project1.Repository.PersonRepository;
import com.example.project1.Security.MyUserDetails;
import com.example.project1.Service.ApplicationService;
import com.example.project1.Service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



@Slf4j
@Controller
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public AppRepository appRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EmailConfig emailCfg;

    @Autowired
    public EmailService emailService;

    @GetMapping({"/","/index"})
    public String index(Model model){
        Map<Integer, Integer> graphData = new LinkedHashMap<Integer, Integer>();
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        Integer numberOfPeople = 0;
        for(App a : applicationService.appRepository.findAll()) {
            numberOfPeople = 0;
            for (Connection c : listOfConnections) {
                if (c.getId_application().getId().equals(a.getId())) {
                    numberOfPeople++;
                }
            }
            graphData.put(a.getId(), numberOfPeople);
            a.setNumberOfUsers(numberOfPeople);
        }
        //model.addAttribute("data", graphData);
        model.addAttribute("apps", appRepository.findAll());
        return "index";
    }


    @GetMapping("/admin/peopleList")
    public String getPerson(Model model){
        model.addAttribute("people", personRepository.findAll());
        return "peopleList";
    }

    @GetMapping("/user/myAccount")
    public String getMyData(Model model, HttpServletRequest request){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
            }
        }
        //int id = person.getId();
        //System.out.println(id);
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        List<App> listOfApplications = new LinkedList<App>();
        for(Connection c : listOfConnections){
            if(c.getId_person().getId().equals(person.getId())){
                listOfApplications.add(applicationService.appRepository.findById(c.getId_application().getId()).get());
            }
        }
        model.addAttribute("applications",listOfApplications);
        model.addAttribute("person", person);
        return "myAccount";
    }


    @GetMapping("/admin/addPerson")
    public String showAddForm(Model model){
        Person p= new Person();
        model.addAttribute("newPerson",new Person());
        return "addPerson";
    }

    @PostMapping("/admin/addPerson")
    public String addPerson(@Valid @ModelAttribute("newPerson") Person p, Errors errors){
        for(Person per : applicationService.personRepository.findAll()) {
            if(per.getUsername().equals(p.getUsername()) && !(per.getId()).equals(p.getId())){
                return "errorUserExists";
            }
        }
        if(errors.hasErrors()){
            return "addPerson";
        }
        else {
            applicationService.addPerson(p);
            return "redirect:/admin/peopleList";
        }
    }

    @GetMapping("/registerUser")
    public String showRegisterForm(Model model){
        Person p= new Person();
        model.addAttribute("newPerson",new Person());
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String registerPerson(@Valid @ModelAttribute("newPerson") Person p, Errors errors, BindingResult bindingResult){
        for(Person per : applicationService.personRepository.findAll()) {
            if(per.getUsername().equals(p.getUsername()) && !(per.getId()).equals(p.getId())){
                return "errorUserExists";
            }
        }
        if(errors.hasErrors()){
            return "registerUser";
        }
        else {
            applicationService.addPerson(p);

            String to = p.getEmail();
            String from = "Application.com <auto-confirm@application.com>";
            String subject = p.getName() + ", welcome to Application!";
            String body = p.getName() + ", welcome to Application!\r\n" + "\r\n"
                    + "Log in and check our applications.\r\n" + "\r\n";
            emailService.sendSimpleMessage(to, from, subject, body);

            return "redirect:/index";
        }
    }

    @GetMapping("/admin/deletePerson/{id}")
    public String deletePersonForm(@PathVariable Integer id, Model model){
        model.addAttribute("id", id);
        return "showDeletePerson";

    }

    @PostMapping("/admin/deletePerson/{id}")
    public String deletePerson(@PathVariable Integer id){
        applicationService.deletePerson(id);
        return "redirect:/admin/peopleList";
    }

    @GetMapping("user/deletePerson/{id}")
    public String deleteAccountForm(@PathVariable Integer id, Model model){
        model.addAttribute("id", id);
        return "showDeletePersonU";

    }

    @PostMapping("/user/deletePerson/{id}")
    public String deleteAccount(@PathVariable Integer id, HttpServletRequest request){
        request.getSession().invalidate();
        applicationService.deletePerson(id);
        return "redirect:/index";
    }

    @GetMapping("/admin/updatePerson/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model){
        Person p = null;
        p = applicationService.personRepository.findById(id).get();
        model.addAttribute("people", false);
        model.addAttribute("person", p);
        return "updateuser";
    }

    @PostMapping("/admin/updatePerson/{id}")
    public String updatePerson(@PathVariable Integer id, @Valid @ModelAttribute("person") Person p, Errors errors){
        for(Person per : applicationService.personRepository.findAll()) {
            if(per.getUsername().equals(p.getUsername()) && !(per.getId()).equals(id)){
                return "errorUserExists";
            }
        }
        if(errors.hasErrors()){
            return "updateuser";
        }
        else {
            applicationService.updatePerson(p);
            return "redirect:/admin/peopleList";
        }
    }

    @GetMapping("/user/updateData")
    public String showUpdateFormU(Model model, HttpServletRequest request){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
                person.setId(p.getId());
            }
        }
        //model.addAttribute("people", false);
        model.addAttribute("person", person);
        return "editPersData";
    }

    @PostMapping("/user/updateData")
    public String updatePersonU(@Valid @ModelAttribute("person") Person per, Errors errors){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
            }
        }
        for(Person p : applicationService.personRepository.findAll()) {
            if(p.getUsername().equals(per.getUsername()) && !(p.getId()).equals(person.getId())){
                return "errorUserExists";
            }
        }
        if(errors.hasErrors()){
            return "editPersData";
        }
        else {
            /*Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String personName = ((MyUserDetails) user).userName;
            Person person = null;
            for(Person p : applicationService.personRepository.findAll()){
                if(p.getUsername().equals(personName)){
                    person = p;
                }
            }*/
            person.setName(per.getName());
            person.setSurname(per.getSurname());
            person.setEmail(per.getEmail());
            person.setCountry(per.getCountry());
            person.setUsername(per.getUsername());
            person.setPassword(per.getPassword());
            applicationService.updatePerson(person);
            return "redirect:/user/myAccount";
        }

    }

    @GetMapping("/admin/appList")
    public String getApps(Model model){
        Map<Integer, Integer> graphData = new LinkedHashMap<Integer, Integer>();
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        Integer numberOfPeople = 0;
        for(App a : applicationService.appRepository.findAll()) {
            numberOfPeople = 0;
            for (Connection c : listOfConnections) {
                if (c.getId_application().getId().equals(a.getId())) {
                    numberOfPeople++;
                }
            }
            graphData.put(a.getId(), numberOfPeople);
            a.setNumberOfUsers(numberOfPeople);
        }
        model.addAttribute("data", graphData);
        model.addAttribute("apps", appRepository.findAll());
        return "appList";
    }

    @GetMapping("/user/appList")
    public String getAppsU(Model model){
        Map<Integer, Integer> graphData = new LinkedHashMap<Integer, Integer>();
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        Integer numberOfPeople = 0;
        for(App a : applicationService.appRepository.findAll()) {
            numberOfPeople = 0;
            for (Connection c : listOfConnections) {
                if (c.getId_application().getId().equals(a.getId())) {
                    numberOfPeople++;
                }
            }
            graphData.put(a.getId(), numberOfPeople);
            a.setNumberOfUsers(numberOfPeople);
        }


        model.addAttribute("data", graphData);
        model.addAttribute("apps", appRepository.findAll());
        return "appListU";
    }

    @GetMapping("/admin/addApp")
    public String showAddFormApp(Model model){
        App a= new App();
        model.addAttribute("newApp",new App());
        return "addApp";
    }

    @PostMapping("/admin/addApp")
    public String addApp(@Valid @ModelAttribute("newApp") App a, Errors errors){
        if(errors.hasErrors()){
            return "addApp";
        }
        else {
            applicationService.addApp(a);
            return "redirect:/admin/appList";
        }
    }

    @GetMapping("/user/addApp")
    public String showAddFormAppU(Model model, HttpServletRequest request){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
            }
        }
        App a= new App();
        model.addAttribute("newApp",new App());
        model.addAttribute("person", person);
        return "addAppU";
    }

    @PostMapping("/user/addApp")
    public String addAppU(@Valid @ModelAttribute("newApp") App a, HttpServletRequest request, Errors errors){
        if(errors.hasErrors()){
            return "addAppU";
        }
        else {
            applicationService.addApp(a);
            //App app = applicationService.appRepository.findTopByOrderByIdDesc();
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String personName = ((MyUserDetails) user).userName;
            Person person = null;
            for(Person p : applicationService.personRepository.findAll()){
                if(p.getUsername().equals(personName)){
                    person = p;
                }
            }
            Connection connection = new Connection();
            connection.setId_application(applicationService.appRepository.findById(a.getId()).get());
            connection.setId_person(applicationService.personRepository.findById(person.getId()).get());
            applicationService.connectionRepository.save(connection);
            return "redirect:/user/appList";
        }
    }


    @PostMapping("/admin/deleteApp/{id}")
    public String deleteApp(@PathVariable Integer id){
        applicationService.deleteApp(id);
        return "redirect:/admin/appList";
    }

    @GetMapping("/admin/updateApp/{id}")
    public String showUpdateFormApp(@PathVariable int id, Model model){
        App a = null;
        a = applicationService.appRepository.findById(id).get();
        model.addAttribute("apps", false);
        model.addAttribute("app", a);
        return "updateApp";
    }

    @PostMapping("/admin/updateApp/{id}")
    public String updateApp(@PathVariable int id,@Valid @ModelAttribute("app") App a, Errors errors){
        if(errors.hasErrors()){
            return "updateApp";
        }
        else {
            applicationService.updateApp(id, a);
            return "redirect:/admin/appList";
        }
    }

    @GetMapping("/admin/showApps/{id}")
    public String showApps(@PathVariable int id, Model model){
        //int index = applicationService.findIdOfPerson(id);
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        List<App> listOfApplications = new LinkedList<App>();
        for(Connection c : listOfConnections){
            if(c.getId_person().getId().equals(id)){
                listOfApplications.add(applicationService.appRepository.findById(c.getId_application().getId()).get());
            }
        }
        model.addAttribute("applications",listOfApplications);
        model.addAttribute("id",id);
        return "showApps";
    }

    @GetMapping("/admin/showUsers/{id}")
    public String showUsers(@PathVariable int id, Model model){
        //int index = applicationService.findIdOfApplication(id);
        List<Connection> listOfConnections = applicationService.connectionRepository.findAll();
        List<Person> listOfPeople = new LinkedList<Person>();
        for(Connection c : listOfConnections){
            if(c.getId_application().getId().equals(id)){
                listOfPeople.add(applicationService.personRepository.findById(c.getId_person().getId()).get());
            }
        }
        model.addAttribute("users",listOfPeople);
        model.addAttribute("id",id);
        return "showUsers";
    }

    @GetMapping("/admin/showUsers/{id}/addUser")
    public String showAddUserForm(@PathVariable Integer id, Model model){
        List<Person> myUserList = new ArrayList<>();
        List<Person> notMyUserList = new ArrayList<>();
        for(Connection c : applicationService.connectionRepository.findAll()){
            if(c.getId_application().getId().equals(id)){
                myUserList.add(c.getId_person());
            }
            else {
                notMyUserList.add(c.getId_person());
            }
        }

        List<Person> finalUsers = new ArrayList<>();

        for(Person p : applicationService.personRepository.findAll()){
            int exists=0;
            for(Person d: myUserList){
                if(p.equals(d)){
                    exists++;
                }
            }
            if(exists==0){
                finalUsers.add(p);
            }
        }
        model.addAttribute("allUsers", finalUsers);
        //Person p= new Person();
        //model.addAttribute("Person",p);
        return "addUser";
    }

    @PostMapping("/admin/showUsers/{id}/addUser")
    public String addUser(@PathVariable Integer id, @RequestParam int userId, @ModelAttribute("Person") Person user, Model model){
        //Connection connection = new Connection();
        //connection.setId_application(applicationService.appRepository.findById(id).get());
        //connection.setId_person(applicationService.personRepository.findById(user.getId()).get());
        //applicationService.connectionRepository.save(connection);
        //return "redirect:/admin/showUsers/"+id;
        List<Person> myUserList = new ArrayList<>();
        List<Person> notMyUserList = new ArrayList<>();
        for(Connection c : applicationService.connectionRepository.findAll()){
            if(c.getId_application().getId().equals(id)){
                myUserList.add(c.getId_person());
            }
            else {
                notMyUserList.add(c.getId_person());
            }
        }

        List<Person> finalUsers = new ArrayList<>();

        for(Person p : applicationService.personRepository.findAll()){
            int exists=0;
            for(Person d: myUserList){
                if(p.equals(d)){
                    exists++;
                }
            }
            if(exists==0){
                finalUsers.add(p);
            }
        }
        model.addAttribute("allUsers", finalUsers);

        Connection connection = new Connection();
        connection.setId_application(applicationService.appRepository.findById(id).get());
        connection.setId_person(applicationService.personRepository.findById(userId).get());
        applicationService.connectionRepository.save(connection);
        return "redirect:/admin/showUsers/"+id;
    }

    @GetMapping("/user/addMe")
    public String showAddMeForm(Model model,  HttpServletRequest request){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
            }
        }
        List<App> myAppList = new ArrayList<>();
        List<App> notMyAppList = new ArrayList<>();
        for(Connection c : applicationService.connectionRepository.findAll()){
            if(c.getId_person().getId().equals(person.getId())){
                myAppList.add(c.getId_application());
            }
            else {
                notMyAppList.add(c.getId_application());
            }
        }

        List<App> finalApps = new ArrayList<>();

        for(App c : applicationService.appRepository.findAll()){
            int exists=0;
            for(App d: myAppList){
                if(c.equals(d)){
                    exists++;
                }
            }
            if(exists==0){
                finalApps.add(c);
            }
        }
        model.addAttribute("allApps", finalApps);
        return "addMe";
    }

    @PostMapping("/user/addMe")
    public String addMe(Model model, @RequestParam int appId){
        //model.addAttribute("allApps", appRepository.findAll());
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String personName = ((MyUserDetails) user).userName;
        Person person = null;
        for(Person p : applicationService.personRepository.findAll()){
            if(p.getUsername().equals(personName)){
                person = p;
            }
        }

        List<App> myAppList = new ArrayList<>();
        List<App> notMyAppList = new ArrayList<>();
        for(Connection c : applicationService.connectionRepository.findAll()){
            if(c.getId_person().getId().equals(person.getId())){
                myAppList.add(c.getId_application());
            }
            else {
                notMyAppList.add(c.getId_application());
            }
        }

        List<App> finalApps = new ArrayList<>();

        for(App c : applicationService.appRepository.findAll()){
            int exists=0;
            for(App d: myAppList){
                if(c.equals(d)){
                    exists++;
                }
            }
            if(exists==0){
                finalApps.add(c);
            }
        }
        model.addAttribute("allApps", finalApps);

        Connection connection = new Connection();
        connection.setId_application(applicationService.appRepository.findById(appId).get());
        connection.setId_person(applicationService.personRepository.findById(person.getId()).get());
        applicationService.connectionRepository.save(connection);
        return "redirect:/user/appList";
    }

    @GetMapping("/admin/showUsers/deleteUser/{id}/{idU}")
    public String deleteUserForm(Model model,@PathVariable Integer id, @PathVariable Integer idU){
        model.addAttribute("id",id);
        model.addAttribute("idU",idU);
        return "showDeleteUsers";
    }

    @PostMapping("/admin/showUsers/deleteUser/{id}/{idU}")
    public String deleteUserFormPost(@PathVariable Integer id, @PathVariable Integer idU){
        applicationService.appRepository.existsById(id);
        if(!applicationService.appRepository.existsById(id)){
            return "nieMaOsobyOTakimId";
        }
        applicationService.personRepository.existsById(idU);
        if(!applicationService.personRepository.existsById(idU)){
            return "nieMaOsobyOTakimId";
        }
        Connection connection = null;
        for(Connection con : applicationService.connectionRepository.findAll()){
            if(con.getId_application().getId().equals(id) && con.getId_person().getId().equals(idU)){
                connection = con;
            }
        }
        applicationService.connectionRepository.delete(connection);
        return "redirect:/admin/showUsers/"+id;
    }

    @GetMapping("/person/json/{id}")
    @ResponseBody
    public ObjectNode peopleJsn(@PathVariable Integer id){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        int index = applicationService.findIdOfPerson(id);
        if(index == -1){
            node.put("error","there is no person with given id");
        }
        else {
            node.put("id",applicationService.peopleList.get(index).getId());
            node.put("name",applicationService.peopleList.get(index).getName());
            node.put("surname",applicationService.peopleList.get(index).getSurname());
            node.put("email",applicationService.peopleList.get(index).getEmail());
            node.put("country",applicationService.peopleList.get(index).getCountry());
            node.put("username",applicationService.peopleList.get(index).getUsername());
            node.put("password",applicationService.peopleList.get(index).getPassword());
        }
        return node;
    }

    @GetMapping("/app/json/{id}")
    @ResponseBody
    public ObjectNode appsJsn(@PathVariable Integer id){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        int index = applicationService.findIdOfApplication(id);
        if(index == -1){
            node.put("error","there is no person with given id");
        }
        else {
            node.put("id",applicationService.appList.get(index).getId());
            node.put("name",applicationService.appList.get(index).getName());
            node.put("domain",applicationService.appList.get(index).getDomain());
        }
        return node;
    }

    @GetMapping("/admin/users/export")
    public void exportToCSVPeople(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        List<Person> listUsers = applicationService.personRepository.findAll();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"ID", "Name", "Surname", "Email", "Counry", "Username", "Password"};
        String[] nameMapping = {"id", "name", "surname", "email", "country", "username", "password"};

        csvWriter.writeHeader(csvHeader);

        for (Person person : listUsers) {
            csvWriter.write(person, nameMapping);
        }

        csvWriter.close();

    }

    @GetMapping("/admin/apps/export")
    public void exportToCSVApps(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=apps_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        List<App> listApps = applicationService.appRepository.findAll();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"ID", "Name", "Domain"};
        String[] nameMapping = {"id", "name", "domain"};

        csvWriter.writeHeader(csvHeader);

        for (App app : listApps) {
            csvWriter.write(app, nameMapping);
        }

        csvWriter.close();

    }

    @PostMapping("/admin/apps/import")
    public String importToCSVApps(@RequestParam("applicationsFile") MultipartFile apps) {
        if (apps.isEmpty()) {
            return "redirect:/admin/appList";
        } else {
            try (Reader csvReader = new BufferedReader(new InputStreamReader(apps.getInputStream()))) {
                LinkedList<String[]> valuesApp = new LinkedList<>();
                String row;
                while ((row = ((BufferedReader) csvReader).readLine()) != null) {
                    String[] data = row.split(",");
                    valuesApp.add(data);
                }
                LinkedList<String> basicValuesApp = new LinkedList<>();
                for (String s : valuesApp.get(0)) {
                    basicValuesApp.add(s);
                }
                valuesApp.remove(0);
                System.out.println(valuesApp.size());
                for (String[] string : valuesApp) {
                    App app = new App();
                    //app.setId(Integer.parseInt(string[0]));
                    app.setName(string[1]);
                    app.setDomain(string[2]);
                    applicationService.appRepository.save(app);
                    //System.out.println(string[1]);
                    //System.out.println(string[2]);
                }
                //return "redirect:/admin/appList";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "redirect:/admin/appList";
        }
    }

    @PostMapping("/admin/people/import")
    public String importToCSVPeople(@RequestParam("peopleFile") MultipartFile ppl) {
        if (ppl.isEmpty()) {
            return "redirect:/admin/peopleList";
        } else {
            try (Reader csvReader = new BufferedReader(new InputStreamReader(ppl.getInputStream()))) {
                LinkedList<String[]> valuesPeople = new LinkedList<>();
                String row;
                while ((row = ((BufferedReader) csvReader).readLine()) != null) {
                    String[] data = row.split(",");
                    valuesPeople.add(data);
                }
                LinkedList<String> basicValuesPeople = new LinkedList<>();
                for (String s : valuesPeople.get(0)) {
                    basicValuesPeople.add(s);
                }
                valuesPeople.remove(0);
                for (String[] string : valuesPeople) {
                    Person person = new Person();
                    //app.setId(Integer.parseInt(string[0]));
                    person.setName(string[1]);
                    person.setSurname(string[2]);
                    person.setEmail(string[3]);
                    person.setCountry(string[4]);
                    person.setPassword(string[5]);
                    person.setUsername(string[6]);
                    applicationService.personRepository.save(person);
                }
                //return "redirect:/admin/appList";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "redirect:/admin/peopleList";
        }
    }


}
