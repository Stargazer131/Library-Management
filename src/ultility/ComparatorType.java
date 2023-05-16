/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultility;

import java.util.Comparator;


/**
 *
 * @author Hao
 */
public class ComparatorType {
    public static final Comparator<Object> INTERGER = (Object o1, Object o2) -> Integer.compare(Integer.parseInt(o1.toString()), Integer.parseInt(o2.toString()));
    public static final Comparator<Object> DATE = (Object o1, Object o2) -> Formatter.stringToDate(o1.toString()).compareTo(Formatter.stringToDate(o2.toString()));   
    public static final Comparator<Object> STRING = (Object o1, Object o2) -> o1.toString().compareTo(o2.toString());  
    public static final Comparator<Object> FLOAT = (Object o1, Object o2) -> Float.compare(Float.parseFloat(o1.toString()), Float.parseFloat(o2.toString()));
}
