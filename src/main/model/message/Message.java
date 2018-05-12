package model.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String operation;
	private String sender;
	private List<String> recipients;
	private Map<String, String> values;

	public Message() {
		recipients = new ArrayList<>();
		values = new HashMap<>();
	}

	public Message(String operation) {
		this();
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public String addValue(String key, String value) {
		return values.put(key, value);
	}

	public void addRecipient(String recipient) {
		recipients.add(recipient);
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public String getRecipient(String recipient) {
		return recipients
					.stream()
					.filter(r -> r.equals(recipient))
					.findFirst()
					.orElseGet(() -> "");
	}

	public String getRecipient(int index) {
		return recipients.get(index);
	}

	public int getRecipientsSize() {
		return recipients.size();
	}

}
