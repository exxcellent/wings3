
// faking a namespace
if (!wingS) {
	var wingS = new Object();
}
else if (typeof wingS != "object") {
	throw new Error("wingS already exists and is not an object");
}

if (!wingS.XCalendar) {
	wingS.XCalendar = new Object();
}
else if (typeof wingS.XCalendar != "object") {
	throw new Error("wingS.XCalendar already exists and is not an object");
}

/**
 * XCalendar JavaScript Code; additionally to calendar.js but does
 * not belong to the original 'The DHTML Calendar' code.
 */

/**
 * Uses  Mihai Bazon calendar and Yahoo event functionality to implement a date picker.
 *
 * @param  {string}  formatterKey    A lookup key for the validaton method.
 * @param  {string}  inputfieldId    The HTML element id of the hidden input field
 * @param  {string}  textfieldId     The HTML element id of the SFormattedTextField
 * @param  {string}  editButtonId    The HTML element id of the button triggering the calendar
 * @param  {string}  resetButtonId   The HTML element id of the button triggering the calendar reset
 */
wingS.XCalendar = function (formatterKey, inputfieldId, textfieldId, editButtonId, resetButtonId, onUpdateCom) {
    this.formatterKey = formatterKey;
    this.inputfield = document.getElementById(inputfieldId);
    this.textfield = document.getElementById(textfieldId);
    this.editButton = document.getElementById(editButtonId);
    this.onUpdateCommit = onUpdateCom;
    if (resetButtonId)
        this.resetButton = document.getElementById(resetButtonId);

    this.calendar = Calendar.setup({
          inputField  : this.inputfield.id
        , textField   : this.textfield.id
        , ifFormat    : "%Y.%m.%d"
        , button      : this.editButton.id
        , showOthers  : true
        , electric    : false
        , onUpdate    : this.onCalUpdate
        , formatter   : this.formatterKey
        , onUpdateCommit : this.onUpdateCommit
     });

    if (this.resetButton) {
        YAHOO.util.Event.addListener(this.resetButton.id, "click", this.clearCalendar, this, true);
    }
}

wingS.XCalendar.prototype.onCalUpdate = function onCalUpdate(cal) {
    /* this.onCalUpdateCallback doesn't work as the this scope is lost. */
    xcalendar.onCalUpdate(cal.params.formatter, cal.params.textField, cal.date, this.onUpdateCommit, wingS.XCalendar.prototype.onCalUpdateCallback);
}

wingS.XCalendar.prototype.onCalUpdateCallback = function onCalUpdateCallback(result) {
    var elem = document.getElementById(result[0]);
    if (!elem)
        return; // dwr bug
    var data = result[1];
    elem.value = data;
    elem.style.color = '';
    if ( result[2] == 'true' ) {
        wingS.request.sendEvent( null, true, true, elem.id, elem.value );
    }
}

wingS.XCalendar.prototype.clearCalendar = function clearCalendar(ev) {
    this.textfield.value = '';
    this.inputfield.value = '';
    YAHOO.util.Event.preventDefault(ev);
    return false;
}