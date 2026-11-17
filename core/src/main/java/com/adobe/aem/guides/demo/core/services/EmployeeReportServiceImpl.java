package com.adobe.aem.guides.demo.core.services;

import org.slf4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

@Component(service = EmployeeReportService.class)
public class EmployeeReportServiceImpl implements EmployeeReportService {

     private static final Logger LOG =
            LoggerFactory.getLogger(EmployeeReportServiceImpl.class);

    @Reference
    private BonusService bonusService;


    @Override
    public double getEmployeeBonus(double salary) {

         double bonus = bonusService.calculateBonus(salary);
        LOG.info("Employee bonus calculated: {}", bonus);
        return bonus;
    }

}
