/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.ui.Container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A light-weight list of actions.  Includes some utility methods that are handy for performing on 
 * groups of actions together.
 * @author shannah
 */
public class Actions implements Iterable<ActionNode> {
    private List<ActionNode> actions = new ArrayList<>();

    public void add(ActionNode... nodes) {
        for (ActionNode n : nodes) {
            actions.add(n);
        }
    }
    
    public void add(Iterable<ActionNode> actions) {
        if (actions == this) {
            return;
        }
        for (ActionNode n : actions) {
            this.actions.add(n);
        }
    }
    
    public Actions getEnabled(Entity entity) {
        Actions out = new Actions();
        for (ActionNode action : this) {
            if (action.isEnabled(entity)) {
                out.add(action);
            }
        }
        return out;
    }
    
    public int size() {
        return actions.size();
    }
    
    public boolean isEmpty() {
        return actions.isEmpty();
    }
    
    @Override
    public Iterator<ActionNode> iterator() {
        return actions.iterator();
    }
    
    public ActionNode[] toArray() {
        return actions.toArray(new ActionNode[actions.size()]);
    }
    
    public void addToContainer(Container cnt, Entity entity) {
        for (ActionNode n : this) {
            cnt.addComponent(n.createView(entity));
        }
    }
    
}