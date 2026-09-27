package com.cardcombat;

import java.util.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.entity.Player;

public class CardMenu {
 public static final String LIBRARY_PREFIX=ChatColor.DARK_PURPLE+"BattleCards • Library";
 public static final String PROFILE=ChatColor.DARK_GREEN+"BattleCards • Profile";
 public static final String DOMAINS=ChatColor.DARK_PURPLE+"BattleCards • Domains";
 private final CardCombatPlugin p;
 public CardMenu(CardCombatPlugin p){this.p=p;}
 private ItemStack filler(Material m,String name){ItemStack i=new ItemStack(m);ItemMeta x=i.getItemMeta();x.setDisplayName(name);i.setItemMeta(x);return i;}

 private String category(CardDefinition d){ return d.cardClass; }
 private List<String> categories(Player player){
  return new ArrayList<String>(Arrays.asList("Shadow","Fire","Water","Earth","Wind","Light","Dragon","Ice","Fighting","Mech","Magnitized","Soul"));
 }
 public Inventory createLibrary(Player player){String chosen=p.profiles().getClass(player);return createLibrary(player,chosen==null||chosen.isEmpty()?"Shadow":chosen);}
 public Inventory createLibrary(Player player,String selected){
  List<String> cats=categories(player); if(!cats.contains(selected))selected=cats.get(0);
  Inventory i=Bukkit.createInventory(null,54,LIBRARY_PREFIX+" ["+selected+"]");
  String[] tabNames={"Shadow","Fire","Water","Earth","Wind","Light","Dragon","Ice","Fighting","Mech","Magnitized","Soul"};
  Material[] tabMats={Material.OBSIDIAN,Material.BLAZE_POWDER,Material.WATER_BUCKET,Material.DIRT,Material.FEATHER,Material.GLOWSTONE_DUST,Material.DRAGON_EGG,Material.PACKED_ICE,Material.IRON_INGOT,Material.IRON_BLOCK,Material.IRON_ORE,Material.SOUL_SAND};
  for(int n=0;n<12;n++){
   String cat=tabNames[n]; boolean chosen=p.profiles().hasClass(player);
   String label=(cat.equals(selected)?ChatColor.GREEN+"» ":ChatColor.GRAY)+cat;
   if(chosen && !cat.equalsIgnoreCase(p.profiles().getClass(player))) label=ChatColor.DARK_GRAY+"Locked";
   i.setItem(n,filler(tabMats[n],label));
  }
  i.setItem(43,filler(Material.BARRIER,ChatColor.RED+"Close"));
  i.setItem(44,filler(Material.NETHER_STAR,ChatColor.GOLD+"Profile"));
  i.setItem(35,filler(Material.NETHER_BRICK,ChatColor.LIGHT_PURPLE+"Domains"));
  int slot=12;
  int shown=0;
  for(CardDefinition d:p.cards().all()){
   if(!category(d).equalsIgnoreCase(selected))continue;
   if(d.type.startsWith("DOMAIN_"))continue;
   if(d.adminOnly&&!p.isAdmin(player))continue;
   if(slot>34)break;
   ItemStack card=p.cards().createItem(d.id,1);
   if(card==null)continue;
   if(!p.isAdmin(player) && p.economy()!=null && !p.economy().owns(player,d.id) && p.economy().price(d)>0){
    ItemMeta cm=card.getItemMeta(); List<String> cl=cm.hasLore()?new ArrayList<String>(cm.getLore()):new ArrayList<String>();
    cl.add(ChatColor.RED+"Locked - buy in /shop"); cl.add(ChatColor.GOLD+"Price: "+p.economy().price(d)+" CC"); cm.setLore(cl); card.setItemMeta(cm);
   }
   i.setItem(slot++,card);
   shown++;
  }
  if(shown==0){
   i.setItem(22,filler(Material.BARRIER,ChatColor.RED+"No cards loaded"));
   i.setItem(31,filler(Material.PAPER,ChatColor.GRAY+"Try /battlecards reload"));
  }
  while(slot<=34){i.setItem(slot++,filler(Material.STAINED_GLASS_PANE,ChatColor.DARK_GRAY+" "));}
  // The bottom row mirrors the player's nine-card BattleCards hotbar.
  for(int h=0;h<9;h++){
   ItemStack current=player.getInventory().getItem(h);
   if(current!=null&&p.cards().cardId(current)!=null){
    ItemStack copy=current.clone();ItemMeta m=copy.getItemMeta();List<String> lore=m.hasLore()?new ArrayList<String>(m.getLore()):new ArrayList<String>();lore.add(ChatColor.RED+"Click to remove from hotbar");m.setLore(lore);copy.setItemMeta(m);i.setItem(45+h,copy);
   }else i.setItem(45+h,filler(Material.STAINED_GLASS_PANE,ChatColor.DARK_GRAY+"Empty Slot "+(h+1)));
  }
  return i;
 }
 public Inventory createDomains(Player player){
  String chosen=p.profiles().getClass(player);
  Inventory i=Bukkit.createInventory(null,54,DOMAINS);
  i.setItem(43,filler(Material.BARRIER,ChatColor.RED+"Back"));
  i.setItem(44,filler(Material.NETHER_STAR,ChatColor.GOLD+"Profile"));
  int slot=9;
  if(chosen==null||chosen.isEmpty()){i.setItem(22,filler(Material.BARRIER,ChatColor.RED+"Choose a class first"));return i;}
  for(CardDefinition d:p.cards().all()){
   if(!d.cardClass.equalsIgnoreCase(chosen)||!d.type.startsWith("DOMAIN_"))continue;
   if(slot>44)break;
   ItemStack card=p.cards().createItem(d.id,1);if(card!=null)i.setItem(slot++,card);
  }
  while(slot<=44)i.setItem(slot++,filler(Material.STAINED_GLASS_PANE,ChatColor.DARK_GRAY+" "));
  return i;
 }
 public String libraryCategory(String title){
  if(title==null||!title.startsWith(LIBRARY_PREFIX))return null;
  int a=title.indexOf('['),b=title.indexOf(']',a+1);if(a<0||b<0)return "OFFENSE";return title.substring(a+1,b);
 }
 public boolean isLibrary(String title){return title!=null&&title.startsWith(LIBRARY_PREFIX);}
 public Inventory createProfile(Player player){return createProfile(player,0);}
 public Inventory createProfile(Player player,int page){if(page<0)page=0;List<String> all=p.isStaff(player)?Arrays.asList("none","spark","ember","flame","frost","portal","aqua","hearts","leaf","void","blood","electric","cosmic","snow","ash","rainbow","storm","orbit","eclipse","runes","inferno","galaxy"):p.profiles().unlockedCosmetics(p.profiles().getWins(player));int maxPage=Math.max(0,(all.size()-1)/7);if(page>maxPage)page=maxPage;Inventory i=Bukkit.createInventory(null,54,PROFILE+" [Cosmetics "+(page+1)+"/"+(maxPage+1)+"]");i.setItem(4,filler(Material.NETHER_STAR,ChatColor.GOLD+"Battle Profile"));i.setItem(13,filler(Material.DIAMOND,ChatColor.AQUA+"Wins: "+p.profiles().getWins(player)));i.setItem(22,filler(Material.REDSTONE,ChatColor.RED+"Losses: "+p.profiles().getLosses(player)));i.setItem(31,filler(Material.EMERALD,ChatColor.GREEN+"Shards: "+p.profiles().getShards(player)));int slot=36;List<String> titleList=p.isStaff(player)?Arrays.asList("Novice","Duelist","Card Adept","Spellblade","Arena Champion","Legend of Cards","Grandmaster of the Arcane","Eternal Cardlord"):p.profiles().unlockedTitles(p.profiles().getWins(player));for(String t:titleList){ItemStack x=filler(Material.NAME_TAG,ChatColor.YELLOW+t);ItemMeta m=x.getItemMeta();List<String> lore=new ArrayList<String>();lore.add(ChatColor.GRAY+"Click to equip this title");if(t.equals(p.profiles().getTitle(player)))lore.add(ChatColor.GREEN+"Currently equipped");m.setLore(lore);m.setDisplayName(ChatColor.YELLOW+t);m.setLocalizedName("TITLE:"+t);x.setItemMeta(m);if(slot<45)i.setItem(slot++,x);}List<String> names=Arrays.asList("None","Spark","Ember","Flame","Frost","Portal","Aqua","Hearts","Leaf","Void","Blood","Electric","Cosmic","Snow","Ash","Rainbow","Storm","Orbit","Eclipse","Runes","Inferno","Galaxy");int from=page*7;for(int j=0;j<7&&from+j<all.size();j++){String c=all.get(from+j);ItemStack x=filler(Material.FIREWORK,ChatColor.LIGHT_PURPLE+names.get(Math.min(names.size()-1,from+j)));ItemMeta m=x.getItemMeta();m.setLocalizedName("COSMETIC:"+c);m.setLore(Arrays.asList(ChatColor.GRAY+"Click to equip this cosmetic",c.equals(p.profiles().getCosmetic(player))?ChatColor.GREEN+"Currently equipped":ChatColor.GRAY+"Unlocks through wins"));x.setItemMeta(m);i.setItem(45+j,x);}if(page>0)i.setItem(52,filler(Material.ARROW,ChatColor.YELLOW+"Previous cosmetics"));if(page<maxPage)i.setItem(53,filler(Material.ARROW,ChatColor.YELLOW+"Next cosmetics"));else i.setItem(53,filler(Material.BARRIER,ChatColor.RED+"Close"));return i;}
 public static String profileKey(ItemStack x){if(x==null||!x.hasItemMeta())return null;String s=x.getItemMeta().getLocalizedName();return s==null||s.length()==0?null:s;}
}
