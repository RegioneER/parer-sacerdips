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

package it.eng.dispenser.helper;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.dispenser.exception.RuntimeHelperException;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(GenericHelper.class);

    private static final String MSG_GET_SUCCESSFUL = "Get successful";

    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> void insertEntity(T entity, boolean forceFlush) {
        if (entity != null) {
            logger.debug(String.format("%s %s", "Persisting instance of class", entity.getClass().getSimpleName()));
            entityManager.persist(entity);
            if (forceFlush) {
                entityManager.flush();
            }
        }
    }

    public <T> void removeEntity(T entity, boolean forceFlush) {
        if (entity != null) {
            logger.debug(String.format("%s %s", "Removing instance of class", entity.getClass().getSimpleName()));
            entityManager.remove(entity);
            if (forceFlush) {
                entityManager.flush();
            }
        }
    }

    public <T> T findById(Class<T> entityClass, BigDecimal id) {
        return findById(entityClass, id.longValue());
    }

    public <T> T findById(Class<T> entityClass, Serializable id) {
        logger.debug(
                String.format("%s %s %s %s", "Getting instance of class", entityClass.getSimpleName(), "with id:", id));
        try {
            T instance = entityManager.find(entityClass, id);
            logger.debug(MSG_GET_SUCCESSFUL);
            return instance;
        } catch (RuntimeException re) {
            throw new RuntimeHelperException("Errore nella fine dell'entità", re);
        }
    }

    public <T> T findByIdWithLock(Class<T> entityClass, BigDecimal id) {
        return findByIdWithLock(entityClass, id.longValue());
    }

    public <T> T findByIdWithLock(Class<T> entityClass, Serializable id) {
        logger.debug("Getting instance of class {} with id: {}, with exclusive lock", entityClass.getSimpleName(), id);
        T instance = null;
        try {
            instance = entityManager.find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
            logger.debug(MSG_GET_SUCCESSFUL);
        } catch (LockTimeoutException lte) {
            logger.error(
                    String.format("%s --- Impossibile acquisire il lock", GenericHelper.class.getSimpleName(), lte));
        }
        return instance;
    }

    public <T> T findViewById(Class<T> entityViewClass, Serializable id) {
        logger.debug("Getting instance of class {} with id: {}", entityViewClass.getSimpleName(), id);
        try {
            T instance = entityManager.find(entityViewClass, id);
            logger.debug(MSG_GET_SUCCESSFUL);
            return instance;
        } catch (RuntimeException re) {
            throw new RuntimeHelperException("Errore nella find view", re);
        }
    }

}
