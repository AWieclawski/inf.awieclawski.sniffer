package inf.awieclawski.sniffer.schdlrs;

import inf.awieclawski.sniffer.utls.CronCheck;
import org.springframework.scheduling.support.CronTrigger;


public class TriggerHolder {

    private CronTrigger triggerStandard;

    public TriggerHolder(CronTrigger trigger) {
        this.triggerStandard = trigger;
    }

    public void setStandardTrigger(String cronExpression) {
        CronCheck.checkCron(cronExpression);
        this.triggerStandard = new CronTrigger(cronExpression);
    }

    public CronTrigger getStandardTrigger() {
        return this.triggerStandard;
    }
}
