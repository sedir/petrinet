package br.ufrn.msed.s20141.dsj.petrinet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;

public class MarkupProcessor {
	Petrinet petrinet;
	String script;

	public MarkupProcessor(String script) throws NumberFormatException, IOException {
		this.script = script;
		this.petrinet = new Petrinet();
		BufferedReader br = new BufferedReader(new StringReader(script));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			String[] command = strLine.split("\\s+");

			if (command[0].equalsIgnoreCase("net")){
				StringBuilder strb = new StringBuilder();
				for (String string : command) {
					strb.append(string+" ");
				}
				petrinet.setName(strb.toString());
			} else if (command[0].equalsIgnoreCase("p")){
				Place p = petrinet.getPlace(command[1]);
				if (command.length == 3){
					int tokens = Integer.parseInt(command[2]);
					p.setTokens(tokens);
				}
			} else if (command[0].equalsIgnoreCase("t")){
				petrinet.getTransitionOrAdd(command[1]);
			} else if (command[0].equalsIgnoreCase("a") && command.length >= 3 && command.length <= 4){
				int weight = 1;
				if (command.length == 4){
					weight = Integer.parseInt(command[3]);
				}
				if (petrinet.existPlace(command[1]) && petrinet.existTransition(command[2])){
					petrinet.arc(petrinet.getPlace(command[1]),petrinet.getTransitionOrAdd(command[2]),weight);
				}
				if (petrinet.existTransition(command[1]) && petrinet.existPlace(command[2])){
					petrinet.arc(petrinet.getTransitionOrAdd(command[1]),petrinet.getPlace(command[2]),weight);
				}
			}
		}
		br.close();
	}

	public MarkupProcessor(File file) throws NumberFormatException, IOException{
		this.petrinet = new Petrinet();
		StringBuilder builderScript = new StringBuilder(); 
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			builderScript.append(strLine+"\n");
			String[] command = strLine.split("\\s+");

			if (command[0].equalsIgnoreCase("net")){
				StringBuilder strb = new StringBuilder();
				for (String string : command) {
					strb.append(string+" ");
				}
				petrinet.setName(strb.toString());
			} else if (command[0].equalsIgnoreCase("p")){
				Place p = petrinet.getPlace(command[1]);
				if (command.length == 3){
					int tokens = Integer.parseInt(command[2]);
					p.setTokens(tokens);
				}
			} else if (command[0].equalsIgnoreCase("t")){
				petrinet.getTransitionOrAdd(command[1]);
			}  else if (command[0].equalsIgnoreCase("a") && command.length >= 3 && command.length <= 4){
				int weight = 1;
				if (command.length == 4){
					weight = Integer.parseInt(command[3]);
				}
				if (petrinet.existPlace(command[1]) && petrinet.existTransition(command[2])){
					petrinet.arc(petrinet.getPlace(command[1]),petrinet.getTransitionOrAdd(command[2]),weight);
				}
				if (petrinet.existTransition(command[1]) && petrinet.existPlace(command[2])){
					petrinet.arc(petrinet.getTransitionOrAdd(command[1]),petrinet.getPlace(command[2]),weight);
				}
			}
		}
		br.close();
		script = builderScript.toString();

	}

	public Petrinet getPetrinet() {
		return petrinet;
	}

	public String getScript() {
		return script;
	}
}


