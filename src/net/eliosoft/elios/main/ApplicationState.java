package net.eliosoft.elios.main;

import java.util.ArrayList;

/**
 * This class represents the current state of the application. The default state
 * is ApplicationState.State.RUNNING.
 * 
 * @author acollign
 */
public class ApplicationState {

    /**
     * The available state.
     * 
     * @author acollign
     */
    enum State {
	RUNNING, SHUTTING_DOWN
    }

    /**
     * Listener that could be registered to the {@link ApplicationState}.
     * 
     * @author acollign
     */
    public interface Listener {
	void stateChanged(State oldState, State newState);
    }

    /**
     * The current {@link State}.
     */
    private State state = State.RUNNING;

    /**
     * The registered listeners.
     */
    private ArrayList<Listener> listeners = new ArrayList<ApplicationState.Listener>();

    /**
     * Changes the current {@link State}. Listeners are fired that the state has
     * changed.
     * 
     * @param newState
     *            the new {@link State}.
     */
    void changeState(State newState) {
	State oldState = state;
	this.state = newState;

	fireStateChanged(oldState, newState);
    }

    /**
     * Fires the change event to all {@link Listener}.
     * 
     * @param oldState
     *            the old {@link State}
     * @param newState
     *            the new {@link State}
     */
    private void fireStateChanged(State oldState, State newState) {
	for (Listener l : listeners) {
	    l.stateChanged(oldState, newState);
	}
    }

    /**
     * Registers a {@link Listener}.
     * 
     * @param l
     *            a {@link Listener}
     */
    void addListener(Listener l) {
	this.listeners.add(l);
    }
}
