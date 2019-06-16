package com.mycompany.mavenproject1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import com.mycompany.mavenproject1.KillerBot;


/**
 * Example of Simple Pogamut bot, that randomly walks around the map searching
 * for preys shooting at everything that is in its way.
 *
 * @author Rudolf Kadlec aka ik
 * @author a7capone/p7fontan
 */

@AgentScoped
public class KillerBot extends UT2004BotModuleController<UT2004Bot> {
    
    @JProp
    public int debutHealth = 100; //Bot's health at the attack state debut
    
    @JProp
    public int score = 0; //Learning Score
    
    @JProp
    public boolean test = false; 
    /**
     * boolean switch to activate escape behavior
     */
    
    @JProp
    public boolean shouldEscape = false;
    /**
     * boolean switch to activate engage behavior
     */

    @JProp
    public boolean shouldEngage = true;
    /**
     * boolean switch to activate pursue behavior
     */

    @JProp
    public boolean shouldPursue = true;
    /**
     * boolean switch to activate rearm behavior
     */

    @JProp
    public boolean shouldRearm = true;
    /**
     * boolean switch to activate collect health behavior
     */

    @JProp
    public boolean shouldCollectHealth = true;
    /**
     * how low the health level should be to start collecting health items
     */

    @JProp
    public int healthLevel = 40;
    /**
     * how many times the hunter killed other bots (i.e., bot has fragged them /
     * got point for killing somebody)
     */

    @JProp
    public int frags = 0;
    /**
     * how many times the hunter died
     */
    @JProp
    public int deaths = 0;

    @JProp
    public ItemType weaponList[] = {UT2004ItemType.LIGHTNING_GUN,UT2004ItemType.SHOCK_RIFLE,UT2004ItemType.MINIGUN,UT2004ItemType.FLAK_CANNON,UT2004ItemType.ROCKET_LAUNCHER,UT2004ItemType.LINK_GUN,UT2004ItemType.ASSAULT_RIFLE,UT2004ItemType.BIO_RIFLE};
   
            
    @JProp
    public boolean deadPlayer = false;
    /*public double weights[] = {0,0,0,0,0,0,0,0}; // Weights list*/
    
    
    /**
     * {@link PlayerKilled} listener that provides "frag" counting + is switches
     * the state of the hunter.
     *
     * @param event //WHERE TO PUT IT!!!!!!!!
     */
    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        if (event.getKiller().equals(info.getId())) {
            ++frags;
        }
        if (enemy == null) {
            return;
        }
        if (enemy.getId().equals(event.getId())) {
            enemy = null;
        }

    }
    /**
     * Used internally to maintain the information about the bot we're currently
     * hunting, i.e., should be firing at.
     */
    protected Player enemy = null;
    /**
     * Item we're running for. 
     */
    protected Item item = null;
    /**
     * Taboo list of items that are forbidden for some time.
     */
    protected TabooSet<Item> tabooItems = null;
    
    private UT2004PathAutoFixer autoFixer;
    
    private static int instanceCount = 0;

    public enum States {ATTACK, SEARCH, IDLE, HURT, DEAD};

    public States state;

    public enum Actions {newEnemy, Shoot, runTowards, search, Medkit, Hide, Idle};

    public Actions action;

    public EpsilonGreedyExploration greedyExplore;

    public double reward = 0;

    public Sarsa sarsa = new Sarsa(States.values().length, Actions.values().length, greedyExplore,true);

    /**
     * Bot's preparation - called before the bot is connected to GB2004 and
     * launched into UT2004.S
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        tabooItems = new TabooSet<Item>(bot);

        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

        // listeners        
        navigation.getState().addListener(new FlagListener<NavigationState>() {

            @Override
            public void flagChanged(NavigationState changedValue) {
                switch (changedValue) {
                    case PATH_COMPUTATION_FAILED:
                    case STUCK:
                        if (item != null) {
                            tabooItems.add(item, 10);
                        }
                        reset();
                        break;

                    case TARGET_REACHED:
                        reset();
                        break;
                }
            }
        });

        // DEFINE WEAPON PREFERENCES
        weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false); 
        weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true); 
        weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true); 
        weaponPrefs.addGeneralPref(UT2004ItemType.ONS_AVRIL, true); //Anti-vehicule Weapon
    }

    /**
     * Here we can modify initializing command for our bot.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        // just set the name of the bot and his skill level, 1 is the lowest, 7 is the highest
    	// skill level affects how well will the bot aim
        return new Initialize().setName("Killer_bot-" + (++instanceCount)).setDesiredSkill(5);
    }

    /**
     * Resets the state of the Hunter.
     */
    protected void reset() {
        bot.getBotName().setInfo("Score:" + score);
    	item = null;
        enemy = null;
        navigation.stopNavigation();
        itemsToRunAround = null;

        // Set Discount Factor and Learning Rate
        sarsa.setDiscountFactor(0.95);
        sarsa.setLearningRate(0.25);

        // Reset state and action to restart
        state = States.SEARCH;
        sarsa.setState(state.ordinal());
        action = Actions.values()[sarsa.getAction(state.ordinal())];
        sarsa.setAction(action.ordinal());
    }
    
    /**
     * Score chart recompensation
     */
    /*protected double rewardUpdate(int initialHealth, int finalHealth, boolean dead) {

        double reward = 0; // = 200-(initialHealth-finalHealth);
        //double punishment = -200+(200-initialHealth);
        if(dead == true){
            reward = -100;
        }
        else if (deadPlayer){
            reward = 100;
        }
        else {
            reward = 0;
        }
        return reward;
    }*/

    public void update(){
        // Define nextState and nextAction
        States nextState;

        // Define next State with bot perceptions and update rewards
        if ((this.shouldEngage && this.getPlayers().canSeeEnemies() && this.getWeaponry().hasLoadedWeapon()) || this.getSenses().isBeingDamaged()) {
            nextState = States.ATTACK;
            reward = 100;
        }
        else if (this.shouldCollectHealth && this.getInfo().getHealth() < this.healthLevel) {
            nextState = States.HURT;
            reward = 0;
        }
        else if (this.enemy != null && this.shouldPursue && this.getWeaponry().hasLoadedWeapon()){
            nextState = States.SEARCH;
            reward = 25;
        }
        else if (deadPlayer){
            nextState = States.DEAD;
            reward = -100;
        }
        else {
            nextState = States.IDLE;
            reward = 0;
        }

        // Learn and update state and action with next action and next state
        action = Actions.values()[sarsa.learn(reward, nextState.ordinal())];
        sarsa.setAction(action.ordinal());
        sarsa.setState(nextState.ordinal());
        
        // newEnemy, Shoot, runTowards, search, Medkit, Hide, Idle
        switch(action){
            case newEnemy:
                pickEnemy();
                break;
            case Shoot:
                shoot();
                break;
            case runTowards:
                followEnemy();
                break;
            case search:
                searchAndPursue();
                break;
            case Medkit:
                getMedkit();
                break;
            case Hide:
                runForLife();
                break;
            case Idle:
                getItem();
                break;
        }
    }

    @EventListener(eventClass = PlayerDamaged.class)
    public void playerDamaged(PlayerDamaged event) {
    	log.info("I have just hurt other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
    }
    
    @EventListener(eventClass = BotDamaged.class)
    public void botDamaged(BotDamaged event) {
    	log.info("I have just been hurt by other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     *
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    @Override
    public void logic() {   
        if(score+1 == stats.getKilledOthers()){
            score++;
            for ( int i = 0; i < 8; i++){
                if(weaponry.getCurrentWeapon().getType().getGroup() == weaponList[i].getGroup()){
                    //rewardUpdate( debutHealth, info.getHealth(), true, i);
                    test=false;
                    break;
                }
            }  
        }
        update();
    }

    //////////////////
    // STATE ATTACK //
    //////////////////
    protected boolean runningToPlayer = false;

    /**
     * Fired when bot see any enemy. <ol> <li> if enemy that was attacked last
     * time is not visible than choose new enemy <li> if enemy is reachable and the bot is far - run to him
     * <li> otherwise - stand still (kind a silly, right? :-)
     * </ol>
     */
    protected void pickEnemy() {
        if (test == false){
            test= true;
            debutHealth = info.getHealth();
        }
        pursueCount = 0;

        // 1) pick new enemy if the old one has been lost
        if (enemy == null || !enemy.isVisible()) {
            // pick new enemy
            enemy = players.getNearestVisiblePlayer(players.getVisibleEnemies().values());
            if (enemy == null) {
                log.info("Can't see any enemies... ???");
                if(senses.isBeingDamaged()){
                    bot.getBotName().setInfo("Someone is hiting me!");
                    if (navigation.isNavigating()) {
                        navigation.stopNavigation();
                        item = null;
                    }
                    getAct().act(new Rotate().setAmount(32000));
                }
                return;
            }
        }
    }

    protected void shoot(){
        boolean shooting = false;
        double distance = Double.MAX_VALUE;
        // 2) stop shooting if enemy is not visible
        if (!enemy.isVisible()){
            if (info.isShooting() || info.isSecondaryShooting()) {
                // stop shooting
                getAct().act(new StopShooting());
            }
                runningToPlayer = false;
        } else {
        	// 2) or shoot on enemy if it is visible
	        distance = info.getLocation().getDistance(enemy.getLocation());
	        if (shoot.shoot(weaponPrefs, enemy) != null) {
	            log.info("Shooting at enemy!!!");
                log.info("with : " + weaponry.getCurrentWeapon().getType().getGroup());
                bot.getBotName().setInfo("ATTACK");
	            shooting = true;
                log.info("Ammo =" + weaponry.getCurrentAmmo());                  
                if(weaponry.getCurrentAmmo() < weaponry.getMaxAmmo(weaponry.getCurrentWeapon().getType())/4){
                    bot.getBotName().setInfo("Reload");
                        weaponry.changeWeapon(weaponry.getLoadedWeapons().values().iterator().next());
                        log.info("Weapon Changed!!!");
                }
            }    
        }
    }

    protected void followEnemy(){
        boolean shooting = false;
        double distance = Double.MAX_VALUE;
        // 3) if enemy is far or not visible - run to him
        int decentDistance = Math.round(random.nextFloat() * 800) + 200;
        if (!enemy.isVisible() || !shooting || decentDistance < distance) {
            if (!runningToPlayer) {
                bot.getBotName().setInfo("SEARCHING");
                log.info("Searching !!!");
                navigation.navigate(enemy);
                runningToPlayer = true;
            }
        } else {
            runningToPlayer = false;
            navigation.stopNavigation();
        }

        item = null;
    }
           
    //////////////////
    // STATE SEARCH //
    //////////////////
    /**
     * State pursue is for pursuing enemy who was for example lost behind a
     * corner. How it works?: <ol> <li> initialize properties <li> obtain path
     * to the enemy <li> follow the path - if it reaches the end - set lastEnemy
     * to null - bot would have seen him before or lost him once for all </ol>
     */
    
    protected void searchAndPursue() {
        log.info("Decision is: Search and Pursue");
        ++pursueCount;
        if (pursueCount > 30) {
            reset();  
        }
        if (enemy != null) {
        	bot.getBotName().setInfo("Search/Pursue");
        	navigation.navigate(enemy);
        	item = null;
        } else {
            reset();
        }
    }
    
    protected int pursueCount = 0;

    //////////////////
    // STATE HURT   //
    //////////////////
    protected void getMedkit() {
        // 1) Heal by getting Medkit
        log.info("Decision is: MEDKIT");
        Item item = items.getPathNearestSpawnedItem(ItemType.Category.HEALTH);
        //log.info( info.getHealt());
        if (item == null) {
        	log.warning("NO HEALTH ITEM TO RUN TO => ITEMS");
        	//getItem();
        } else {
        	bot.getBotName().setInfo("MEDKIT");
        	navigation.navigate(item);
                this.item = item;
        }
    }
    
    protected void runForLife(){
        // 2) if enemy is far or not visible - run in the opposite direction
        if (!players.canSeeEnemies() && shouldEscape) {
            NavPoint targetNavPoint = this.getNavPoints().getRandomNavPoint();
            if (navigation.isNavigating()){
                navigation.stopNavigation();
            }
            getAct().act(new Rotate().setAmount(32500));
            navigation.navigate(targetNavPoint);
            log.info("Running away!");
            //shouldEscape = false;
        }
    }

    ////////////////
    // STATE IDLE //
    ////////////////
    protected List<Item> itemsToRunAround = null;

    protected void getItem() {
        if (navigation.isNavigatingToItem()) return;
        
        List<Item> interesting = new ArrayList<Item>();
        
        // ADD WEAPONS
        for (ItemType itemType : ItemType.Category.WEAPON.getTypes()) {
        	if (!weaponry.hasLoadedWeapon(itemType)) interesting.addAll(items.getSpawnedItems(itemType).values());
        }
        // ADD ARMORS
        for (ItemType itemType : ItemType.Category.ARMOR.getTypes()) {
        	interesting.addAll(items.getSpawnedItems(itemType).values());
        }
        // ADD QUADS
        interesting.addAll(items.getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK).values());
        // ADD HEALTHS
        if (info.getHealth() < 100) {
        	interesting.addAll(items.getSpawnedItems(UT2004ItemType.HEALTH_PACK).values());
        }
        
        Item item = MyCollections.getRandom(tabooItems.filter(interesting));
        if (item == null) {  
                log.info("Bot weapon : " + weaponry.getCurrentWeapon().getType().getGroup().toString());
        	log.warning("NO ITEM TO RUN FOR!");
        	if (navigation.isNavigating()) return;
        	bot.getBotName().setInfo("RANDOM NAV");
        	navigation.navigate(navPoints.getRandomNavPoint());
        } else {
        	this.item = item;
        	log.info("RUNNING FOR: " + item.getType().getName());
        	// bot.getBotName().setInfo("ITEM: " + item.getType().getName() + "");
        	navigation.navigate(item);        	
        }        
    }

    ////////////////
    // BOT KILLED //
    ////////////////
    @Override
    public void botKilled(BotKilled event) {
        deadPlayer = true;
        for (int i = 0; i<8; i++){
            if(weaponList[i].getName().toUpperCase().equals(players.getPlayer(event.getKiller()).getWeapon().toUpperCase()+"PICKUP") || ("XWEAPONS."+weaponList[i].getGroup().getName().toUpperCase()).equals(players.getPlayer(event.getKiller()).getWeapon().toUpperCase())){
                test = false;
                break;
            }
        }
        reset();
    }

    ///////////////////////////////////
    public static void main(String args[]) throws PogamutException {
        // starts 3 Hunters at once
        // note that this is the most easy way to get a bunch of (the same) bots running at the same time        
    	new UT2004BotRunner(KillerBot.class, "Hunter").setMain(true).setLogLevel(Level.INFO).startAgents(1);
    }
}
