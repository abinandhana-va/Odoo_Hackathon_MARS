package com.example.Controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Model.EmployeeModel;

@Controller
@RequestMapping("/employee")
public class EmployeeController {


    //http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee
    @GetMapping
    @ResponseBody
    public String getEmployee(){
        return "Fetching detaiols";
    }

    //http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee/1
    @GetMapping("/{id}")
    @ResponseBody
    public String getAllEmployee(@PathVariable int id){
        return "Fetching All EMployee"+id;
    }

    //http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee/getEmployee?id=1
    @GetMapping("/getEmployee")
    @ResponseBody
    public String getEmployeeByidWithReqBody(@RequestParam int id){
        return "Fetchinng employee by id using request body"+"for this"+id;
    }

    // post http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee/save2?id=1&name=vivek
    @PostMapping("/save2")
    @ResponseBody
    public String registerEmployeee(@RequestParam int id,@RequestParam String name)
    {
        return "Saving employee details id: " +id +" Name :" +name ;
    }

   //post http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee/save1?id=1&name=vivek&salary=100.0&city=namakkal&department=ece

    @PostMapping("/save1")
    @ResponseBody
    public String registerEmployeee(@RequestParam int id,@RequestParam String name,
                        @RequestParam double salary,
                                @RequestParam String city,
                                @RequestParam String department)
            {
        return "Saving employee details id: " +id +" Name :" +name +" salary " +salary +" city " +city +" department " +department;
    }
    @PostMapping("/save3")
    @ResponseBody
    public String registerEmployee(@ModelAttribute EmployeeModel employee){

        return "Saving employee details id: " +employee.getId() +" Name :" +employee.getName()+" salary " +employee.getSalary() +" city " +employee.getCity() +" department " + employee.getDepartment();

    }

    @PostMapping("/save4")
    @ResponseBody
    public String registerEmployeeValid(@Valid @ModelAttribute EmployeeModel employee){

        return "Saving employee details id: " +employee.getId() +" Name :" +employee.getName()+" salary " +employee.getSalary() +" city " +employee.getCity() +" department " + employee.getDepartment();

    }
        //http://localhost:8080/EmployeeManagement-1.0-SNAPSHOT/employee/save5?id=-454&name=jd&salary=100&city=&department=
    @PostMapping("/save5")
    @ResponseBody
    public String registerEmployeeValidBind(@Valid @ModelAttribute EmployeeModel employee, BindingResult result){
        if(result.hasErrors()){
            //instead of for loops we are using field
            return result.getFieldErrors().stream().map(error->error.getField()+" : "+error.getDefaultMessage()).collect(Collectors.joining());
        }
        return "Saving employee details id: " +employee.getId() +" Name :" +employee.getName()+" salary " +employee.getSalary() +" city " +employee.getCity() +" department " + employee.getDepartment();
    }




    @PostMapping("/saveusingReqbody")
    @ResponseBody
    public String registerEmployeeValidBindusingRequestBody(@Valid @RequestBody EmployeeModel employee, BindingResult result){
        if(result.hasErrors()){
            //instead of for loops we are using field
            return result.getFieldErrors().stream().map(error->error.getField()+" : "+error.getDefaultMessage()).collect(Collectors.joining());
        }

        return "Saving employee details id: " +employee.getId() +" Name :" +employee.getName()+" salary " +employee.getSalary() +" city " +employee.getCity() +" department " + employee.getDepartment();

    }
    
    
    @GetMapping("/empForm")
    //without response body it expecting view resolver it is created obj in config
    public String getEmployeeForm(){
        return "emp-form";
    }

    @GetMapping("/success")
    @ResponseBody
    public String getSuccess(){
        return "success ";
    }
}