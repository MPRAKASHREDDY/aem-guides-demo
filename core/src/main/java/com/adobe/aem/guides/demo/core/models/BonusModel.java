package com.adobe.aem.guides.demo.core.models;

import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.guides.demo.core.services.EmployeeReportService;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BonusModel {


      @ValueMapValue
    private String name;

    @ValueMapValue
    private Integer age;

    @ValueMapValue
    private double salary;

     @OSGiService
    private EmployeeReportService employeeReportService;

    private double bonus;

    @PostConstruct
    protected void init() {
        if (employeeReportService != null) {
            bonus = employeeReportService.getEmployeeBonus(salary !=0  ? salary : 0);
        }
    }

    public double getBonus() {
        return bonus;
    }

     public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public double getSalary() {
        return salary;
    }

  


}
