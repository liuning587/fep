/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.codec.protocol.gb;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author luxiaochung
 */
public class DataTypeA15 extends DataTypeDateBase {
    public DataTypeA15(Date date){
        super();
        setDate(date);
    }

    public DataTypeA15(String dateStr,String format) {
        super(dateStr,format);
    }


    public DataTypeA15(byte[] array){
        super();
        setArray(array,0);
    }

    public void setArray(byte[] array){
        setArray(array,0);
    }

    public final void setArray(byte[] array, int beginPosition){
        setArray(array, beginPosition, "MIHHDDMMYY");
    }

    public byte[] getArray(){
        return getArray("MIHHDDMMYY");
    }

    @Override
    public String toString(){
        if (this.isNull) return "";
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(this.getDate());
        return date;
    }
}
