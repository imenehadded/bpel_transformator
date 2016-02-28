package bpel.transformator.aldebarane;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.regex.derivator.Derivative;
import com.regex.derivator.Expression;
import com.regex.derivator.ExpressionString;

import bpel.transformator.BpelManager;

public class AutManager {

	private List<String> aut_content = new ArrayList<String>();

	private String org_expression = null;

	private List<Edge> critical = null;

	public AutManager(String aut_location_file, String expression) throws IOException {

		this.org_expression = expression;

		FileInputStream ins = new FileInputStream(aut_location_file);

		InputStreamReader r = new InputStreamReader(ins, "UTF-8");
		BufferedReader br = new BufferedReader(r);

		String line = null;
		while ((line = br.readLine()) != null) {
			aut_content.add(line);
		}
		br.close();

	}

	private List<Edge> getEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		for (String s_edge : this.aut_content)
			if (s_edge.startsWith("des") == false) {
				String[] l_edge = s_edge.split(",");
				Edge new_edge = new Edge(l_edge[0].replace("(", ""), l_edge[1], l_edge[2].replace(")", ""));

				edges.add(new_edge);
			}
		return edges;
	}

	private List<Edge> getSuccessors(String start_stat) {

		List<Edge> successors = new ArrayList<Edge>();

		for (Edge edge : this.getEdges())
			if (edge.getStart_state().equals(start_stat))
				successors.add(edge);

		return successors;
	}

	private void computeDerivatives(String s, String expression) {

		List<Edge> successors = this.getSuccessors(s);

		for (Edge edge : successors) {

			String action = edge.getBpel_action_name();

			String d_expression = derivative(expression, action);

			if (expression.equals(d_expression) == false) {

				this.critical.add(edge);

				expression = d_expression;
			}

			this.computeDerivatives(edge.getEnd_state(), expression);

		}
	}

	private String derivative(String expression, String action) {

		ExpressionString expressionString = new ExpressionString(expression);
		Expression expressionObj = expressionString.getExpression();

		Expression exp_action = new Expression();
		exp_action.name_action = action;
		Derivative derivative = new Derivative(expressionObj, exp_action);

		Expression expressionNew = derivative.getExpression();
		expressionNew = Derivative.simplify_Expression(expressionNew);

		String result = expressionNew.toString();

		System.out.println("EXPRESSION : " + expression + ", ACTION : " + action + ", RESULT : " + result);

		return result;
	}

	private String getInitialState(List<Edge> edges) {

		String index = null;

		for (String des : this.aut_content)
			if (des.startsWith("des") == true)
				index = des.split(",")[0].replace("des(", "");

		return index;
	}

	public List<Edge> getCriticalEdges() {

		this.critical = new ArrayList<Edge>();

		List<Edge> edges = this.getEdges();

		String s0 = this.getInitialState(edges);

		this.computeDerivatives(s0, this.org_expression);

		return this.critical;
	}

	public String[] splitLable(String lable, String process_name) {

		String[] split = new String[4];
		String[] actions = { "invoke", "reply", "receive" };

		split[0] = process_name;

		// Extracting action name
		for (String action : actions)
			if (lable.indexOf(action) > 0) {
				split[2] = action;
				break;
			}

		if (split[2] == null) {
			System.err.println("No action " + actions + " found in label : " + lable);
			return null;
		}

		// Extracting the operation name
		String[] rl_split = lable.split("_" + split[2] + "_");

		split[3] = rl_split[1];

		// Extracting partnerlink
		split[1] = rl_split[0].split(process_name + "_")[1];

		return split;
	}

	public void loadBpelLables(BpelManager bpelManger, List<Edge> allEdges) {
		System.out.println("################# IN LOAD BPEL LABLES ##############");
		String process_name = bpelManger.get_process_name();

		for (Edge edge : allEdges) {
 
			String[] split = this.splitLable(edge.getLabel(), process_name);

			String bpel_action_name = bpelManger.get_action_name(split[2], split[1], split[3]);

			if (bpel_action_name == null)  
				continue;
	 
			edge.setBpel_process_name(split[0]);
			edge.setBpel_partnerLink(split[1]);
			edge.setBpel_action(split[2]);
			edge.setBpel_operation(split[3]);

			edge.setBpel_action_name(bpel_action_name);

		}
	}
}
