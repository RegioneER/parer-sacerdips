/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.dispenser.timer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.dispenser.bo.SacerSyncroJob;
import it.eng.dispenser.entity.constraint.ConstDipLogJob;
import it.eng.dispenser.util.Constants;
import it.eng.parer.jboss.timer.common.CronSchedule;

/**
 *
 * @author Moretti_Lu
 *
 */
@Singleton(mappedName = "SacerSyncroTimer")
@LocalBean
@Lock(LockType.READ)
public class SacerSyncroTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(SacerSyncroTimer.class.getName());

    @EJB(mappedName = "java:app/Dispenser-ejb/SacerSyncroTimer")
    private SacerSyncroTimer thisTimer;
    @EJB(mappedName = "java:app/Dispenser-ejb/SacerSyncroJob")
    private SacerSyncroJob sacerJob;

    public SacerSyncroTimer() {
        super(Constants.JobEnum.SACER_SYNCRO.name());
        logger.debug(SacerSyncroTimer.class.getName() + "creato");
    }

    @Override
    @Lock(LockType.WRITE)
    public void startSingleAction(String applicationName) {
        boolean existTimer = false;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
            timerService.createTimer(TIME_DURATION, jobName);
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void startCronScheduled(CronSchedule sched, String applicationName) {
        ScheduleExpression tmpScheduleExpression;
        boolean existTimer = false;

        logger.info("Schedulazione: Ore: " + sched.getHour());
        logger.info("Schedulazione: Minuti: " + sched.getMinute());
        logger.info("Schedulazione: DOW: " + sched.getDayOfWeek());
        logger.info("Schedulazione: DOM: " + sched.getDayOfMonth());
        logger.info("Schedulazione: Mese: " + sched.getMonth());

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
            tmpScheduleExpression = new ScheduleExpression();
            tmpScheduleExpression.hour(sched.getHour());
            tmpScheduleExpression.minute(sched.getMinute());
            tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            tmpScheduleExpression.month(sched.getMonth());

            logger.info("Lancio il timer SacerSyncroTimer...");

            // Ripristinarlo prima di rilasciare !!!!
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));

            // SOLO PER TESTING e per eseguire il job ONE-SHOT
            // timerService.createSingleActionTimer(0, new TimerConfig(jobName, true));

        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void stop(String applicationName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                timer.cancel();
            }
        }
    }

    @Timeout
    public void doJob(Timer timer) throws Exception {
        logger.debug("Sincronizzazione con Sacer - Inizio schedulazione");
        if (timer.getInfo().equals(jobName)) {
            thisTimer.startProcess();
        }
    }

    @Override
    public void startProcess() throws Exception {
        jobHelper.writeAtomicLogJob(jobName, ConstDipLogJob.tiEvento.INIZIO_ESECUZIONE.name());

        try {
            sacerJob.sincronizzazioneConSacer();
        } catch (Exception e) {
            logger.error("Sincronizzazione Sacer - Errore durante l'esecuzione del job ", e);
            jobHelper.writeAtomicLogJob(jobName, ConstDipLogJob.tiEvento.ERRORE.name(),
                    "Errore: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
