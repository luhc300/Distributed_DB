package transaction;



public class ResourceItemImpl implements ResourceItem {
	private String resourceType = null;
	private String key = null;
	private String[] ColumnNames = null;
	private String[] ColumnValues = null;
	private boolean ifDeleted = false;
	public ResourceItemImpl(String resourceType, String primaryKey, String[] colNames, String[] colValues) {
		this.resourceType = resourceType;
		key = primaryKey;
		ColumnNames = colNames;
		ColumnValues = colValues;
	}
	public ResourceItemImpl(String resourceType,  String[] colNames, String[] colValues) {
		this.resourceType = resourceType;
		ColumnNames = colNames;
		ColumnValues = colValues;
	}
	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return ColumnNames;
	}

	@Override
	public String[] getColumnValues() {
		// TODO Auto-generated method stub
		return ColumnValues;
	}

	@Override
	public String getIndex(String indexName) throws InvalidIndexException {
		// TODO Auto-generated method stub
		for (int i = 0; i < ColumnNames.length; i++) {
			if (ColumnNames[i].equals(indexName)) {
				return ColumnValues[i];
			}
		}
		return null;
	}

	
	@Override
	public Object getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return ifDeleted;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		ifDeleted = true;
	}
	public Object clone() {
		return null;
	}
}
