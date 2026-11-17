package com.adobe.aem.guides.demo.core.services;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.metatype.annotations.Designate;

@Designate(ocd = BonusConfig.class)
@Component(service = BonusService.class)
public class BonusServiceImpl implements BonusService {

   private static final Logger LOG =
            LoggerFactory.getLogger(BonusServiceImpl.class);

    private double bonusPercentage = 10;

  @Activate
protected void activate(BonusConfig config) {
    bonusPercentage = config.bonusPercentage();
    LOG.info("Bonus Service Activated. Percentage: {}%", bonusPercentage);
}

@Modified
protected void modified(BonusConfig config) {
    bonusPercentage = config.bonusPercentage();
    LOG.info("Bonus Service Configuration Modified. Percentage: {}%", bonusPercentage);
}

@Deactivate
protected void deactivate() {
    LOG.info("Bonus Service Deactivated");
}

    @Override
    public double calculateBonus(double salary) {
        double bonus = salary * bonusPercentage / 100;
        LOG.info("Bonus calculated for salary: {}", salary);
        return bonus;
    }

}
