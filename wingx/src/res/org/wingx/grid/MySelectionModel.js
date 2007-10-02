/*
 * YUI Extensions
 * Copyright(c) 2006, Jack Slocum.
 * 
 * This code is licensed under BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php
 */

/**
 @class The default SelectionModel used by {@link YAHOO.ext.grid.Grid}. 
 It supports multiple selections and keyboard selection/navigation. <br><br>
 @constructor
 */
YAHOO.ext.grid.MyDefaultSelectionModel = function(){
    /** @private */
    this.selectedRows = [];
    /** @private */
    this.selectedRowIds = [];
    /** @private */
    this.lastSelectedRow = null;
    
    /** Fires when a row is selected or deselected - fireDirect sig: (this, row, isSelected)
     * @type YAHOO.util.CustomEvent 
     * @deprecated
     */
    this.onRowSelect = new YAHOO.util.CustomEvent('SelectionTable.rowSelected');
    /** Fires when the selection changes on the Grid - fireDirect sig: (this, selectedRows[], selectedRowIds[])
     * @type YAHOO.util.CustomEvent 
     * @deprecated
     */
    this.onSelectionChange = new YAHOO.util.CustomEvent('SelectionTable.selectionChanged');
   
    this.events = {
        'selectionchange' : this.onSelectionChange,
        'rowselect' : this.onRowSelect
    };
    
    this.locked = false;
};

YAHOO.ext.grid.MyDefaultSelectionModel.prototype = {
    /** @ignore Called by the grid automatically. Do not call directly. */
    init : function(grid){
        this.grid = grid;
        this.initEvents();
    },
    
    lock : function(){
        this.locked = true;
    },
    
    unlock : function(){
        this.locked = false;  
    },
    
    isLocked : function(){
        return this.locked;    
    },
    
    /** @ignore */
    initEvents : function(){
        if(this.grid.trackMouseOver){
        	this.grid.addListener("mouseover", this.handleOver, this, true);
        	this.grid.addListener("mouseout", this.handleOut, this, true);
        }
        this.grid.addListener("rowclick", this.rowClick, this, true);
        this.grid.addListener("keydown", this.keyDown, this, true);
    },
    
    addListener : YAHOO.ext.grid.Grid.prototype.addListener,
    removeListener : YAHOO.ext.grid.Grid.prototype.removeListener,
    fireEvent : YAHOO.ext.grid.Grid.prototype.fireEvent,
    
    /** @ignore Syncs selectedRows with the correct row by looking it up by id. 
      Used after a sort moves data around. */
    syncSelectionsToIds : function(){
        if(this.getCount() > 0){
            var ids = this.selectedRowIds.concat();
            this.clearSelections();
            this.selectRowsById(ids, true);
        }
    },
    
    /**
     * Set the selected rows by their ID(s). IDs must match what is returned by the DataModel getRowId(index).
     * @param {String/Array} id The id(s) to select 
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRowsById : function(id, keepExisting){
        var rows = this.grid.getRowsById(id);
        this.selectRows(rows, keepExisting);
    },
    
    /**
     * Gets the number of selected rows.
     * @return {Number}
     */
    getCount : function(){
        return this.selectedRows.length;
    },
    
    /**
     * Selects the first row in the grid.
     */
    selectFirstRow : function(){
        for(var j = 0; j < this.grid.rows.length; j++){
            if(this.isSelectable(this.grid.rows[j])){
            	this.focusRow(this.grid.rows[j]);
                this.setRowState(this.grid.rows[j], true);
                return;
            }
        }
    },
    
    /**
     * Selects the row immediately following the last selected row.
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections
     */
    selectNext : function(keepExisting){
        if(this.lastSelectedRow){
            for(var j = (this.lastSelectedRow.rowIndex+1); j < this.grid.rows.length; j++){
                var row = this.grid.rows[j];
                if(this.isSelectable(row)){
                    this.focusRow(row);
                    this.setRowState(row, true, keepExisting);
                    return;
                }
            }
        }
    },
    
    /**
     * Selects the row that precedes the last selected row.
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectPrevious : function(keepExisting){
        if(this.lastSelectedRow){
            for(var j = (this.lastSelectedRow.rowIndex-1); j >= 0; j--){
                var row = this.grid.rows[j];
                if(this.isSelectable(row)){
                    this.focusRow(row);
                    this.setRowState(row, true, keepExisting);
                    return;
                }
            }
        }
    },
    
    /**
     * Returns the selected rows.
     * @return {Array} Array of DOM row elements
     */
    getSelectedRows : function(){
        return this.selectedRows;
    },
    
    /**
     * Returns the selected row ids.
     * @return {Array} Array of String ids
     */
    getSelectedRowIds : function(){
        return this.selectedRowIds;
    },
    
    /**
     * Clears all selections.
     */
    clearSelections : function(){
        if(this.isLocked()) return;
        var oldSelections = this.selectedRows.concat();
        for(var j = 0; j < oldSelections.length; j++){
            this.setRowState(oldSelections[j], false);
        }
        this.selectedRows = [];
        this.selectedRowIds = [];
    },
    
        
    /**
     * Selects all rows.
     */
    selectAll : function(){
        if(this.isLocked()) return;
        for(var j = 0; j < this.grid.rows.length; j++){
            this.setRowState(this.grid.rows[j], true, true);
        }
	this.fireEvent('selectionchange', this );	
    },
    
    /**
     * Returns True if there is a selection.
     * @return {Boolean}
     */
    hasSelection : function(){
        return this.selectedRows.length > 0;
    },
    
    /**
     * Returns True if the specified row is selected.
     * @param {HTMLElement} row The row to check
     * @return {Boolean}
     */
    isSelected : function(row){
        return row && (row.selected === true || row.getAttribute('selected') == 'true');
    },
    
    /**
     * Returns True if the specified row is selectable.
     * @param {HTMLElement} row The row to check
     * @return {Boolean}
     */
    isSelectable : function(row){
        return row && row.getAttribute('selectable') != 'false';
    },
    
    /** @ignore */
    rowClick : function(grid, rowIndex, e){
        if(this.isLocked()) return;
        var row = grid.getRow(rowIndex);
        if(this.isSelectable(row)){
            if(e.shiftKey && this.lastSelectedRow){
                var lastIndex = this.lastSelectedRow.rowIndex;
                this.selectRange(this.lastSelectedRow, row, e.ctrlKey);
                this.lastSelectedRow = this.grid.el.dom.rows[lastIndex];
            }else{
                this.focusRow(row);
                var rowState = e.ctrlKey ? !this.isSelected(row) : true;
                this.setRowState(row, rowState, e.hasModifier());
            }
        }
	this.fireEvent('selectionchange', this );	
    },
    
    /**
     * Deprecated. Tries to focus the row and scroll it into view - Use grid.scrollTo or grid.getView().focusRow() instead.
     * @deprecated
     * @param {HTMLElement} row The row to focus
     */
    focusRow : function(row){
    	this.grid.view.focusRow(row);
    },

    /**
     * Selects a row.
     * @param {Number/HTMLElement} row The row or index of the row to select
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRow : function(row, keepExisting){
        this.setRowState(this.getRow(row), true, keepExisting);
    },
    
    /**
     * Selects multiple rows.
     * @param {Array} rows Array of the rows or indexes of the row to select
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRows : function(rows, keepExisting){
        if(!keepExisting){
            this.clearSelections();
        }
        for(var i = 0; i < rows.length; i++){
            this.selectRow(rows[i], true);
        }
    },
    
    /**
     * Deselects a row.
     * @param {Number/HTMLElement} row The row or index of the row to deselect
     */
    deselectRow : function(row){
        this.setRowState(this.getRow(row), false);
    },
    
    /** @ignore */
    getRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.rows[row];
        }
        return row;
    },
    
    /**
     * Selects a range of rows. All rows in between startRow and endRow are also selected.
     * @param {Number/HTMLElement} startRow The row or index of the first row in the range
     * @param {Number/HTMLElement} endRow The row or index of the last row in the range
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRange : function(startRow, endRow, keepExisting){
        startRow = this.getRow(startRow);
        endRow = this.getRow(endRow);
        this.setRangeState(startRow, endRow, true, keepExisting);
    },
    
    /**
     * Deselects a range of rows. All rows in between startRow and endRow are also deselected.
     * @param {Number/HTMLElement} startRow The row or index of the first row in the range
     * @param {Number/HTMLElement} endRow The row or index of the last row in the range
     */
    deselectRange : function(startRow, endRow){
        startRow = this.getRow(startRow);
        endRow = this.getRow(endRow);
        this.setRangeState(startRow, endRow, false, true);
    },
    
    /** @ignore */
    setRowStateFromChild : function(childEl, selected, keepExisting){
        var row = this.grid.getRowFromChild(childEl);
        this.setRowState(row, selected, keepExisting);
    },
    
    /** @ignore */
    setRangeState : function(startRow, endRow, selected, keepExisting){
        if(this.isLocked()) return;
        if(!keepExisting){
            this.clearSelections();
        }
        var curRow = startRow;
        while(curRow.rowIndex != endRow.rowIndex){
            this.setRowState(curRow, selected, true);
            curRow = (startRow.rowIndex < endRow.rowIndex ? 
                        this.grid.getRowAfter(curRow) : this.grid.getRowBefore(curRow))
        }
        this.setRowState(endRow, selected, true);
    },
    
    /** @ignore */
    setRowState : function(row, selected, keepExisting){
        if(this.isLocked()) return;
        if(this.isSelectable(row)){
            if(selected){
                if(!keepExisting){
                    this.clearSelections();
                }
                this.setRowClass(row, 'selected');
                row.selected = true;
                this.selectedRows.push(row);
                this.selectedRowIds.push(this.grid.dataModel.getRowId(row.rowIndex));
                this.lastSelectedRow = row;
            }else{
                this.setRowClass(row, '');
                row.selected = false;
                this._removeSelected(row);
            }
            this.fireEvent('rowselect', this, row, selected);
        }
    },

    /** @ignore */
    handleOver : function(e){
        var row = this.grid.getRowFromChild(e.getTarget());
        if(this.isSelectable(row) && !this.isSelected(row)){
            this.setRowClass(row, 'over');
        }
    },
    
    /** @ignore */
    handleOut : function(e){
        var row = this.grid.getRowFromChild(e.getTarget());
        if(this.isSelectable(row) && !this.isSelected(row)){
            this.setRowClass(row, '');
        }
    },
    
    /** @ignore */
    keyDown : function(e){
        if(e.browserEvent.keyCode == e.DOWN){
            this.selectNext(e.shiftKey);
            e.preventDefault();
        }else if(e.browserEvent.keyCode == e.UP){
            this.selectPrevious(e.shiftKey);
            e.preventDefault();
        }
    },

    /** @ignore */
    setRowClass : function(row, cssClass){
        if(this.isSelectable(row)){
            if(cssClass == 'selected'){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-over');
                YAHOO.util.Dom.addClass(row, 'ygrid-row-selected');
            }else if(cssClass == 'over'){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-selected');
                YAHOO.util.Dom.addClass(row, 'ygrid-row-over');
            }else if(cssClass == ''){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-selected');
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-over');
            }
        }
    },
    
    /** @ignore */
    _removeSelected : function(row){
        var sr = this.selectedRows;
        for (var i = 0; i < sr.length; i++) {
          if (sr[i] === row){
              this.selectedRows.splice(i, 1);
              this.selectedRowIds.splice(i, 1);
              return;
          }
        }
    }
};

/**
 @class Extends {@link YAHOO.ext.grid.DefaultSelectionModel} to allow only one row to be selected at a time. <br><br> 
 @extends YAHOO.ext.grid.DefaultSelectionModel
 @constructor
 */
YAHOO.ext.grid.MySingleSelectionModel = function(){
    YAHOO.ext.grid.MySingleSelectionModel.superclass.constructor.call(this);
};

YAHOO.extendX(YAHOO.ext.grid.MySingleSelectionModel, YAHOO.ext.grid.MyDefaultSelectionModel);

/** @ignore */
YAHOO.ext.grid.MySingleSelectionModel.prototype.setRowState = function(row, selected){
    YAHOO.ext.grid.MySingleSelectionModel.superclass.setRowState.call(this, row, selected, false);
};

/**
 @class Extends {@link YAHOO.ext.grid.DefaultSelectionModel} to disable row selection. <br><br> 
 @extends YAHOO.ext.grid.DefaultSelectionModel
 @constructor
 */
YAHOO.ext.grid.MyDisableSelectionModel = function(){
    YAHOO.ext.grid.MyDisableSelectionModel.superclass.constructor.call(this);
};

YAHOO.extendX(YAHOO.ext.grid.MyDisableSelectionModel, YAHOO.ext.grid.MyDefaultSelectionModel);

YAHOO.ext.grid.MyDisableSelectionModel.prototype.initEvents = function(){
};
