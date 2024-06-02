package com.ashcollege;


import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;




    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }


    public void saveTeams(Team[] teams) {
        Session session = getQuerySession();
        List<Team> existingTeams = session.createQuery("SELECT t FROM Team t").list();
        for (Team team : teams) {
            save(team);
        }
    }




//    public User getUserById(int id) {
//        User user = (User)this.sessionFactory.getCurrentSession().createQuery("FROM User WHERE id =: id")
//                .setParameter("id",id);
//        return user;
//    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }
//
//    public <T> List<T> loadList(Class<T> clazz) {
//        return  this.sessionFactory.getCurrentSession().createQuery("FROM Client").list();
//    }

}