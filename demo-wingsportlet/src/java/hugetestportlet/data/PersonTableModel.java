package hugetestportlet.data;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class PersonTableModel extends AbstractTableModel {

	private List<PersonBean> personList = new LinkedList<PersonBean>();

	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		return personList.size();
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return "prof";
		case 2:
			return "dr";
		case 3:
			return "Name";
		case 4:
			return "forename";
		default:
			return "missing column";
		}
	}

	public Object getValueAt(int row, int column) {
		PersonBean person = personList.get(row);
		switch (column) {
		case 0:
			return person.getGender();
		case 1:
			return person.isProf();
		case 2:
			return person.isDr();
		case 3:
			return person.getName();
		case 4:
			return person.getGivenName();
		default:
			return person.getName();
		}
	}

	public void addPerson(PersonBean person) {
		personList.add(person);
	}

	public void removePerson(int row) {
		if (row < personList.size())
			personList.remove(row);
	}

	public boolean isCellEditable(int row, int column) {
		return true; // allow for all cells
	}

	public void setValueAt(Object object, int row, int column) {
		PersonBean person = personList.get(row);
		switch (column) {
		case 0:
			person.setGender((String) object);
			break;
		case 1:
			person.setProf((Boolean) object);
			break;
		case 2:
			person.setDr((Boolean) object);
			break;
		case 3:
			person.setName((String) object);
			break;
		case 4:
			person.setGivenName((String) object);
			break;
		}

	}

	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
			return Boolean.class;
		case 2:
			return Boolean.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		default:
			return String.class;
		}
	}
}
