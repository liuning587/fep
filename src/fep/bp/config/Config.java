/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author THINKPAD
 */
public class Config {
    private static InputStream inputStream = null;
    private static Properties p;
    @SuppressWarnings("CallToThreadDumpStack")
    public static void LoadProperties(String PropertyFile)
    {
        inputStream = Config.class.getClassLoader().getResourceAsStream(PropertyFile);
        p = new Properties();

        try
        {
            p.load(inputStream);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    public static String ReadByKey(String key)
    {
        return p.getProperty(key);
    }
}
