/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.realinterface.mto;
import fep.bp.realinterface.IRealMessage;
/**
 *
 * @author Thinkpad
 */
public abstract class MessageTranObject implements IRealMessage{
   /* private int ID;
    public int getID(){
        return this.ID;
    }

    public void setID(int value){
        this.ID = value;
    }
*/
    @Override
    public abstract String toJson();

    public abstract MTOType getType();
    
    public abstract int getTaskType();
}
