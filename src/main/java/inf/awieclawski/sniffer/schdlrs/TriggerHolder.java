package inf.awieclawski.sniffer.schdlrs;

import inf.awieclawski.sniffer.utls.CronCheck;
import org.springframework.scheduling.support.CronTrigger;


public class TriggerHolder {

    private CronTrigger triggerObject;

    public TriggerHolder(CronTrigger trigger) {
        this.triggerObject = trigger;
    }

    public void setCroneTrigger(String cronExpression) {
        CronCheck.checkCron(cronExpression);
        this.triggerObject = new CronTrigger(cronExpression);
    }

    public CronTrigger getTrigger() {
        return this.triggerObject;
    }
}
