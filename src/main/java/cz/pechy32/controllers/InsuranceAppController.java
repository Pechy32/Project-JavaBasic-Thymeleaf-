package cz.pechy32.controllers;

import cz.pechy32.models.Insurance;
import cz.pechy32.models.Insured;
import cz.pechy32.models.User;
import cz.pechy32.models.*;
import cz.pechy32.services.InsurancesService;
import cz.pechy32.services.InsuredsService;
import cz.pechy32.services.UsersService;
import cz.pechy32.statistics.InsurancesStatistics;
import cz.pechy32.statistics.InsuredsStatistics;
import cz.pechy32.statistics.StatisticGenerator;
import cz.pechy32.statistics.UsersStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping
public class InsuranceAppController {

    @Autowired
    private InsuredsService insuredsService;
    @Autowired
    private InsurancesService insurancesService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private StatisticGenerator statisticGenerator;

    @GetMapping("/insurance-app/about")
    public String renderAbout(){
        return "about";
    }

    @GetMapping("/insurance-app/insureds")
    public String showInsureds(Model model, Principal principal) {
        String role = ""; // Role uživatele
        int insuredId = 0;

        // Získání role uživatele z Principal objektu
        if (principal != null) {
            User user = usersService.selectUserByUsername(principal.getName());
            insuredId = user.getInsuredId();
            Authentication authentication = (Authentication) principal;
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null && !authorities.isEmpty()) {
                role = authorities.iterator().next().getAuthority(); // Předpokládáme, že uživatel může mít pouze jednu roli
            }
        }

        // Rozhodnutí, kterou šablonu vrátit na základě role uživatele

        // pokud je uživatel admin vrátí komplet seznam pojištěnců
        if (role.equals("ROLE_ADMIN")) {
            //pokud data nejsou načtena provede dotaz na databázi
            if (insuredsService.getAllInsureds() == null){
                insuredsService.setAllInsureds();
            }
            List<Insured> allInsureds = insuredsService.getInsuredsByUser(principal); // Metoda, která vrátí seznam všech pojištěnců pokud je přihlášený admin, jinak vrátí pouze pojištění daného uživatele
            model.addAttribute("allInsureds", allInsureds);
            return "insureds";

            //pokud uživatel není admin vrací se šablona "insured-detail"
            //zároveň předáváme šabloně potřebná data
        } else {
            Insured insuredById = insuredsService.selectInsuredById(insuredId);
            List<Insurance> insurancesById = insurancesService.selectInsurancesById(insuredId);
            model.addAttribute("insured", insuredById);
            model.addAttribute("insurancesById", insurancesById);
            model.addAttribute("insurancesService", insurancesService);
            model.addAttribute("insured_id", insuredId);
            return "insured-detail";
        }
    }

    @GetMapping("/insurance-app/insureds/search-insured")
    public String searchInsureds(@RequestParam("insured-search-input") String insuredName, Model model){
        HashSet<Insured> insuredsByName = insuredsService.getInsuredsByName(insuredName);
        model.addAttribute("allInsureds", insuredsByName);
        return "insureds-search";
    }

    @GetMapping("/insurance-app/insureds/add-insured")
    public String renderAddInsuranceForm(Model model){
        model.addAttribute("insuredsDTO", new InsuredsDTO());
        return "add-insured";
    }

    @PostMapping("/insurance-app/insureds/add-insured")
    public String addInsured(@ModelAttribute InsuredsDTO insuredsDTO, Model model) {
        insuredsService.addInsuredToDatabase(insuredsDTO);//přidání pojištěného do databáze
        insuredsService.setAllInsureds(); // po přidání pojištěného obnoví seznam pojištěných z databáze
        List<Insured> allInsureds = insuredsService.getAllInsureds(); // Metoda, která vrátí seznam všech pojištěnců po obnovení
        model.addAttribute("allInsureds", allInsureds);
        return "insureds";
    }

    @GetMapping("insurance-app/insureds/delete-insured")
    public String deleteInsured(@RequestParam("insured_id") int insuredId, Model model){
        insuredsService.deleteInsuredById(insuredId);
        insuredsService.setAllInsureds();//obnovení seznamu pojištěných po smazání
        insurancesService.setInsurancesList();//obnovení seznamu pojištění po smazání
        List<Insured> allInsureds = insuredsService.getAllInsureds();
        model.addAttribute("allInsureds", allInsureds);
        return "insureds";
    }

    @GetMapping("/insurance-app/insureds/detail")
    public String showInsuredDetail(@RequestParam("insured_id") int insuredId, Model model){
        Insured insuredById = insuredsService.selectInsuredById(insuredId);
        List<Insurance> insurancesById = insurancesService.selectInsurancesById(insuredId);
        model.addAttribute("insured", insuredById);
        model.addAttribute("insurancesById", insurancesById);
        model.addAttribute("insurancesService", insurancesService);
        return "insured-detail";
    }

    @GetMapping("/insurance-app/insureds/edit-insured")
    public String renderEditInsuredForm(@RequestParam("insured_id") int insuredId, Model model){
        Insured insuredById = insuredsService.selectInsuredById(insuredId);
        model.addAttribute("insured", insuredById);
        model.addAttribute("insured_id", insuredId);
        return "insured-edit";
    }

    @PostMapping("/insurance-app/insureds/edit-insured")
    public String updateInsured(@RequestParam ("insured_id") int insuredId, @RequestParam("name") String name, @RequestParam("surname") String surname, @RequestParam("email") String email, @RequestParam("phone") String phone, @RequestParam("address.street") String street, @RequestParam("address.houseNumber") String houseNumber, @RequestParam("address.city") String city, @RequestParam("address.postalCode") String postalCode) {
        insuredsService.updateInsured(insuredId, name, surname, email, phone, street, houseNumber, city, postalCode);
        insuredsService.setAllInsureds();//update seznamu pojištěných
        return "redirect:/insurance-app/insureds/detail?insured_id=" + insuredId;
    }
    @GetMapping("/insurance-app/insureds/add-insurance")
    public String renderAddInsuranceForm(@ModelAttribute InsuranceDTO insuranceDTO, @RequestParam("insured_id") int insuredId, Model model){
        model.addAttribute("insuranceDTO", insuranceDTO);
        model.addAttribute("insured_id", insuredId);
        return "add-insurance";
    }

    @PostMapping("/insurance-app/insureds/add-insurance")
    public String addInsurance(@ModelAttribute InsuranceDTO insuranceDTO, @RequestParam("insured_id") int id, Model model){
        model.addAttribute("insured_id", id);
        insurancesService.addInsuranceToDatabase(id, insuranceDTO);//přidání pojištění do databáze
        insurancesService.setInsurancesList();//refresh seznamu pojištění
        return "redirect:/insurance-app/insureds/detail?insured_id=" + id;
    }

    @GetMapping("/insurance-app/insurances")
    public String renderInsurancesMenu(){
        return "insurances";
    }

    @GetMapping("/insurance-app/insurances/{name}")
    public String showInsurancesByName(Model model, @PathVariable("name") String name, Principal principal){

        ArrayList<Insurance> insurancesList = new ArrayList<>();

        //kontrola načtení dat
        if(insurancesService.getInsurancesList() == null)
        {
            insurancesService.setInsurancesList();
        }

        insurancesList = insurancesService.getInsurancesByUser(principal);

        if (!name.equals("all")){
            insurancesList = insurancesService.selectInsurancesByName(name, insurancesList);
        }

        model.addAttribute("name", insurancesService.translateInsuranceName(name));
        model.addAttribute("insurancesList", insurancesList);
        model.addAttribute("insurancesService", insurancesService);
        return "insurances-list";
    }

    @GetMapping("/insurance-app/insureds/edit-insurance")
    public String renderEditInsuranceForm(@RequestParam("insurance_id") int insuranceId, @RequestParam("insuredName") String insuredName, Model model){
        Insurance insurance = insurancesService.selectInsuranceById(insuranceId);
        String name = insurancesService.translateInsuranceName(insurance.getName());
        model.addAttribute("name", name);
        model.addAttribute("insuredName", insuredName);
        model.addAttribute("insurance", insurance);
        model.addAttribute("insurance_id", insuranceId);
        return "insurance-edit";
    }

    @PostMapping("/insurance-app/insureds/edit-insurance")
    public String editInsurance(@RequestParam("insurance_id") int insuranceId,
                                @RequestParam("insured_id") int insuredId,
                                @RequestParam("name") String name,
                                @RequestParam(value = "endDate", required = false) String endDateString,
                                @RequestParam("amount") BigDecimal amount) {
        LocalDate endDate;
        if (endDateString == null) {
            endDate = null;
        } else {
            endDate = LocalDate.parse(endDateString);
        }
        insurancesService.updateInsurance(insuranceId, endDate, amount);
        return "redirect:/insurance-app/insureds/detail?insured_id=" + insuredId;
    }
    @GetMapping("/insurance-app/insureds/delete-insurance")
    public String deleteInsurance(@RequestParam("insurance_id") int insuranceId, @RequestParam("insured_id") int insuredId){
        insurancesService.deleteInsurance(insuranceId);
        insurancesService.setInsurancesList();//refresh seznamu po odstranění
        return "redirect:/insurance-app/insureds/detail?insured_id=" + insuredId;
    }

    @GetMapping("insurance-app/statistics")
    public String renderStatistics(Model model){


        //vytvoření statistik o pojištěných a předání do šablony
        if(insuredsService.getAllInsureds() == null){
            insuredsService.setAllInsureds();
        }
        InsuredsStatistics insuredsStatistics = statisticGenerator.createInsuredStatistics(insuredsService.getAllInsureds());
        model.addAttribute("insuredsStatistics", insuredsStatistics);

        //vytvoření statistik o pojištěních a předání do šablony
        if(insurancesService.getInsurancesList() == null){
            insurancesService.setInsurancesList();
        }
        InsurancesStatistics insurancesStatistics = statisticGenerator.createInsuranceStatistics(insurancesService.getInsurancesList());
        model.addAttribute("insurancesStatistics", insurancesStatistics);

        //vytvoření statistik o uživatelích a předání do šablony
        if(usersService.getAllUsers() == null){
            usersService.setAllUsers();
        }
        UsersStatistics usersStatistics = statisticGenerator.createUsersStatistics(usersService.getAllUsers());
        model.addAttribute("usersStatistics", usersStatistics);

        return "statistics";
    }
}
