/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultility;

import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 *
 * @author Hao
 */
public class Formatter {
    // format date display
    public static String formatDateDisplay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
}
