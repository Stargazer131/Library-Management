/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultility;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author Hao
 */
public class Resizer {
    public static ImageIcon resizeImageIcon(ImageIcon imageIcon, int width, int height) {
        Image image = imageIcon.getImage(); // transform it 
        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // scale it the smooth way  
        return new ImageIcon(newImage);  // transform it back
    }
}
