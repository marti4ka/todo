package de.unimannheim.becker.todo.md.model;


public class Item {
	private int id;
	private String title;
	private String description;
	private boolean archived;
	private long timestamp;

	public Item() {
		super();
	}

	public Item(int id, String title, String description, boolean archived, long timestamp) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.archived = archived;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
