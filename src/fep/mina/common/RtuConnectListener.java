/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.mina.common;

/**
 *
 * @author luxiaochung
 */
public interface RtuConnectListener {
    public void rtuConnect(RtuConnectEvent event);
    public void rtuDisconnect(RtuDisconnectEvent event);
}
