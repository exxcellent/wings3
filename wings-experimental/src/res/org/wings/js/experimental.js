wingS.component.splitPaneResized = function(sb, size) {
    wingS.request.sendEvent(null, true, true, sb.el.id, size);
}

