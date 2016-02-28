package bpel.transformator.aldebarane;

public class Edge {

	private String start_state;
	private String label;
	private String bpel_action_name;
	private String bpel_action;
	private String bpel_operation;
	private String bpel_partnerLink;
	private String bpel_process_name;
	private String end_state;

	public Edge(String start_state, String label, String end_state) {
		this.start_state = start_state;
		this.label = label;
		this.end_state = end_state;
	}

	public String getStart_state() {
		return start_state;
	}

	public void setStart_state(String start_state) {
		this.start_state = start_state;
	}

	public String getLabel() {

		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getEnd_state() {
		return end_state;
	}

	public void setEnd_state(String end_state) {
		this.end_state = end_state;
	}
 

	public String getBpel_partnerLink() {
		return bpel_partnerLink;
	}

	public void setBpel_partnerLink(String bpel_partnerLink) {
		this.bpel_partnerLink = bpel_partnerLink;
	}

	public String getBpel_process_name() {
		return bpel_process_name;
	}

	public void setBpel_process_name(String bpel_process_name) {
		this.bpel_process_name = bpel_process_name;
	}

	public String getBpel_action() {
		return bpel_action;
	}

	public void setBpel_action(String bpel_action) {
		this.bpel_action = bpel_action;
	}

	public String getBpel_operation() {
		return bpel_operation;
	}

	public void setBpel_operation(String bpel_operation) {
		this.bpel_operation = bpel_operation;
	}

	public String getBpel_action_name() {
		return bpel_action_name;
	}

	public void setBpel_action_name(String bpel_action_name) {
		this.bpel_action_name = bpel_action_name;
	}

	public String to_string() {

		return "(" + this.start_state + "," + this.label + "," + this.end_state + ")";
	}

}
