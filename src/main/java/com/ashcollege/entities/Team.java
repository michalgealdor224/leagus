package com.ashcollege.entities;


import javax.persistence.*;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private int skillLevel;

    public Team() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Team(String name, int skillLevel) {
        this.name = name;
        this.skillLevel = skillLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
