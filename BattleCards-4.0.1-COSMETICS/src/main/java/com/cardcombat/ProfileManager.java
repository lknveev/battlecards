package com.cardcombat;

import java.io.File;import java.util.*;import org.bukkit.*;import org.bukkit.configuration.*;import org.bukkit.configuration.file.YamlConfiguration;import org.bukkit.entity.Player;

public final class ProfileManager {
 private final CardCombatPlugin plugin; private final File file;
 private final Map<UUID,Integer>wins=new HashMap<UUID,Integer>(),losses=new HashMap<UUID,Integer>(),shards=new HashMap<UUID,Integer>();
 private final Map<UUID,String>classes=new HashMap<UUID,String>(),titles=new HashMap<UUID,String>(),cosmetics=new HashMap<UUID,String>();
 public ProfileManager(CardCombatPlugin p){plugin=p;file=new File(p.getDataFolder(),"profiles.yml");}
 public void load(){wins.clear();losses.clear();shards.clear();classes.clear();titles.clear();cosmetics.clear();if(!file.exists())return;YamlConfiguration y=YamlConfiguration.loadConfiguration(file);ConfigurationSection s=y.getConfigurationSection("players");if(s==null)return;for(String k:s.getKeys(false))try{UUID u=UUID.fromString(k);wins.put(u,s.getInt(k+".wins"));losses.put(u,s.getInt(k+".losses"));shards.put(u,s.getInt(k+".shards"));classes.put(u,s.getString(k+".class",""));titles.put(u,s.getString(k+".title","Novice"));cosmetics.put(u,s.getString(k+".cosmetic","none"));}catch(Exception ignored){}}
 private void save(){YamlConfiguration y=new YamlConfiguration();Set<UUID> ids=new HashSet<UUID>();ids.addAll(wins.keySet());ids.addAll(losses.keySet());ids.addAll(shards.keySet());ids.addAll(classes.keySet());ids.addAll(titles.keySet());ids.addAll(cosmetics.keySet());for(UUID u:ids){String b="players."+u;y.set(b+".wins",getWins(u));y.set(b+".losses",getLosses(u));y.set(b+".shards",getShards(u));y.set(b+".class",getClass(u));y.set(b+".title",getTitle(u));y.set(b+".cosmetic",getCosmetic(u));}try{if(!plugin.getDataFolder().exists())plugin.getDataFolder().mkdirs();y.save(file);}catch(Exception e){plugin.getLogger().warning("Could not save profiles.yml: "+e.getMessage());}}
 public int getWins(Player p){return getWins(p.getUniqueId());}public int getWins(UUID u){return val(wins,u);}public int getLosses(Player p){return getLosses(p.getUniqueId());}public int getLosses(UUID u){return val(losses,u);}public int getShards(Player p){return getShards(p.getUniqueId());}public int getShards(UUID u){return val(shards,u);}private int val(Map<UUID,Integer>m,UUID u){Integer n=m.get(u);return n==null?0:n;}
 public String getClass(Player p){return getClass(p.getUniqueId());}public String getClass(UUID u){String s=classes.get(u);return s==null?"":s;}public boolean hasClass(Player p){return !getClass(p).isEmpty();}
 public boolean setClass(Player p,String c){if(hasClass(p))return false;classes.put(p.getUniqueId(),c);save();plugin.economy().ensureStarterCards(p,c);return true;}public void resetClass(Player p){classes.remove(p.getUniqueId());save();}
 public String getTitle(Player p){String s=titles.get(p.getUniqueId());return s==null?"Novice":s;}public String getCosmetic(Player p){String s=cosmetics.get(p.getUniqueId());return s==null?"none":s;}
 public void setTitle(Player p,String s){titles.put(p.getUniqueId(),s);save();}public void setCosmetic(Player p,String s){cosmetics.put(p.getUniqueId(),s);save();}
 public void win(Player p){wins.put(p.getUniqueId(),getWins(p)+1);shards.put(p.getUniqueId(),getShards(p)+plugin.getConfig().getInt("settings.rewards.shards-per-win",10));save();}
 public void loss(Player p){losses.put(p.getUniqueId(),getLosses(p)+1);save();}
 public List<String> unlockedTitles(int w){return thresholdTitles(w);}private List<String> thresholdTitles(int w){List<String>r=new ArrayList<String>();r.add("Novice");String[]a={"25:Duelist","75:Card Adept","150:Spellblade","300:Arena Champion","600:Legend of Cards","1200:Grandmaster of the Arcane","2500:Eternal Cardlord"};for(String x:a){String[]q=x.split(":",2);if(w>=Integer.parseInt(q[0]))r.add(q[1]);}return r;}
 public List<String> unlockedCosmetics(int w){List<String>r=new ArrayList<String>();r.add("none");String[]a={"25:spark","50:ember","75:flame","100:frost","150:portal","200:aqua","300:hearts","400:leaf","600:void","800:blood","1000:electric","1200:cosmic","1500:snow","1800:ash","2000:rainbow","2500:storm","3000:orbit","4000:eclipse","5000:runes","7500:inferno","10000:galaxy"};for(String x:a){String[]q=x.split(":",2);if(w>=Integer.parseInt(q[0]))r.add(q[1]);}return r;}
 public void applyUnlocks(Player p){if(!unlockedTitles(getWins(p)).contains(getTitle(p)))setTitle(p,"Novice");if(!unlockedCosmetics(getWins(p)).contains(getCosmetic(p)))setCosmetic(p,"none");}
 public void cosmeticTick(Player p){String c=getCosmetic(p);if(c.equals("none")&&!plugin.isStaff(p))return;Location l=p.getLocation().add(0,1,0);World w=l.getWorld();
  if(c.equals("spark"))w.spawnParticle(Particle.FIREWORKS_SPARK,l,3,.2,.4,.2,.01);
  else if(c.equals("ember"))w.spawnParticle(Particle.FLAME,l,5,.25,.5,.25,.01);
  else if(c.equals("flame")){w.spawnParticle(Particle.FLAME,l,7,.25,.6,.25,.015);w.spawnParticle(Particle.SMOKE_NORMAL,l,2,.2,.4,.2,.01);}
  else if(c.equals("frost"))w.spawnParticle(Particle.SNOW_SHOVEL,l,7,.3,.6,.3,.02);
  else if(c.equals("portal"))w.spawnParticle(Particle.PORTAL,l,5,.2,.5,.2,.1);
  else if(c.equals("aqua")){w.spawnParticle(Particle.WATER_SPLASH,l,6,.3,.6,.3,.02);w.spawnParticle(Particle.WATER_BUBBLE,l,4,.25,.5,.25,.01);}
  else if(c.equals("hearts"))w.spawnParticle(Particle.HEART,l,1,.2,.4,.2,0);
  else if(c.equals("leaf"))w.spawnParticle(Particle.VILLAGER_HAPPY,l,5,.3,.6,.3,.01);
  else if(c.equals("void"))w.spawnParticle(Particle.SPELL_WITCH,l,8,.35,.7,.35,.03);
  else if(c.equals("blood"))w.spawnParticle(Particle.REDSTONE,l,8,.3,.7,.3,.01);
  else if(c.equals("electric")){w.spawnParticle(Particle.CRIT_MAGIC,l,8,.35,.7,.35,.06);w.spawnParticle(Particle.FIREWORKS_SPARK,l,4,.3,.6,.3,.04);}
  else if(c.equals("cosmic")){w.spawnParticle(Particle.PORTAL,l,12,.5,.9,.5,.15);w.spawnParticle(Particle.ENCHANTMENT_TABLE,l,8,.5,.9,.5,.08);}
  else if(c.equals("snow")){w.spawnParticle(Particle.SNOWBALL,l,4,.35,.7,.35,.03);w.spawnParticle(Particle.SNOW_SHOVEL,l,4,.35,.7,.35,.01);}
  else if(c.equals("ash")){w.spawnParticle(Particle.SMOKE_LARGE,l,7,.35,.8,.35,.01);w.spawnParticle(Particle.SMOKE_NORMAL,l,5,.3,.6,.3,.01);}
  else if(c.equals("rainbow")){w.spawnParticle(Particle.FIREWORKS_SPARK,l,12,.45,.8,.45,.04);w.spawnParticle(Particle.CRIT_MAGIC,l,5,.35,.6,.35,.05);}
  else if(c.equals("storm")){w.spawnParticle(Particle.CRIT_MAGIC,l,10,.5,.8,.5,.08);w.spawnParticle(Particle.FIREWORKS_SPARK,l,8,.5,.8,.5,.06);}
  else if(c.equals("orbit")){double t=(System.currentTimeMillis()%3000)/3000.0*Math.PI*2;for(int i=0;i<3;i++){double a=t+i*Math.PI*2/3;Location q=l.clone().add(Math.cos(a)*.7,.25+Math.sin(a)*.35,Math.sin(a)*.7);w.spawnParticle(Particle.END_ROD,q,1,0,0,0,0);}}
  else if(c.equals("eclipse")){w.spawnParticle(Particle.SMOKE_NORMAL,l,5,.4,.8,.4,.01);w.spawnParticle(Particle.SPELL_WITCH,l,5,.4,.8,.4,.03);}
  else if(c.equals("runes")){for(int i=0;i<4;i++){double a=(System.currentTimeMillis()%4000)/4000.0*Math.PI*2+i*Math.PI/2;Location q=l.clone().add(Math.cos(a)*.65,.35,Math.sin(a)*.65);w.spawnParticle(Particle.ENCHANTMENT_TABLE,q,1,0,0,0,.01);}}
  else if(c.equals("inferno")){w.spawnParticle(Particle.FLAME,l,12,.45,.9,.45,.02);w.spawnParticle(Particle.LAVA,l,2,.3,.6,.3,0);}
  else if(c.equals("galaxy")){w.spawnParticle(Particle.PORTAL,l,14,.6,1,.6,.18);w.spawnParticle(Particle.END_ROD,l,5,.45,.8,.45,.02);w.spawnParticle(Particle.ENCHANTMENT_TABLE,l,8,.5,.9,.5,.08);}
 }
}
