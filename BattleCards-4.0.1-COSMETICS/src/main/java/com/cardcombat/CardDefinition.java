package com.cardcombat;

import java.util.*;

public final class CardDefinition {
    public final String id,name,rarity,type,element,cardClass,formName,summonType,structureType;
    public final double cooldown,range,power,aoeRadius,aoeDelay,formManaDrain,domainDamageMultiplier,summonHealth,summonDamage,explosionPower;
    public final int duration,mana;
    public final boolean adminOnly,aoe,explosion;
    public final List<String> description;
    public CardDefinition(String id,String name,String rarity,String type,double cooldown,double range,double power,int duration,int mana,
        List<String> description,boolean adminOnly,String summonType,String structureType,double summonHealth,double summonDamage,
        boolean explosion,double explosionPower,String element,String cardClass,boolean aoe,double aoeRadius,double aoeDelay,
        String formName,double formManaDrain,double domainDamageMultiplier){
        this.id=id;this.name=name;this.rarity=rarity;this.type=type;this.cooldown=cooldown;this.range=range;this.power=power;
        this.duration=duration;this.mana=mana;this.description=description==null?Collections.<String>emptyList():Collections.unmodifiableList(new ArrayList<String>(description));
        this.adminOnly=adminOnly;this.summonType=summonType;this.structureType=structureType;this.summonHealth=summonHealth;this.summonDamage=summonDamage;
        this.explosion=explosion;this.explosionPower=explosionPower;this.element=element==null?"Arcane":element;this.cardClass=cardClass==null?"General":cardClass;
        this.aoe=aoe;this.aoeRadius=aoeRadius;this.aoeDelay=aoeDelay;this.formName=formName==null?"":formName;this.formManaDrain=formManaDrain;
        this.domainDamageMultiplier=domainDamageMultiplier<=0?1.0:domainDamageMultiplier;
    }
}
