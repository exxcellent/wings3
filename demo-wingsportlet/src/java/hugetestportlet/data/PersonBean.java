package hugetestportlet.data;

public class PersonBean {

	private String gender;

	private boolean prof;

	private boolean dr;

	private String name;

	private String givenName;

	public PersonBean() {

	}

	public PersonBean(String gender, boolean prof, boolean dr, String name,
			String givenName) {
		this.gender = gender;
		this.prof = prof;
		this.dr = dr;
		this.name = name;
		this.givenName = givenName;
	}

	public boolean isDr() {
		return dr;
	}

	public void setDr(boolean dr) {
		this.dr = dr;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isProf() {
		return prof;
	}

	public void setProf(boolean prof) {
		this.prof = prof;
	}

}
