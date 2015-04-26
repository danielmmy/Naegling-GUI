package br.com.naegling;

import javax.swing.table.AbstractTableModel;

/**
 * Model for nodes table
 * @author Daniel Yokoyama
 *
 */
public class NodesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames;;
    private Object[][] data;
	

	/**
	 * Constructor
	 * @param data 
	 * @param columnNames
	 */
	public NodesTableModel(Object[][] data, String[] columnNames){
		if(data!=null && columnNames!=null){
			this.data=data;
			this.columnNames=columnNames;
		}else{
		    Object[][] obj = {{ "", ""},};
		    this.data=obj;
		}
			
	}


		/**
		 * Return column count
		 * @return int
		 */
        public int getColumnCount() {
          return columnNames.length;
        }
        
		/**
		 * Return row count
		 * @return int
		 */      
        public int getRowCount() {
          return data.length;
        }

        /**
         * Return column name
         * @param col - column index
         */
        public String getColumnName(int col) {
          return columnNames[col];
        }

        /**
         * Return the selected cell value
         * @param row - row index
         * @param col - column index
         * @return Object
         */
        public Object getValueAt(int row, int col) {
          return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         * @param c - column index
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
          return getValueAt(0, c).getClass();
        }

        /**
         * Return which cell is editable.
         * @param row - row index
         * @param col - column index
         */
        public boolean isCellEditable(int row, int col) {
        	switch(col){
        	case 0:
        		return false;
        	case 1:
        		return false;
        	case 2:
        		return false;
        	default:
        		return false;
        	}
        }
        
        /**
         * Alter cell's value
         * @param value - new value
         * @param row - row index
         * @param col - column index
         */
        public void setValueAt(Object value, int row, int col) {
          data[row][col] = value;
          fireTableCellUpdated(row, col);
        }


	
}
