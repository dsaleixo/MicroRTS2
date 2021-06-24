/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.synthesis.dslForScriptGenerator.DSLBasicConditional;

import java.util.HashMap;

import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author julian and rubens
 */
public interface IConditional {
    
    public boolean runConditional(GameState game, int player, PlayerAction currentPlayerAction, 
                                        PathFinding pf, UnitTypeTable a_utt, HashMap<Long, String> counterByFunction);
    
}
