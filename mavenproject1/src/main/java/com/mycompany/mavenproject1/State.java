/*
 * Copyright (C) 2019 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.KillerBot;

/**
 *
 * @author a7capone/p7fontan
 */
public enum State { 
    ATTACK {
        @Override
        public State currState(KillerBot bot){
            bot.reward = 100;
            return update(bot);
        }
    },
    HURT {
        @Override 
        public State currState(KillerBot bot){
            bot.reward = -25;
            return update(bot);
        }
    },
    SEARCH {
        @Override
        public State currState(KillerBot bot){
            bot.reward = 50;
            return update(bot);
        }
    },
    IDLE {
        @Override
        public State currState(KillerBot bot){
            bot.reward = 0;
            return update(bot);
        }
    },
    DEAD {
        @Override
        public State currState(KillerBot bot){
            bot.reward = -200;
            return update(bot);
        }
    };
    
    public abstract State currState(KillerBot bot);
    
    public State update(KillerBot bot) {
        // Old Bot
        if ((bot.shouldEngage && bot.getPlayers().canSeeEnemies() && bot.getWeaponry().hasLoadedWeapon()) || bot.getSenses().isBeingDamaged()) {
            return ATTACK;
        }
        else if (bot.shouldCollectHealth && bot.getInfo().getHealth() < bot.healthLevel) {
            return HURT;
        }
        else if (bot.enemy != null && bot.shouldPursue && bot.getWeaponry().hasLoadedWeapon()){
            return SEARCH;
        }
        else {
            return IDLE;
        }
    }
}
