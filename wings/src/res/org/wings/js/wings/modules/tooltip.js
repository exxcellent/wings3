/***************************************************************************************************
 * WINGS.TOOLTIP  --  contains: tooltip related functions 
 **************************************************************************************************/

// Create module namespace
wingS.namespace("tooltip");


wingS.tooltip.init = function(delay, duration, followMouse) {
    if (config && config.Delay && config.Duration && config.FollowMouse) {
    	config.Delay = delay;
		config.Duration = duration;
		config.FollowMouse = followMouse;
    }
};

