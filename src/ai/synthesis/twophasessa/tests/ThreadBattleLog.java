/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.synthesis.twophasessa.tests;

import util.SOA.*;
import Standard.StrategyTactics;
import ai.CMAB.CmabNaiveMCTS;
import ai.RandomBiasedAI;
import ai.abstraction.LightRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.ahtn.AHTNAI;
import ai.asymmetric.GAB.SandBox.GABScriptChoose;
import ai.asymmetric.PGS.LightPGSSCriptChoice;
import ai.core.AI;
import ai.asymmetric.SAB.SABScriptChoose;
import ai.asymmetric.SSS.LightSSSmRTSScriptChoice;
import ai.competition.dropletGNS.DropletWithin;
import ai.competition.newBotsEval.botEmptyBase;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetSearchMCTS;
import ai.scv.SCVPlus;
import gui.PhysicalGameStatePanel;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class ThreadBattleLog implements Runnable {

    AI ai1, ai2;
    String sMap;
    String sIte, pathLog;    
    int id1, id2;
    String uniqueID;
    UnitTypeTable utt;
    
    AI winner;
    char winnerSide;
    //char player1Side, player2Side;

    public ThreadBattleLog(AI ai1, AI ai2, String sMap, String sIte, String pathLog, 
            int id1, int id2, String uniqueID, UnitTypeTable utt) {
        this.ai1 = ai1; // jogador da esquerda encima
        this.ai2 = ai2; // jogador da direita embaixo
        this.sMap = sMap;
        this.sIte = sIte;
        this.pathLog = pathLog;
        this.id1 = id1;
        this.id2 = id2;
        this.uniqueID = uniqueID;
        this.utt = utt;
    }
    
    

    @Override
    public void run() {
        try {
            ArrayList<String> log = new ArrayList<>();
            Instant timeInicial = Instant.now();
            Duration duracao;        

            //UnitTypeTable utt = new UnitTYpeTableBattle();            
            PhysicalGameState pgs = PhysicalGameState.load(sMap, utt);            

            GameState gs = new GameState(pgs, utt);
            int MAXCYCLES = 4000;
            int PERIOD = 20;
            boolean gameover = false;

            if (pgs.getHeight() == 8) {
                MAXCYCLES = 3000;
            } else if (pgs.getHeight() == 16) {
                MAXCYCLES = 4000;
            } else if (pgs.getHeight() == 24) {
                MAXCYCLES = 5000;
            } else if (pgs.getHeight() == 32) {
                MAXCYCLES = 6000;
            } else if (pgs.getHeight() == 64) {
                MAXCYCLES = 8000;
            }

            /*
            Vari??veis para coleta de tempo
             */
            double ai1TempoMin = 9999, ai1TempoMax = -9999;
            double ai2TempoMin = 9999, ai2TempoMax = -9999;
            double sumAi1 = 0, sumAi2 = 0;
            int totalAction = 0;

            log.add("---------AIs---------");
            log.add("AI 1 = " + ai1.toString());
            log.add("AI 2 = " + ai2.toString() + "\n");
            System.out.println("AI 1 = " + ai1.toString());
            System.out.println("AI 2 = " + ai2.toString() + "\n");

            log.add("---------Mapa---------");
            log.add("Mapa= " + sMap + "\n");
            
            //JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);
            //w.setLocation(posx, posy);
            //JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
            long startTime;
            long timeTemp;
            //System.out.println("Tempo de execu????o P2="+(startTime = System.currentTimeMillis() - startTime));            
            
            do {                    
                    totalAction++;
                    startTime = System.currentTimeMillis();

                    PlayerAction pa1 = ai1.getAction(0, gs);
                    //dados de tempo ai1
                    timeTemp = (System.currentTimeMillis() - startTime);
                    sumAi1 += timeTemp;
                    //coleto tempo m??nimo
                    if (ai1TempoMin > timeTemp) {
                        ai1TempoMin = timeTemp;
                    }
                    //coleto tempo maximo
                    if (ai1TempoMax < timeTemp) {
                        ai1TempoMax = timeTemp;
                    }

                    startTime = System.currentTimeMillis();
                    PlayerAction pa2 = ai2.getAction(1, gs);
                    //dados de tempo ai2
                    timeTemp = (System.currentTimeMillis() - startTime);
                    sumAi2 += timeTemp;
                    //coleto tempo m??nimo
                    if (ai2TempoMin > timeTemp) {
                        ai2TempoMin = timeTemp;
                    }
                    //coleto tempo maximo
                    if (ai2TempoMax < timeTemp) {
                        ai2TempoMax = timeTemp;
                    }

                    gs.issueSafe(pa1);
                    gs.issueSafe(pa2);

                    // simulate:
                    gameover = gs.cycle();
                    //w.repaint();      
                
                //avaliacao de tempo
                duracao = Duration.between(timeInicial, Instant.now());

            } while (!gameover && (gs.getTime() < MAXCYCLES));

            log.add("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

            log.add("Tempos de AI 1 = " + ai1.toString());
            log.add("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

            log.add("Tempos de AI 2 = " + ai2.toString());
            log.add("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

            log.add("Winner " + Integer.toString(gs.winner()));
            
            log.add("Game Over");            
            System.out.println("Winner " + Integer.toString(gs.winner()));            
            System.out.println("Game Over");            
            //w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
            
            //System.out.println("AI 1 = " + ai1.toString());           
            //System.out.println("AI 2 = " + ai2.toString());            
            //System.out.println("Winner " + Integer.toString(gs.winner()));
            if(gs.winner() == 0) {
            	//System.out.println(ai1.toString());
            	this.winner = ai1;
            	this.winnerSide = 'e';
            }else if (gs.winner() == 1){
            	//System.out.println(ai2.toString());
            	this.winner = ai2;
            	this.winnerSide = 'd';
            }
            //System.out.println("- - - - - - - - - - - - - - - - - - - - -");
            
            //gravarLog(log, String.valueOf(id1), String.valueOf(id2), sMap, sIte, pathLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void gravarLog(ArrayList<String> log, String sIA1, String sIA2, String sMap, String sIte, String pathLog) throws IOException {
        if (!pathLog.endsWith("/")) {
            pathLog += "/";
        }
        String nameArquivo = pathLog + "match_" + sIA1 + "_" + sIA2 + "_" + this.uniqueID + ".scv";
        File arqLog = new File(nameArquivo);
        if (!arqLog.exists()) {
            arqLog.createNewFile();
        }
        //abre o arquivo e grava o log
        try {
            FileWriter arq = new FileWriter(arqLog, false);
            PrintWriter gravarArq = new PrintWriter(arq);
            for (String l : log) {
                gravarArq.println(l);
            }

            gravarArq.flush();
            gravarArq.close();
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static List<AI> decodeScripts(UnitTypeTable utt, String sScripts) {

        //decomp??e a tupla
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        String[] itens = sScripts.split(";");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }

        List<AI> scriptsAI = new ArrayList<>();

        ScriptsCreator sc = new ScriptsCreator(utt, 300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });

        return scriptsAI;
    }
    
    public AI getWinner() {
    	return this.winner;
    }
    
    public char getWinnerSide() {
    	return this.winnerSide;
    }
    
    public AI getAI1() {
    	return ai1;
    }
    
    public AI getAI2() {
    	return ai2;
    }
    
}
