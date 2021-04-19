/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.Slot;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ActionSource;
import com.codename1.ui.events.ComponentStateChangeEvent;

/**
 * A controller class that handles application logic for a view.  In most cases a FormController will be sufficient to
 * handle the logic for a form.  Some more complex views may require their own controllers as well, in which case you 
 * might use a ViewController to handle Action dispatch on the view.
 * 
 * == Controller Hierarchy
 * 
 * Controllers form a hierarchy similar to UI components.  E.g. A controller has a parent controller, and events that are not 
 * consumed by the child will propagate to the parent.  See {@link FormController} for a more details discussion of the Controller hierarchy.
 * @author shannah
 */
public class ViewController extends Controller {
    private Component view;
    private static final String KEY = "com.codename1.ui.controllers.ViewController";
    
    /**
     * Event that is fired when a "view" is set in a view controller.
     */
    public static class DidSetViewEvent extends ControllerEvent {
        private Component view;
        public DidSetViewEvent(ViewController source, Component view) {
            super(source);
            this.view = view;
        }
        
        public Controller getSource() {
            return (Controller)super.getSource();
        }
        
        public Component getView() {
            return view;
        }
        
        public <T extends Component> T getView(Class<T> asClass) {
            return (T)getView();
        }
    }
    
    /**
     * Event that is fired when a "view" is unset from a view controller.
     */
    public static class DidUnSetViewEvent extends ControllerEvent {
        private Component view;
        public DidUnSetViewEvent(ViewController source, Component view) {
            super(source);
            this.view = view;
        }
        
        public Controller getSource() {
            return (Controller)super.getSource();
        }
        
        public Component getView() {
            return view;
        }
        
        public <T extends Component> T getView(Class<T> asClass) {
            return (T)getView();
        }
    }
    
    private ActionListener viewListener = evt->{
        if (evt instanceof ControllerEvent) {
            dispatchEvent((ControllerEvent)evt);
        }
    };
    
    /**
     * Listener to be notified when view is initialized/deinitialized.
     */
    private ActionListener<ComponentStateChangeEvent> stateChangeListener = evt -> {
        if (evt.isInitialized()) {
            initController();
        } else {
            deinitialize();
        }
    };
    
    public ViewController(Controller parent) {
        super(parent);
    }
    
    /**
     * Sets the view associated with this controller.
     * @param view 
     */
    public void setView(Component view) {
        if (this.view != null) {
            if (this.view instanceof EventProducer) {
                ((EventProducer)this.view).getActionSupport().removeActionListener(viewListener);
            } else if (this.view instanceof ActionSource) {
                ((ActionSource)this.view).removeActionListener(viewListener);
            }
            this.view.removeStateChangeListener(stateChangeListener);
            this.view.putClientProperty(KEY, null);

            dispatchEvent(new DidUnSetViewEvent(this, this.view));
            
        }
        this.view = view;
        if (this.view != null) {
            this.view.addStateChangeListener(stateChangeListener);
            if (this.view instanceof EventProducer) {
                ((EventProducer)this.view).getActionSupport().addActionListener(viewListener);
            } else if (this.view instanceof ActionSource) {
                ((ActionSource)this.view).addActionListener(viewListener);
            }
            this.view.putClientProperty(KEY, this);

            activate(this.view);
            dispatchEvent(new DidSetViewEvent(this, this.view));
        }
    }


    /**
     * Activates a component.  This is called when the component is assigned to a controller.
     * The primary purpose of this is to activate all of the slots in the view.  Containers
     * are recursively activated.
     *
     * @param cmp The component to activate.
     */
    private static void activate(Component cmp) {
        if (isActivated(cmp)) return;
        cmp.putClientProperty(KEY_ACTIVATED, Boolean.TRUE);
        if (cmp instanceof Slot) {
            // We activate a slot first using the slot activation.
            // Then we'll propagate our activation call to the slot's
            // children,in case any children have been added.
            ((Slot)cmp).activate();
        }
        if (cmp instanceof Container) {
            Container cnt = (Container)cmp;
            for (Component child : cnt) {
                activate(child);
            }
        }
    }

    /**
     * Key for storing an activation flag in a view so that it is not activated twice.
     */
    private static String KEY_ACTIVATED = "$$ViewController$$Activated";

    /**
     * Checks if the given component has already been activated.
     * @param cmp The component to check for activation.
     * @return True if the component is already activated.
     */
    private static boolean isActivated(Component cmp) {
        Boolean activated = (Boolean)cmp.getClientProperty(KEY_ACTIVATED);
        return activated != null;
    }
    
    public static ViewController getViewController(Component cmp) {
        Component orig = cmp;
        ViewController ctrl = (ViewController)cmp.getClientProperty(KEY);
        if (ctrl != null) {
            return ctrl;
        }
        cmp = orig.getOwner();
        if (cmp != null) {
            ctrl =  getViewController(cmp);
            if (ctrl != null) {
                return ctrl;
            }
        }
        
        
        cmp = orig.getParent();
        if (cmp != null) {
            ctrl = getViewController(cmp);
            if (ctrl != null) {
                return ctrl;
            }
        }
        
        
        return null;
    }
    
    /**
     * Gets the view associated with this controller.
     * @return 
     */
    public Component getView() {
        return view;
    }
    
    
    /**
     * Callback called when the view is initialized (i.e. made visible)
     */
    public void initController() {
        
    }
    
    /**
     * Callback called when the view is deinitialized (i.e. removed from display hierarchy).
     */
    public void deinitialize() {
        
    }

}
