package flingball;
/**
 * Interface for the board state change listener
 */
interface StateChangeListener {

    /**
     * Notifies observers of state changes via listener callback
     */
    void notifyStateChange(String str);

}
